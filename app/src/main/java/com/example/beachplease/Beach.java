package com.example.beachplease;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Beach {
    private String name;
    private Double longitude;
    private Double latitude;
    private String hours;
    private Double averageRating;
    private String picture;
    private List<String> reviews;
    private List<String> tags;
    private WeatherService forecast;
    private String blurb;

    public Beach(String name, Double longitude, Double latitude, String hours, Double averageRating, String picture, String blurb) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hours = hours;
        this.averageRating = averageRating;
        this.picture = picture;
        this.blurb = blurb;

        this.reviews = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() { return latitude; }

    public String getHours() {
        return hours;
    }

    public Double getAvgRating() {
        return averageRating;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public List<String> getTags() {return tags;}

    public WeatherService getForecast() {
        return forecast;
    }

    public String getBlurb() { return blurb; }

    public String getPicture() { return picture; }

    public void addReview(String reviewId) {
        reviews.add(reviewId);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void updateAvgRating(Double rating) {
        int reviewNum = reviews.size();
        double newAvg = ((averageRating * reviewNum) + rating)/(reviewNum + 1);
        averageRating = newAvg;
    }

    public void addBeachToFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("beaches");
        String key = name;
        database.child(key).setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("successful!");
            }
            else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

}
