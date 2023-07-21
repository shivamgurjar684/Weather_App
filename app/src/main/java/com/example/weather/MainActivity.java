package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    int PERMISSIONS_REQUEST_LOCATION = 1;
    double Latitude;
    double Longitude;
    String cityName;

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private ImageView iconIV, backIV, searchIV;
    private TextInputEditText cityEdt;

    private RecyclerView recyclerView;
    private HourlyForecastAdapter adapter;
    private ArrayList<HourlyForecast> hourlyForecasts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        recyclerView = findViewById(R.id.idRVWeather);

        //---------------------
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        hourlyForecasts = new ArrayList<>();
        adapter = new HourlyForecastAdapter(this, hourlyForecasts);
        recyclerView.setAdapter(adapter);
        //----------------------


        // Initialize LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Location permission not granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);

        }

        // If the GPS is not ON asks the user to turn it ON
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are not enabled
            Toast.makeText(MainActivity.this, "Please enable GPS to use this app", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            onCreate(savedInstanceState);

        }

        // If we want to get weather of another city

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cityName = cityEdt.getText().toString() ;

                if(!cityName.isEmpty()){

                   getWeatherInfo(cityName);
                }
            }
        });

        // Initialize location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Update UI with user's location

                Latitude = location.getLatitude();
                Longitude = location.getLongitude();


                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    cityName = addresses.get(0).getLocality();
                    // Use cityName as needed...

                    getWeatherInfo(cityName);

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        // Request location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }

    private void getWeatherInfo(String cityName){

        String url = "https://api.weatherapi.com/v1/forecast.json?key=fd0853144cc148ac9f351810230703&q=" + cityName + "&days=1&aqi=yes&alerts=yes" ;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                hourlyForecasts.clear();

                try {


                    //---------------------------------------------

                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature.substring(0,2) + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    String city = response.getJSONObject("location").getString("name");
                    String region = response.getJSONObject("location").getString("region");
                    String country = response.getJSONObject("location").getString("country");

                    cityNameTV.setText(city + " , " + region + "\n" + country);
                    cityEdt.getText().clear();


                    if (isDay==1){
                        // Day
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/472/828/HD-wallpaper-mountain-peak-clouds-forest-landscape-thumbnail.jpg").into(backIV);
                    } else {
                        // Night
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/467/834/HD-wallpaper-nature-black-white-moon-nature-landscape.jpg").into(backIV);
                    }

                    //---------------------------------------------

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0) ;
                    JSONArray hourArray = forecastO.getJSONArray("hour") ;

                    for (int i = 0 ; i < hourArray.length() ;i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        hourlyForecasts.add(new HourlyForecast(time,temper,img,wind));
                    }
                    adapter.notifyDataSetChanged();

                    loadingPB.setVisibility(View.INVISIBLE);
                    homeRL.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid City name..", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}