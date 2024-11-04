package com.example.beachplease;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Beach implements Parcelable {
    private String name;
    private Double longitude;
    private Double latitude;
    private String hours;
    private Double avgRating;
    private String picture;
    private Object reviews; // Accepts both List<String> or Map<String, Boolean> types
    private Object tags; // Accepts both Map<String, Boolean> and List<String> types
    private String blurb;

    public Beach(String name, Double longitude, Double latitude, String hours, Double averageRating, String picture, String blurb) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hours = hours;
        this.avgRating = averageRating;
        this.picture = picture;
        this.blurb = blurb;
        this.reviews = new ArrayList<>();
        this.tags = new HashMap<>();
    }

    // Default constructor for Firebase
    public Beach() {
        this.reviews = new ArrayList<>();
        this.tags = new HashMap<>();
    }

    protected Beach(Parcel in) {
        name = in.readString();
        longitude = (Double) in.readValue(Double.class.getClassLoader());
        latitude = (Double) in.readValue(Double.class.getClassLoader());
        hours = in.readString();
        avgRating = (Double) in.readValue(Double.class.getClassLoader());
        picture = in.readString();
        blurb = in.readString();

        // Deserialize reviews
        List<String> reviewsList = new ArrayList<>();
        in.readList(reviewsList, String.class.getClassLoader());
        reviews = reviewsList;

        // Deserialize tags
        List<String> tagsList = new ArrayList<>();
        in.readList(tagsList, String.class.getClassLoader());
        Map<String, Boolean> tagsMap = new HashMap<>();
        for (String tag : tagsList) {
            tagsMap.put(tag, true);
        }
        tags = tagsMap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeString(hours);
        dest.writeValue(avgRating);
        dest.writeString(picture);
        dest.writeString(blurb);

        // Serialize reviews as a List<String>
        if (reviews instanceof List) {
            dest.writeList((List<String>) reviews);
        } else if (reviews instanceof Map) {
            dest.writeList(new ArrayList<>(((Map<String, Boolean>) reviews).keySet()));
        }

        // Serialize tags as a List<String>
        if (tags instanceof Map) {
            dest.writeList(new ArrayList<>(((Map<String, Boolean>) tags).keySet()));
        } else if (tags instanceof List) {
            dest.writeList((List<String>) tags);
        }
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

    // Getters and setters
    public String getName() {
        return name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getHours() {
        return hours;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public String getPicture() {
        return picture;
    }

    public String getBlurb() {
        return blurb;
    }

    // Convert reviews to List<String>
    public List<String> getReviews() {
        if (reviews instanceof List) {
            return (List<String>) reviews;
        } else if (reviews instanceof Map) {
            return new ArrayList<>(((Map<String, Object>) reviews).keySet());
        }
        return new ArrayList<>();
    }

    // Convert tags to List<String>
    public List<String> getTags() {
        if (tags instanceof List) {
            return (List<String>) tags;
        } else if (tags instanceof Map) {
            return new ArrayList<>(((Map<String, Boolean>) tags).keySet());
        }
        return new ArrayList<>();
    }

    public void addReview(String reviewId) {
        if (reviews instanceof List) {
            ((List<String>) reviews).add(reviewId);
        } else if (reviews instanceof Map) {
            ((Map<String, Boolean>) reviews).put(reviewId, true);
        }
    }

    public void addTag(String tag) {
        if (tags instanceof Map) {
            ((Map<String, Boolean>) tags).put(tag, true);
        } else if (tags instanceof List) {
            ((List<String>) tags).add(tag);
        }
    }

    public void updateAvgRating(Double rating) {
        int reviewCount = getReviews().size();
        avgRating = ((avgRating * reviewCount) + rating) / (reviewCount + 1);
    }

    public void addBeachToFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("beaches");
        database.child(name).setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("BeachData", "Beach added successfully!");
            } else {
                Log.e("BeachData", "Error adding beach: " + task.getException());
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

