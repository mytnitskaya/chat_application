package ru.cft.shift.client;

import ru.cft.shift.common.ClientMessage;
import ru.cft.shift.common.JsonHelper;
import ru.cft.shift.common.ServerMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable{
    private ClientView client;
    private PrintWriter outMessage;
    private BufferedReader inMessage;
    private Socket clientSocket;
    public ServerHandler(Socket socket, ClientView client) {
        try {
            this.client = client;
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

    public void closeClient(){
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
    private void startListening(){
        try {
            while (true) {
                ServerMessage message = JsonHelper.tryParse(inMessage.readLine(), ServerMessage.class);
                client.addMessageToChat(message.message);
                if (message.isError){
                    client.setIsLogged(false);
                    closeClient();
                    return;
                }

                client.setUsersList(message.usersNames);
            }
        } catch (Exception e) {
        }
    }
}
