package eu.linksmart.maven.example;

import org.junit.Test;

public class TestHelloMaven {
	
	public TestHelloMaven() {
		
	}
	
	@Test
	public void testHelloMaven() {
		HelloMaven maven = new HelloMaven();
		maven.sayHi();
	}

}
