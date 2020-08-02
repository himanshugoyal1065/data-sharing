package com.himanshu.datasharing.kafka;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public enum PropertiesEnum {

    BOOTSTRAP_SERVER("bootstrapServer");

    PropertiesEnum(String inValue) {
        value = inValue;
    }

    final String value;

    public String getValue() {
        return value;
    }
}
