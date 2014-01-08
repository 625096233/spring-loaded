
package io.spring.loaded.methods;

import io.spring.loaded.member.AddedMember;
import io.spring.loaded.member.AddedMembers;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

/**
 * {@link AddedMember} used to track replaced methods.
 *
 * @author Phillip Webb
 */
@AddedMember("replacedMethods")
public class ReplacedMethods {

	private CallSite getMethodReplacement(String name, MethodType type) {
		// FIXME return the equivalent method in the replaced class
		// also fixup arguments
		// keep the callsite so that we can fix it up later
		MutableCallSite callSite;

		return null;
	}

	// FIXME on refresh callSite.setTarget(newTarget);

	public static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name,
			MethodType type) throws Throwable {
		ReplacedMethods replacedMethods = AddedMembers.get(caller, ReplacedMethods.class);
		return replacedMethods.getMethodReplacement(name, type);
	}

}
