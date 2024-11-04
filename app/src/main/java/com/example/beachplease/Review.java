package com.example.beachplease;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private String beachName;
    private Double rating;
    private Date date;
    private User author;
    private String comment;
    private List<String> tags;

    public Review(String beachName, Double rating, Date date, User author, String comments, ArrayList<String> tags) {
        this.beachName = beachName;
        this.rating = rating;
        this.date = date;
        this.author = author;
        this.comment = comments;
        this.tags = tags;
    }

    public String getBeachName () {
        return beachName;
    }

    public Double getRating() {
        return rating;
    }

    public Date getDate() {
        return date;
    }

    public User getAuthor() {
        return author;
    }

    public String getComments() {
        return comment;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
