package io.spring.loaded;

import java.util.Iterator;


public interface Reloader {

	void reload(Iterator<ReloadReplacement> replacements);


	/*
	 *

	  for each replacement {

	  	get the reloaded class from the origin

	  	use that to get the next number

	  	read the source, apply the adapters and write bytes

	  	set the reloaded class

	  }

	  fire listener



	 *
	 */

}
