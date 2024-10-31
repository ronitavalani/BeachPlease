package com.example.beachplease;

import android.os.Parcel;
import android.os.Parcelable;

public class Beach implements Parcelable {
    private String name;
    private String blurb;
    private String hours;
    private Float avgRating;
    private Double latitude;
    private Double longitude;

    // Default constructor needed for Firebase
    public Beach() {}

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBlurb() { return blurb; }
    public void setBlurb(String blurb) { this.blurb = blurb; }

    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }

    public Float getAvgRating() { return avgRating; }
    public void setAvgRating(Float avgRating) { this.avgRating = avgRating; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // Parcelable implementation
    protected Beach(Parcel in) {
        name = in.readString();
        blurb = in.readString();
        hours = in.readString();
        avgRating = (in.readByte() == 0) ? null : in.readFloat();
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
            dest.writeFloat(avgRating);
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
