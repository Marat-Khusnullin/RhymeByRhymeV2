package com.example.rhymebyrhymeversion2;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Amir on 08.07.2017.
 */

public class User {
    private String name;
    private String surname;
    private String email;
    private int year;
    private String link;
    private String description;
    private int poemCount;
    private int readersCount;
    private List poems;
    private int rating;

    public User() {
    }

    public User(String name, String surname, String email, int year, String link, String description, int poemCount, int rating) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.year = year;
        this.link = link;
        this.description = description;
        this.poemCount = poemCount;
        this.rating = rating;
    }

    public User(String name, String surname, String email, int year, String link, String description, int poemCount, int readersCount, int rating) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.year = year;
        this.link = link;
        this.description = description;
        this.poemCount = poemCount;
        this.readersCount = readersCount;
        this.rating = rating;
    }

    public User(String name, String surname, String email, int year, String link, String description) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.year = year;
        this.link = link;
        this.description = description;
        this.poemCount = 0;
        this.poems = new ArrayList<>();
        this.rating = 0;
    }

    public User(String name, String surname, String email, int year, String link, String description, List poems, int rating) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.year = year;
        this.link = link;
        this.description = description;
        poemCount = 0;
        readersCount = 0;
        this.poems = poems;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getYear() {
        return year;
    }

    public void setYear(byte year) {
        this.year = year;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getPoemCount() {
        return poemCount;
    }


    public List getPoems() {
        return poems;
    }

    public void setPoems(List poems) {
        this.poems = poems;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    public int getReadersCount() {
        return readersCount;
    }

    public void setReadersCount(int readersCount) {
        this.readersCount = readersCount;
    }
}
