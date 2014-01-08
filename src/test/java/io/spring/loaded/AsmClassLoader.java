
package io.spring.loaded;

import io.spring.loaded.BytecodeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

public class AsmClassLoader extends ClassLoader {

	private boolean trace;

	private Map<String, List<BytecodeAdapter>> adapters = new HashMap<>();

	public void addAdapter(String className, BytecodeAdapter adapter) {
		List<BytecodeAdapter> adapterList = this.adapters.get(className);
		if (adapterList == null) {
			adapterList = new ArrayList<>();
			this.adapters.put(className, adapterList);
		}
		adapterList.add(adapter);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		try {
			List<BytecodeAdapter> adapterList = this.adapters.get(name);
			if (adapterList != null) {
				ClassReader reader = new ClassReader(getClassResource(name));
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES
						| ClassWriter.COMPUTE_MAXS);
				ClassVisitor visitor = getVisitor(writer, adapterList);
				reader.accept(visitor, 0);
				byte[] bytes = writer.toByteArray();
				return defineClass(name, bytes, 0, bytes.length);
			}
			return super.loadClass(name, resolve);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private ClassVisitor getVisitor(ClassWriter writer, List<BytecodeAdapter> adapterList) {
		ClassVisitor visitor = writer;
		if (trace) {
			visitor = new TraceClassVisitor(visitor, new PrintWriter(System.out));
		}
		for (BytecodeAdapter adapter : adapterList) {
			visitor = adapter.appy(visitor);
		}
		return visitor;
	}

	private InputStream getClassResource(String name) {
		return getResourceAsStream(name.replace(".", "/") + ".class");
	}

}
