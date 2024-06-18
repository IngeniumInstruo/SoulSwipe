package com.example.soulswipe;

import com.google.firebase.Timestamp;

public class ConversationModel {
    private String user;
    private String lastMessage;
    private Timestamp timestamp;
    private String image;
    private CardModel matchedUser;
    private String uid;
    private String sender;
    private Boolean user1Viewed;
    private Boolean user2Viewed;

    public ConversationModel(String user, String lastMessage, Timestamp timestamp, String image, CardModel matchedUser, String uid, String sender, Boolean user1Viewed, Boolean user2Viewed) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.image = image;
        this.matchedUser = matchedUser;
        this.uid = uid;
        this.sender = sender;
        this.user1Viewed = user1Viewed;
        this.user2Viewed = user2Viewed;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUser() {
        return user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getImage() {
        return image;
    }

    public CardModel getMatchedUser() {
        return matchedUser;
    }

    public String getUid() {
        return uid;
    }

    public Boolean getUser1Viewed() {
        return user1Viewed;
    }

    public Boolean getUser2Viewed() {
        return user2Viewed;
    }

    public String getSender() {
        return sender;
    }

    public void setUser1Viewed(Boolean user1Viewed) {
        this.user1Viewed = user1Viewed;
    }

    public void setUser2Viewed(Boolean user2Viewed) {
        this.user2Viewed = user2Viewed;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
