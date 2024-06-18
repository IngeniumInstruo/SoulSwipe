package com.example.soulswipe;

<<<<<<< HEAD
import java.io.Serializable;

public class CardModel implements Serializable {
=======
public class CardModel {
>>>>>>> origin/main
    private String title;
    private String image;
    private String uid;

    public CardModel(String title, String image, String uid) {
        this.title = title;
        this.image = image;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
