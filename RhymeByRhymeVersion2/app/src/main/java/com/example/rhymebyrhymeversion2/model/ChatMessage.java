package com.example.rhymebyrhymeversion2.model;

import java.util.Date;

/**
 * Created by Amir on 14.12.2017.
 */

public class ChatMessage {

    private String messageText;
    private String whoSend;
    private String toWhomSend;
    private long messageTime;

    public ChatMessage(String messageText, String whoSend, String toWhomSend) {
        this.messageText = messageText;
        this.whoSend = whoSend;
        this.toWhomSend = toWhomSend;
        this.messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getWhoSend() {
        return whoSend;
    }

    public void setWhoSend(String whoSend) {
        this.whoSend = whoSend;
    }

    public String getToWhomSend() {
        return toWhomSend;
    }

    public void setToWhomSend(String toWhomSend) {
        this.toWhomSend = toWhomSend;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}