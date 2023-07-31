package com.example.weazy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button refBtn = findViewById(R.id.btn_get_weather);
        TextView cityView = findViewById(R.id.tv_city);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        String cityName = addresses.get(0).getLocality();
                        cityView.setText(cityName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        myWeather(cityView.getText().toString());

        refBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newCity = findViewById(R.id.enter_city);
                String myCity = newCity.getText().toString();
                myWeather(myCity);
                cityView.setText(myCity);
            }
        });


    }

    private void myWeather(String city) {
        WeatherApi weatherApi = new WeatherApi(this);

        weatherApi.getWeatherData(city, new WeatherApi.WeatherCallback() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                // Do something with the weather data
                TextView temperatureTextView = findViewById(R.id.tv_temperature);
                TextView humidityTextView = findViewById(R.id.tv_humidity);
                TextView descriptionTextView = findViewById(R.id.tv_description);

                temperatureTextView.setText(String.valueOf(weatherData.getTemperature()));
                humidityTextView.setText(String.valueOf(weatherData.getHumidity()));
                descriptionTextView.setText(weatherData.getDescription());

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error
            }
        });
    }
}