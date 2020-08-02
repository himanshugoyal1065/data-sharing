package com.himanshu.datasharing.config;

import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class MyApplicationPropertiesTestSuite {

    @Test
    public void testApplicationPropertiesReload() {
        MyApplicationProperties myApplicationProperties = new MyApplicationProperties();
        Configuration configuration = myApplicationProperties.getConfiguration();
        System.out.println(configuration.getString("key"));
        configuration.setProperty("key", "new_value");
        System.out.println(configuration.getString("key"));
    }
}
