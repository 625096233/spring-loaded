
package io.spring.loaded.member;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for classes that are added as members to existing user classes.
 *
 * @author Phillip Webb
 * @see AddedMembers
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AddedMember {

	/**
	 * @return the name of the member to add. NOTE: the actual field name may include
	 * an additional prefix to help ensure that it doesn't clash with any existing member.
	 */
	String value();

	/**
	 * @return the type of member
	 */
	MemberType type() default MemberType.STATIC;


}
