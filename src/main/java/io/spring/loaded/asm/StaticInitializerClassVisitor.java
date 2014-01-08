
package io.spring.loaded.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

/**
 * {@link ClassVisitor} that can be used to add or extend a static initializer.
 *
 * @author Phillip Webb
 */
public abstract class StaticInitializerClassVisitor extends ClassVisitor {

	private static final String INIT_METHOD = "<clinit>";

	private static final String INIT_SIGNATURE = "()V";

	private boolean visited;

	public StaticInitializerClassVisitor(int api, ClassVisitor delegate) {
		super(api, delegate);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.visited = !needsStaticInitializer();
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (!visited && INIT_METHOD.equals(name)) {
			visited = true;
			return getStaticInitializerMethodVisitor(mv);
		}
		return mv;
	}

	@Override
	public void visitEnd() {
		if (!visited) {
			addStaticInitializer();
		}
		super.visitEnd();
	}

	private void addStaticInitializer() {
		MethodVisitor mv = super.visitMethod(ACC_STATIC, INIT_METHOD, INIT_SIGNATURE,
				null, null);
		mv = getStaticInitializerMethodVisitor(mv);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}

	protected boolean needsStaticInitializer() {
		return true;
	}

	protected abstract MethodVisitor getStaticInitializerMethodVisitor(
			MethodVisitor delegate);

}
