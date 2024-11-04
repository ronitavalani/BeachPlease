package com.example.beachplease;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private String beachName;
    private Double rating;
    private Date date;
    private String author;
    private String comment;
    private List<String> tags;

    // No-argument constructor required for Firebase
    public Review() {
    }

    // Full constructor
    public Review(String beachName, Double rating, Date date, String author, String comment, ArrayList<String> tags) {
        this.beachName = beachName;
        this.rating = rating;
        this.date = date;
        this.author = author;
        this.comment = comment;
        this.tags = tags;
    }

    // Getters and setters
    public String getBeachName() {
        return beachName;
    }

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
