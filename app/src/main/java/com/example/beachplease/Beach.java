package com.example.beachplease;

import java.util.List;

public class Beach {
    private String name;
    private String location;
    private String hours;
    private Float averageRating;
    private List<Review> reviews;
    private WeatherService forecast;

    public Beach() {
        //connect with the database somehow
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getHours() {
        return hours;
    }

    public Float getAvgRating() {
        return averageRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public WeatherService getForecast() {
        return forecast;
    }

    public void updateAvgRating(Double rating) {
        int reviewNum = reviews.size();
        //take this new review rating and update the average

    }

}
