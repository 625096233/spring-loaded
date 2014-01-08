
package io.spring.loaded.member;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import io.spring.loaded.AsmClassLoader;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Tests for {@link AddedMembers}.
 *
 * @author Phillip Webb
 */
public class AddedMembersTest {

	@Test
	public void staticMember() throws Exception {
		AsmClassLoader classLoader = new AsmClassLoader();
		classLoader.addAdapter(SourceClass.class.getName(),
				AddedMembers.getAddMemberAdapter(StaticMember.class));
		Class<?> loadedClass = classLoader.loadClass(SourceClass.class.getName());
		Field field = loadedClass.getDeclaredField("___example");
		assertEquals(field.getType(), StaticMember.class);
		assertThat(Modifier.isPublic(field.getModifiers()), equalTo(true));
		assertThat(Modifier.isStatic(field.getModifiers()), equalTo(true));
		assertThat(Modifier.isFinal(field.getModifiers()), equalTo(false));
		assertThat(AddedMembers.get(loadedClass, StaticMember.class), nullValue());
		assertThat(field.get(null), nullValue());
	}

	@Test
	public void staticFinal() throws Exception {
		AsmClassLoader classLoader = new AsmClassLoader();
		classLoader.addAdapter(SourceClass.class.getName(),
				AddedMembers.getAddMemberAdapter(StaticFinalMember.class));
		Class<?> loadedClass = classLoader.loadClass(SourceClass.class.getName());
		Field field = loadedClass.getDeclaredField("___example");
		assertEquals(field.getType(), StaticFinalMember.class);
		assertThat(Modifier.isPublic(field.getModifiers()), equalTo(true));
		assertThat(Modifier.isStatic(field.getModifiers()), equalTo(true));
		assertThat(Modifier.isFinal(field.getModifiers()), equalTo(true));
		assertThat(AddedMembers.get(loadedClass, StaticFinalMember.class), instanceOf(StaticFinalMember.class));
		assertThat(field.get(null), instanceOf(StaticFinalMember.class));
	}

	public static class SourceClass {

		public static MethodHandles.Lookup lookup() {
			return MethodHandles.lookup();
		}

	}

	/**
	 * Example normal added member
	 */
	@AddedMember("example")
	public static class StaticMember {
	}

	/**
	 * Example normal added member
	 */
	@AddedMember(value = "example", type = MemberType.STATIC_FINAL)
	public static class StaticFinalMember {
	}

}
