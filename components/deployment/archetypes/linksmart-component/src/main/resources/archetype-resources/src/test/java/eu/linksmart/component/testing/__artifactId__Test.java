package eu.linksmart.${artifactId}.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import org.apache.log4j.Logger;

public class ${artifactId}Test {

    private Logger mlogger = Logger.getLogger(${artifactId}Test.class.getName());

    @Test
    public void testSomething() {
        //TODO dummy assertion. Add your proper tests
        assertTrue(true);

    }
}