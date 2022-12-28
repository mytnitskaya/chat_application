package ru.cft.shift.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServerMessage {
    public ServerMessage() {

    }
    public ServerMessage(List<String> usersNames){
        this.usersNames = usersNames;
    }
    public ServerMessage(List<String> usersNames, String message) {
        this(usersNames);
        this.message = message + System.lineSeparator();
    }
    public ServerMessage(List<String> usersNames, ClientMessage clientMessage){
        this(usersNames);
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        this.message = df.format(clientMessage.date) + "  " + clientMessage.userName + ": " + clientMessage.message
                + System.lineSeparator();
    }

    public String message;
    public List<String> usersNames;
    public boolean isError;

    public static ServerMessage createErrorMessage(String message){
        ServerMessage errorMessage = new ServerMessage();
        errorMessage.message = message + System.lineSeparator();
        errorMessage.isError = true;

        return errorMessage;
    }
}
