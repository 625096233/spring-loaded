package io.spring.loaded.member;


/**
 * Types of {@link AddedMember}.
 *
 * @author Phillip Webb
 */
public enum MemberType {

	/**
	 * The member is added as a non-final static field.
	 */
	STATIC,

	/**
	 * The member is added as a static final static field.
	 */
	STATIC_FINAL

}
