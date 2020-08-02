package com.himanshu.datasharing;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="himanshu.goyal@navis.com">Himanshu Goyal</a>
 */
public class FileNameChecker {
    @Test
    public void testFileName() {
        String inPath = "Integration Tests - TLS - No Mule-23.log.txt";

        String[] fileName = inPath.split("\\\\");


        for (String s : fileName) {
            System.out.println(s);
        }

        System.out.println(fileName[fileName.length - 1]);
    }
}
