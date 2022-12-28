package ru.cft.shift.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsReader {
    private static final String portKey = "port";

    public static ServerSettings getSettings(){
        Properties properties = getProperties();
        ServerSettings settings = new ServerSettings();
        settings.port = Integer.parseInt(properties.getProperty(portKey));

        return settings;
    }

    private static Properties getProperties(){
        Properties properties;
        try (InputStream input = new FileInputStream("src/main/resources/prop.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
