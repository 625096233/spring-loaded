
package io.spring.loaded;

import org.objectweb.asm.ClassVisitor;

public interface BytecodeAdapter {

	public static final BytecodeAdapter NONE = new BytecodeAdapter() {
		@Override
		public ClassVisitor appy(ClassVisitor delegate) {
			return delegate;
		}
	};

	ClassVisitor appy(ClassVisitor delegate);

}
