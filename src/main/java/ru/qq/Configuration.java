package ru.qq;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Configuration {

    private static String USERNAME = "username";

    private static String HOSTNAME = "hostname";

    private static String ZIPPATH;

    public Configuration(String configPath) {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(configPath)) { // Используем FileInputStream
            insertValues(yaml.load(in));
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file: " + e.getMessage(), e);
        }
    }

    private static void insertValues(Map<String, String> values){
        USERNAME = values.get("username");
        HOSTNAME = values.get("hostname");
        ZIPPATH = values.get("zip-path");
    }

    public static String getZipPathStatic() {
        return ZIPPATH;
    }

    public String getUsername() {
        return USERNAME;
    }

    public String getHostname() {
        return HOSTNAME;
    }

    public String getZipPath() {
        return ZIPPATH;
    }

    public static String getZipDirectoryPath() {
        if(ZIPPATH.isEmpty()) return "";

        String[] parts = ZIPPATH.split("\\\\");
        StringBuilder der = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            der.append(parts[i]);
            if(i != parts.length - 2) der.append(File.separator);
        }



        return der.toString();
    }

    public static String getZipDirectoryPathInside() {
        return ZIPPATH.substring(0, ZIPPATH.lastIndexOf("."));
    }
}
