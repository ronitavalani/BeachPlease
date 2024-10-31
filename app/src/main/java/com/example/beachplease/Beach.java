package com.example.beachplease;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;


public class Beach implements Parcelable {
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

    // Default constructor needed for Firebase
    public Beach() {}

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  
    public String getBlurb() { return blurb; }
    public void setBlurb(String blurb) { this.blurb = blurb; }

    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<String> getReviews() {
        return reviews;
    }

    public List<String> getTags() {return tags;}

    public WeatherService getForecast() {
        return forecast;
    }

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

    // Parcelable implementation
    protected Beach(Parcel in) {
        name = in.readString();
        blurb = in.readString();
        hours = in.readString();
        avgRating = (in.readByte() == 0) ? null : in.readDouble();
        latitude = (in.readByte() == 0) ? null : in.readDouble();
        longitude = (in.readByte() == 0) ? null : in.readDouble();
    }

    public static final Creator<Beach> CREATOR = new Creator<Beach>() {
        @Override
        public Beach createFromParcel(Parcel in) {
            return new Beach(in);
        }

        @Override
        public Beach[] newArray(int size) {
            return new Beach[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(blurb);
        dest.writeString(hours);
        if (avgRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(avgRating);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }
}
