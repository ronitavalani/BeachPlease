package com.example.beachplease;

import java.util.Date;

public class Review {
    private String beachName;
    private Double rating;
    private Date date;
    private User author;
    private String comment;

    public Review(String beachName, Double rating, Date date, User author, String comments) {
        this.beachName = beachName;
        this.rating = rating;
        this.date = date;
        this.author = author;
        this.comment = comments;
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
