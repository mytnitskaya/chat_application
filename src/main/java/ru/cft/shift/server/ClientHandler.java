package ru.cft.shift.server;

import ru.cft.shift.common.ClientMessage;
import ru.cft.shift.common.JsonHelper;
import ru.cft.shift.common.ServerMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Server server;
    private PrintWriter outMessage;
    private BufferedReader inMessage;
    private Socket clientSocket;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String userName;
    public boolean isLogged;

    @Override
    public void run() {
        try {
            var success = tryLogin();
            if (!success) {
                return;
            }
            startListening();
        } finally {
            close();
        }
    }

    public void sendMessage(ServerMessage message) {
        try {
            outMessage.println(JsonHelper.getJson(message));
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        server.removeClient(this);
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isLogged) {
            isLogged = false;
            String exitMessageText = userName + " has left chat..";
            server.sendMessageToAllClients(new ServerMessage(server.getUsersNames(), exitMessageText));
        }
    }

    private boolean tryLogin() {
        userName = getUserNameFromClient();
        boolean isNameValid = !(isUserNameAlreadyExist(userName) || userName.isBlank());
        if (isNameValid) {
            isLogged = true;
            String joinedMessageText = userName + " has joined the chat!";
            ServerMessage welcomeMessage = new ServerMessage(server.getUsersNames(), joinedMessageText);
            server.sendMessageToAllClients(welcomeMessage);
        } else {
            String errorMessageText = "Name - " + userName + " already in use, try login with other name";
            ServerMessage errorMessage = ServerMessage.createErrorMessage(errorMessageText);
            sendMessage(errorMessage);
            server.removeClient(this);
        }
        return isNameValid;
    }

    private String getUserNameFromClient() {
        ClientMessage userNameMessage = getNextMessage();
        return userNameMessage != null ? userNameMessage.userName : null;
    }

    private ClientMessage getNextMessage() {
        try {
            return JsonHelper.tryParse(inMessage.readLine(), ClientMessage.class);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isUserNameAlreadyExist(String userName) {
        return server.hasAnyClientWithName(userName);
    }

    private void startListening() {
        while (true) {
            ClientMessage clientMessage = getNextMessage();

            if (clientMessage == null) {
                continue;
            }
            if (clientMessage.isExit) {
                close();
                break;
            }
            ServerMessage serverMessage = new ServerMessage(server.getUsersNames(), clientMessage);
            server.sendMessageToAllClients(serverMessage);
        }
    }
}
