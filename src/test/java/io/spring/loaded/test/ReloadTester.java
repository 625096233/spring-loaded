
package io.spring.loaded.test;

import java.io.IOException;
import java.net.URL;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class ReloadTester {

	private ClassLoader classLoader;

	public ReloadTester(Class<?>... classes) throws Exception {
		this.classLoader = new Dunno(ClassLoader.getSystemClassLoader());
		for (Class<?> classToLoad : classes) {
			Class<?> loadClass = classLoader.loadClass(classToLoad.getName());
			System.out.println(loadClass);
			loadClass.newInstance();
			System.out.println(loadClass.getClassLoader());
		}
	}

	public void replace(Class<?>... classes) {

		// Get the bytes
		// get the original 001 version of the class
		// hot replace the bytes (apply the rename filter before the replace happens)




		// FIXME
	}

	public void verify(Class<? extends Assertor> assertor) {
		// FIXME
	}

	private static class Dunno extends ClassLoader {

		public Dunno(ClassLoader systemClassLoader) {
			super(systemClassLoader);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve)
				throws ClassNotFoundException {
			try {
				URL resource = getResource(name.replace(".", "/") + ".class");
				if (name.endsWith("001")) {
					System.out.println(name);
					System.out.println(resource);
					ClassReader reader = new ClassReader(resource.openStream());
					ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES
							| ClassWriter.COMPUTE_MAXS);

					Remapper remapper = new Remapper() {
						@Override
						public String map(String type) {
							if (type.endsWith("001")) {
								System.out.println(">>"+type);
								return type.substring(0, type.length() - 3);
							}
							return type;
						}
					};
					RemappingClassAdapter adapter = new RemappingClassAdapter(writer,
							remapper);

					reader.accept(adapter, 0);
					byte[] byteArray = writer.toByteArray();
					try {
						if (name.endsWith("001")) {
							name = name.substring(0, name.length() - 3);
						}
						return defineClass(name, byteArray, 0, byteArray.length);
					} catch (Exception ex) {
						ex.printStackTrace();
						return super.loadClass(name, resolve);
					}
				}
				return super.loadClass(name, resolve);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}

	}
}
