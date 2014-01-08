package io.spring.loaded.integrationtest.method.add;

import org.junit.runners.JUnit4;


public class AddStaticMethod001 {

	static {
		System.out.println("Hello");
		System.out.println(JUnit4.class);
	}


	public AddStaticMethod001() {
		System.out.println(getClass());
		System.out.println(AddStaticMethod001.class);
	}

}
