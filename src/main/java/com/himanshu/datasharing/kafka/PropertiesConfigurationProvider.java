package com.himanshu.datasharing.kafka;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class PropertiesConfigurationProvider implements Supplier<Configuration> {
    @Override
    public Configuration get() {
        Parameters params = new Parameters();
        // Read data from this file
        File propertiesFile = new File("application.properties");

        ReloadingFileBasedConfigurationBuilder builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.fileBased().setFile(propertiesFile));
        builder.setAutoSave(true);
        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                null, 5000, TimeUnit.MILLISECONDS);
        trigger.start();
        try {
            return (Configuration) builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
