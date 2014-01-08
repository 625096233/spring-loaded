
package io.spring.loaded.member;

import static org.objectweb.asm.Opcodes.*;
import io.spring.loaded.BytecodeAdapter;
import io.spring.loaded.asm.StaticInitializerClassVisitor;
import io.spring.loaded.util.Assert;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Utilities for working with {@link AddedMember}s.
 *
 * @author Phillip Webb
 */
public class AddedMembers {

	private static final String NAME_PREFIX = "___";

	/**
	 * Return a {@link BytecodeAdapter} to add a static field to hold the specified
	 * member.
	 *
	 * @param memberClass the member class to add
	 * @return an adapter to add the member bytecode
	 */
	public static BytecodeAdapter getAddMemberAdapter(final Class<?> memberClass) {
		return new BytecodeAdapter() {

			@Override
			public ClassVisitor appy(ClassVisitor delegate) {
				return new AddMemberFieldVisitor(memberClass, delegate);
			}
		};
	}

	/**
	 * Get a previously added member using a {@link Class}.
	 *
	 * @param source the source class.
	 * @param memberClass the member class to load
	 * @return the member
	 * @throws IllegalStateException if the member class cannot be located
	 */
	public static <T> T get(Class<?> source, Class<T> memberClass) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(memberClass, "MemberClass must not be null");
		return get(MethodHandles.publicLookup().in(source), memberClass);
	}

	/**
	 * Get a previously added member using a {@link MethodHandles.Lookup}.
	 *
	 * @param source the lookup source.
	 * @param memberClass the member class to load
	 * @return the member
	 * @throws IllegalStateException if the member class cannot be located
	 */
	public static <T> T get(MethodHandles.Lookup source, Class<T> memberClass) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(memberClass, "MemberClass must not be null");
		AddedMember annotation = getAnnotation(memberClass);
		try {
			MethodHandle staticField = source.findStaticGetter(source.lookupClass(),
					getName(annotation), memberClass);
			Assert.state(staticField != null,
					"Unable to find static field for @AddedMember");
			return (T) staticField.invoke();
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static String getName(AddedMember annotation) {
		return NAME_PREFIX + annotation.value();
	}

	private static AddedMember getAnnotation(Class<?> sourceClass) {
		AddedMember annotation = sourceClass.getAnnotation(AddedMember.class);
		Assert.state(annotation != null, sourceClass.getName()
				+ " is not annotated with @AddedMember");
		return annotation;
	}

	private static class AddMemberFieldVisitor extends StaticInitializerClassVisitor {

		private Class<?> memberClass;

		private AddedMember annotation;

		private String internalName;

		public AddMemberFieldVisitor(Class<?> memberClass, ClassVisitor delegate) {
			super(ASM4, delegate);
			this.memberClass = memberClass;
			this.annotation = getAnnotation(memberClass);
		}

		@Override
		public void visit(int version, int access, String name, String signature,
				String superName, String[] interfaces) {
			super.visit(version, access, name, signature, superName, interfaces);
			this.internalName = name;
		}

		@Override
		public void visitEnd() {
			addField();
			super.visitEnd();
		}

		private void addField() {
			cv.visitField(getAccess(this.annotation), getName(this.annotation),
					Type.getDescriptor(this.memberClass), null, null);
		}

		private int getAccess(AddedMember annotation) {
			switch (annotation.type()) {
				case STATIC:
					return (ACC_PUBLIC | ACC_STATIC);
				case STATIC_FINAL:
					return (ACC_PUBLIC | ACC_STATIC | ACC_FINAL);
			}
			throw new IllegalStateException("Unknown type " + annotation.type());
		}

		@Override
		protected boolean needsStaticInitializer() {
			return this.annotation.type() == MemberType.STATIC_FINAL;
		}

		@Override
		protected MethodVisitor getStaticInitializerMethodVisitor(MethodVisitor delegate) {
			return new MethodVisitor(ASM4, delegate) {

				@Override
				public void visitCode() {
					String memberClassInternalName = Type.getInternalName(memberClass);
					super.visitCode();
					super.visitTypeInsn(NEW, memberClassInternalName);
					super.visitInsn(DUP);
					super.visitMethodInsn(INVOKESPECIAL, memberClassInternalName,
							"<init>", "()V");
					super.visitFieldInsn(PUTSTATIC, internalName,
							getName(annotation), Type.getDescriptor(memberClass));
					super.visitMaxs(1, 0);
				}
			};
		}

	}

}
