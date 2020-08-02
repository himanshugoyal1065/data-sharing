package com.himanshu.datasharing.kafka;

import org.apache.commons.configuration2.Configuration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class PropertyProverImpl implements IPropertyProvider {

    @Autowired
    private PropertiesConfigurationProvider configurationProvider;

    @Override
    public Properties get() {
        Configuration configuration = configurationProvider.get();
        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                configuration.getString(PropertiesEnum.BOOTSTRAP_SERVER.getValue()));
        return properties;
    }

    @Override
    public void accept(Map<String, String> inProperties) {
        Configuration configuration = configurationProvider.get();
        for (Map.Entry<String, String> entry : inProperties.entrySet()) {
            configuration.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
