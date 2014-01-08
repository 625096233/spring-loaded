package io.spring.loaded;

import java.io.File;

/**
 * Provides access to bytecode that could be reloaded in the future.
 *
 * @author Phillip Webb
 */
public interface ReloadableBytecode {

	File getSourceFile();

	byte[] getBytes();

}
