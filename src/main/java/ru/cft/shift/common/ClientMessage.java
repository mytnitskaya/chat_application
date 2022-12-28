package ru.cft.shift.common;

import java.util.Date;

public class ClientMessage {
    public ClientMessage(){

    }
    public ClientMessage(String userName, String message){
        this.userName = userName;
        this.message = message;
        this.date = new Date();
    }

    public Date date;
    public String userName;
    public String message;
    public boolean isExit;

}
