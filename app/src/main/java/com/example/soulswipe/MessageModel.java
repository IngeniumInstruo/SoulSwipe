package com.example.soulswipe;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String sender;
    private String receiver;
    private String message;
    private Timestamp timestamp;
    private String conversationId;

    public MessageModel(String sender, String receiver, String message, Timestamp timestamp, String conversationId) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getConversationId() {
        return conversationId;
    }
}
