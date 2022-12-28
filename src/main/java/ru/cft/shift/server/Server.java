package ru.cft.shift.server;

import ru.cft.shift.common.ServerMessage;
import ru.cft.shift.common.ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {
    private List<ClientHandler> clients = new ArrayList<>();

    public void start(ServerSettings settings) {
        try (ServerSocket serverSocket = new ServerSocket(settings.port)) {
            System.out.println("Server started");
            while (!serverSocket.isClosed()) {
                var clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            for (var client : clients) {
                client.close();
            }
            System.out.println("Server stopped");
        }
    }

    public void sendMessageToAllClients(ServerMessage message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public boolean hasAnyClientWithName(String name) {
        return clients.stream().anyMatch(x -> x.isLogged && Objects.equals(x.userName, name));
    }

    public List<String> getUsersNames() {
        return clients.stream().map(x -> x.userName).toList();
    }

}
