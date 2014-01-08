package io.spring.loaded.methods;

import io.spring.loaded.BytecodeAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;


public class ReplaceMethodsBytecodeAdapter implements BytecodeAdapter {

	@Override
	public ClassVisitor appy(ClassVisitor delegate) {
		delegate = new RedirectToReloadedMethodsClassVisitor(delegate);
		return delegate;
	}

	private static class RedirectToReloadedMethodsClassVisitor extends ClassVisitor {

		public RedirectToReloadedMethodsClassVisitor(ClassVisitor delegate) {
			super(Opcodes.ASM4, delegate);
		}

	}

}
