package com.example.rhymebyrhymeversion2.model;

import java.io.Serializable;

/**
 * Created by Amir on 08.07.2017.
 */

public class Poem implements Serializable {
    private String title;
    private String text;
    private int likes;
    private String id;
    private String uId;
    private String date;
    private boolean like;
    private String tags;
    private String author;




    public Poem(String uId, String id, String title, String text, int likes, String date, String tags, String author) {
        this.uId = uId;
        this.id = id;
        this.title = title;
        this.text = text;
        this.likes = likes;
        this.date = date;
        this.tags = tags;
        this.author = author;


    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public Poem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
