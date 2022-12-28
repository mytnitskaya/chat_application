package ru.cft.shift.client;

import ru.cft.shift.common.ClientMessage;
import ru.cft.shift.common.ServerMessage;
import ru.cft.shift.common.ServerSettings;

import java.net.Socket;

public class ClientModel {
    private String serverHost;
    private final int serverPort;
    private ServerHandler server;
    private final ClientView clientView;
    private String clientName;

    public ClientModel(ClientView clientView, ServerSettings settings) {
        this.clientView = clientView;
        this.serverPort = settings.port;
    }

    public void onLoginPress() {
        String inputHost = clientView.getInputHost();
        String inputName = clientView.getInputName();

        if (inputHost.isEmpty() || inputName.isEmpty()) {
            String errorLoginMessageText = "You did not enter a host or name. "
                    + "Until you enter the correct host and name, you will not be able to join the chat."
                    + System.lineSeparator();
            addMessageToChat(errorLoginMessageText);
            return;
        }
        serverHost = inputHost;

        var success = InitSocket();
        if (!success) {
            String failedConnectionMessageText = "Failed to connect to the specified host. "
                    + "Check if the host is correct and try to login again."
                    + System.lineSeparator();
            addMessageToChat(failedConnectionMessageText);
            return;
        }

        clientView.clearChatTextArea();
        new Thread(server).start();
        clientName = inputName;
        sendUserNameToServer();
        clientView.setIsLoggedState(true);
    }

    public void onSendPress() {
        String inputMessage = clientView.getInputMessage();
        if (!inputMessage.isEmpty()) {
            var message = new ClientMessage(clientName, inputMessage);
            server.sendMessage(message);
            clientView.clearInputMessage();
        }
    }

    public void onClose() {
        server.closeClient();
    }

    public void onMessageReceived(ServerMessage message) {
        addMessageToChat(message.message);
        clientView.setUsersList(message.usersNames);
    }

    public void onErrorReceived(String message) {
        addMessageToChat(message);
        clientView.setIsLoggedState(false);
    }

    private boolean InitSocket() {
        try {
            Socket clientSocket = new Socket(serverHost, serverPort);
            server = new ServerHandler(this, clientSocket);
            return clientSocket.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    private void sendUserNameToServer() {
        var initMessage = new ClientMessage();
        initMessage.userName = clientName;
        server.sendMessage(initMessage);
    }

    private void addMessageToChat(String message) {
        clientView.addMessageToChat(message);
    }
}
