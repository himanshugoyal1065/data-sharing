package com.himanshu.datasharing.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public interface IPropertyProvider extends Supplier<Properties>, Consumer<Map<String, String>> {
}
