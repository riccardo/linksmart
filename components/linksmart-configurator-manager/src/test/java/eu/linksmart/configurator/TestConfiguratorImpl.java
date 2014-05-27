
package eu.linksmart.configurator;
import eu.linksmart.configurator.impl.ConfiguratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 14.02.14
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class TestConfiguratorImpl {

    private ConfigurationAdmin confAdmin;
    private Configuration configuration;
    private Dictionary<String, Object> dictionary;
    private String PID = "X-222-YY";
    private String id_1 = "1";
    private String id_2 = "2";

    @Before
    public void setup() throws IOException, InvalidSyntaxException {

        dictionary = new Hashtable<>();
        dictionary.put(id_1,new String("fuu-1"));
        dictionary.put(id_2,new String("fuu-2"));

        //Configuration[] configs = cm.listConfigurations("(service.pid=*)");

        confAdmin = mock(org.osgi.service.cm.ConfigurationAdmin.class);
        configuration = mock(org.osgi.service.cm.Configuration.class);

        when(configuration.getProperties()).thenReturn(dictionary);
        when(configuration.getPid()).thenReturn(PID);
        when(confAdmin.getConfiguration(id_1)).thenReturn(configuration);
        when(confAdmin.listConfigurations("(service.pid=*)")).thenReturn(new Configuration[]{configuration});
        //Configuration c = cm.getConfiguration(id);
        //Configuration conf = new Con;
        //when(confAdmin.getConfiguration("1")).thenReturn(conf));
    }
    @Test
    public void testConfiguratorInitalization(){

        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        assertNotNull(configurator.getConfigAdmin());
    }
    @Test
    public void testConfigure(){
        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        configurator.configure(id_1,dictionary);
    }
    @Test
    public void testConfigure2(){
        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        String key = new String("FUU-XX");
        String value = new String("10");
        configurator.configure(id_1, key,value);
    }
    @Test
    public void testGetConfiguration(){
        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        Dictionary d = configurator.getConfiguration(id_1);
        assertEquals(dictionary, d);
    }
    @Test
    public void testGetAvailableConfigurations(){
        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        configurator.configure(id_1,dictionary);
        String[] configs = configurator.getAvailableConfigurations();
        assertTrue(configs.length>0);
    }
    @Test
    public void testGetAvailableConfigurations2(){
        ConfiguratorImpl configurator = new ConfiguratorImpl();
        configurator.setConfigAdmin(confAdmin);
        String key = new String("FUU-XX");
        String value = new String("10");
        configurator.configure(id_1, key,value);
        String[] configs = configurator.getAvailableConfigurations();
        assertTrue(configs.length>0);
    }
}
