
package io.spring.loaded;

import java.security.ProtectionDomain;

public interface ReloadableBytecodeProvider {

	ReloadableBytecode getReloadableBytecode(ProtectionDomain protectionDomain,
			String internalClassName);

}
