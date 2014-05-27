package eu.linksmart.maven.examples.impl;

import eu.linksmart.maven.examples.IExample;

public class ExampleImpl implements IExample {
	
	public ExampleImpl() {
		
	}
	
	public void notify(String message) {
		System.out.println("notify: " + message);
	}

}
