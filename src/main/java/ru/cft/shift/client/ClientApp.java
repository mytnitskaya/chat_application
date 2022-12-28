package ru.cft.shift.client;

import ru.cft.shift.common.ServerSettings;
import ru.cft.shift.common.SettingsReader;

public class ClientApp {
    public static void main(String[] args) {
        ClientView client = new ClientView();
        ServerSettings settings = SettingsReader.getSettings();
        client.start(settings);
    }
}
