
package io.spring.loaded.integrationtest.method.add;

import io.spring.loaded.test.ReloadTester;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

public class AddMethodTests {

	@Test
	public void addStaticMethod() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
		for (URL url : classLoader.getURLs()) {
			System.out.println(url);
		}

		ReloadTester tester = new ReloadTester(AddStaticMethod001.class);
		tester.replace(AddStaticMethod002.class);
		tester.verify(AddStaticMethodAssertor.class);
	}

}
