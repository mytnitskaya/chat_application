package ru.cft.shift.server;

import ru.cft.shift.common.ServerSettings;
import ru.cft.shift.common.SettingsReader;

public class ServerApp {
    public static void main(String[] args) {
        Server server = new Server();
        ServerSettings settings = SettingsReader.getSettings();
        server.start(settings);
    }
}
