package io.spring.loaded;


public interface ReloadReplacement {

	public Class<?> getSourceClass();
	
	public byte[] getReplacementBytes();
	
}
