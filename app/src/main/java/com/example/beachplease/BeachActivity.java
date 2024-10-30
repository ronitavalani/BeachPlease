package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;

import java.util.Date;
import java.util.List;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;

public class BeachActivity extends AppCompatActivity {
    private Beach beach;
    private User user;
    private TextView liveWeatherInfo;
    private static final String API_KEY = "60656159d401dedb2ab28b487e8bd931";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        //Intent intent = getIntent();

        //Initialize TextView to display weather
        liveWeatherInfo = findViewById(R.id.weatherInfo);

        //Latitude and longitude needs to be dynamic at some point (EXAMPLE FOR NOW)
        double latitude = 34.0129;
        double longitude = -118.5017;

        fetchWeatherData (latitude, longitude);


    }

    public void fetchWeatherData (double latitude, double longitude){
        new Thread(() ->{
            try{
                //bc this is  long can make a separate function
                String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                        latitude +"&lon="+longitude+"&appid="+API_KEY+"&units=imperial";
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection =( HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String input;
                while  ((input = in.readLine()) !=null){
                    content.append(input);

                }
                in.close();
                urlConnection.disconnect();


                JSONObject response = new JSONObject(content.toString());
                JSONObject main = response.getJSONObject("main");
                double temp = main.getDouble("temperature");
                int humidity = main.getInt("humidity");
                JSONArray weatherInfoArray = response.getJSONArray("weather");
                String weatherCondition = weatherInfoArray.getJSONObject(0).getString("description");

                final String formattedWeatherInfo = "Temperature" + temp + "°F\n" + "Humidity: " + humidity + "%\n" + "Conditions: " + weatherCondition;

                runOnUiThread(()->liveWeatherInfo.setText(weatherCondition));
            }
            catch (Exception e){
             Log.e("WeatherAPI", "Error displaying weather data", e);
            }
        } ).start();
        }

    public void displayBeachInfo() {

    }

    public void displayReviews() {

    }

    public void writeReview (String beachName, Double rating, Date date, User author, String comment) {
        Review newReview = new Review(beachName, rating, date, author, comment);
        beach.updateAvgRating(rating);
        beach.getReviews().add(newReview);
    }

    //should this even be an option or should it just refresh the map?
    public void mapClick(android.view.View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileClick(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}

    }
