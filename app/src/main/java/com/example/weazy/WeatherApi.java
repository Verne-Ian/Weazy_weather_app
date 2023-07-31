 package com.example.weazy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApi {
    private static final String API_KEY = "c4fab0f579869cd658a49d43754335d9";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private RequestQueue requestQueue;

    public WeatherApi(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getWeatherData(String city, final WeatherCallback callback) {
        String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WeatherData weatherData = parseWeatherData(response);
                        callback.onSuccess(weatherData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(error.getMessage());
                    }
                });

        requestQueue.add(request);
    }

    private WeatherData parseWeatherData(JSONObject response) {
        WeatherData weatherData = new WeatherData();

        try {
            JSONObject main = response.getJSONObject("main");
            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);

            double kelvinTemp = main.getDouble("temp");
            int celsiusTemp = (int) (kelvinTemp - 273.15); // convert from Kelvin to Celsius

            weatherData.setTemperature(celsiusTemp);
            weatherData.setHumidity(main.getInt("humidity"));
            weatherData.setDescription(weather.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherData;
    }

    public interface WeatherCallback {
        void onSuccess(WeatherData weatherData);
        void onFailure(String errorMessage);
    }
}

