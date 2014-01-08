
package io.spring.loaded.agent;

import io.spring.loaded.BytecodeAdapter;
import io.spring.loaded.ReloadReplacement;
import io.spring.loaded.ReloadableBytecode;
import io.spring.loaded.ReloadableBytecodeProvider;
import io.spring.loaded.Reloader;
import io.spring.loaded.monitor.FileMonitor;
import io.spring.loaded.monitor.FilesChangedListener;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author Phillip Webb
 */
public class ReloadClassTransformer implements ClassFileTransformer {

	private static final int CLASS_WRITER_FLAGS = (ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

	private static final int CLASS_READER_FLAGS = 0;

	private final FileMonitor fileMonitor;

	private final ConcurrentMap<File, List<Monitored>> monitoredFiles;

	private final ReloadableBytecodeProvider reloadableBytecodeProvider;

	private final BytecodeAdapter reloadableBytecodeAdapter;

	private final BytecodeAdapter retainedBytecodeAdapter;

	private final Reloader reloader;

	public ReloadClassTransformer(FileMonitor fileMonitor,
			ReloadableBytecodeProvider reloadableBytecodeProvider,
			BytecodeAdapter reloadableBytecodeAdapter,
			BytecodeAdapter retainedBytecodeAdapter, Reloader reloader) {
		this.fileMonitor = fileMonitor;
		this.reloadableBytecodeProvider = reloadableBytecodeProvider;
		this.reloadableBytecodeAdapter = reloadableBytecodeAdapter;
		this.retainedBytecodeAdapter = retainedBytecodeAdapter;
		this.reloader = reloader;
		this.monitoredFiles = new ConcurrentHashMap<>();
		addFileChangeListener(fileMonitor);
	}

	private void addFileChangeListener(FileMonitor fileMonitor) {
		fileMonitor.addFilesChangedListener(new FilesChangedListener() {
			@Override
			public void onFilesChanged(List<File> files) {
				reload(files);
			}
		});
	}

	@Override
	public byte[] transform(ClassLoader loader, String internalClassName,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(CLASS_WRITER_FLAGS);

		ReloadableBytecode reloadableBytecode = this.reloadableBytecodeProvider.getReloadableBytecode(
				protectionDomain, internalClassName);

		ClassVisitor visitor;
		if (reloadableBytecode != null) {
			Monitored monitored = new Monitored(loader,
					internalClassName, reloadableBytecode);
			monitorForReload(monitored);
			visitor = this.reloadableBytecodeAdapter.appy(writer);
		} else {
			visitor = this.retainedBytecodeAdapter.appy(writer);
		}

		reader.accept(visitor, CLASS_READER_FLAGS);
		return writer.toByteArray();
	}

	private void monitorForReload(Monitored monitored) {
		File file = monitored.getFile();
		if (!this.monitoredFiles.containsKey(file)) {
			this.monitoredFiles.putIfAbsent(file, new ArrayList<Monitored>(1));
		}
		List<Monitored> monitoredForFile = this.monitoredFiles.get(file);
		synchronized (monitoredForFile) {
			monitoredForFile.add(monitored);
		}
		this.fileMonitor.monitor(file);
	}

	protected void reload(final List<File> files) {
		this.reloader.reload(new ReloadReplacementIterator(files));
	}


	private class ReloadReplacementIterator implements Iterator<ReloadReplacement> {

		private Iterator<File> fileIterator;

		private Iterator<Monitored> monitoredIterator = Collections.emptyIterator();

		public ReloadReplacementIterator(List<File> files) {
			this.fileIterator = files.iterator();
		}

		@Override
		public boolean hasNext() {
			if (monitoredIterator.hasNext()) {
				return true;
			}
			if (!fileIterator.hasNext()) {
				return false;
			}
			List<Monitored> monitoredForFile = monitoredFiles.get(fileIterator.next());
			if (monitoredForFile != null) {
				this.monitoredIterator = monitoredForFile.iterator();
			}
			return hasNext();
		}

		@Override
		public ReloadReplacement next() {
			return monitoredIterator.next().asReloadReplacement();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final class Monitored {

		private final ClassLoader loader;

		private final String internalClassName;

		private final ReloadableBytecode reloadableBytecode;

		public Monitored(ClassLoader loader, String internalClassName,
				ReloadableBytecode reloadableBytecode) {
			this.loader = loader;
			this.internalClassName = internalClassName;
			this.reloadableBytecode = reloadableBytecode;
		}

		public File getFile() {
			return this.reloadableBytecode.getSourceFile();
		}

		public ReloadReplacement asReloadReplacement() {
			// FIXME
			return null;
		}
	}
}
