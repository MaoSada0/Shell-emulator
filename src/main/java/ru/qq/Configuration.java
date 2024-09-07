package ru.qq;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Configuration {

    private static String USERNAME;

    private static String HOSTNAME;

    private static String ZIPPATH;

    public Configuration(String configPath) {
        Yaml yaml = new Yaml();
        try (InputStream in = (Configuration.class).getClassLoader().getResourceAsStream(configPath)) {
            insertValues(yaml.load(in));
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file: " + e.getMessage());
        }
    }

    private static void insertValues(Map<String, String> values){
        USERNAME = values.get("username");
        HOSTNAME = values.get("hostname");
        ZIPPATH = values.get("zip-path");
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
}
