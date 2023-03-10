package ru.cft.shift.client;

import ru.cft.shift.common.ClientMessage;
import ru.cft.shift.common.JsonHelper;
import ru.cft.shift.common.ServerMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private ClientModel model;
    private PrintWriter outMessage;
    private BufferedReader inMessage;
    private Socket clientSocket;

    public ServerHandler(ClientModel model, Socket socket) {
        try {
            this.model = model;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startListening();
    }

    public void sendMessage(ClientMessage message) {
        String messageJson = JsonHelper.getJson(message);
        outMessage.println(messageJson);
        outMessage.flush();
    }

    public void closeClient() {
        try {
            ClientMessage message = new ClientMessage();
            message.isExit = true;
            sendMessage(message);
            outMessage.flush();
            outMessage.close();
            inMessage.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() {
        try {
            while (clientSocket.isConnected()) {
                ServerMessage message = JsonHelper.tryParse(inMessage.readLine(), ServerMessage.class);

                if (message == null) {
                    continue;
                }

                if (message.isError) {
                    onErrorFromServer(message.message);
                    return;
                }

                model.onMessageReceived(message);
            }
        } catch (Exception e) {
            onErrorFromServer(e.getMessage());
        }
    }

    private void onErrorFromServer(String message) {
        model.onErrorReceived(message);
        closeClient();
    }
}
