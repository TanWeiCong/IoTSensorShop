package com.example.iotsensorshop.models;

import java.util.Date;

public class ChatModel {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatModel(String s, String email) {
    }

    public ChatModel(String messageText, String messageUser, long messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
