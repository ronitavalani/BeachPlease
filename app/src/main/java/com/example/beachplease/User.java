package com.example.beachplease;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String password;
    private List<Review> reviews;

    public User(String username, String email, String password, List<Review> reviews) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.reviews = reviews;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
