package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btn_getForeCastByLocation, btn_getWeatherByName;
    TextView tv_cityName;
    EditText et_dataInput;
    ListView lv_weatherReports;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // assign values to each control on the layout
        btn_getForeCastByLocation = findViewById(R.id.btn_getCityID);
        btn_getWeatherByName = findViewById(R.id.btn_getWeatherByCityName);
        tv_cityName = findViewById(R.id.tv_cityName);
        et_dataInput = findViewById(R.id.et_dataInput);
        lv_weatherReports = findViewById(R.id.lv_weatherReports);

        // Initialize fusedLocationProviderClient. This provides the geolocation.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

        // button click will check the geolocation of the device, then find the closest city ID
        btn_getForeCastByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Check permission of location first
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    getLocation(weatherDataService);
                } else {
                    // Permission is denied
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        btn_getWeatherByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDataService.getCityID(et_dataInput.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityID) {
                        weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForecastByNameCallback() {
                            @Override
                            public void onError(String message) {

                            }

                            @Override
                            public void onResponse(List<WeatherReportModel> weatherReportModels) {
                                //ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
                                //WeatherArrayAdapter arrayAdapter = new WeatherArrayAdapter(MainActivity.this, R.layout.weather_list_view_layout, weatherReportModels);
                                //lv_weatherReports.setAdapter(arrayAdapter);
                            }
                        });
                    }
                });
            }
        });

    }

    public void getLocation(WeatherDataService weatherDataService){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if(location != null){
                    // Use long and lat of location to API
                    weatherDataService.getCityNameByLocation(location, new WeatherDataService.GetCityNameByLocationCallback()
                    {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onResponse(String cityName, String stateName, List<WeatherGovReportModel> forecastReports) {
                            tv_cityName.setText(cityName + ", " + stateName);

                            WeatherArrayAdapter arrayAdapter = new WeatherArrayAdapter(MainActivity.this, R.layout.weather_list_view_layout, forecastReports);
                            lv_weatherReports.setAdapter(arrayAdapter);
                        }
                    });
//                            weatherDataService.getCityForecastByID(cityID, new WeatherDataService.ForeCastByIDResponse() {
//                                @Override
//                                public void onError(String message) {
//
//                                }
//
//                                @Override
//                                public void onResponse(List<WeatherReportModel> weatherReportModels) {
//                                    //ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModels);
//                                    WeatherArrayAdapter arrayAdapter = new WeatherArrayAdapter(MainActivity.this, R.layout.weather_list_view_layout, weatherReportModels);
//                                    lv_weatherReports.setAdapter(arrayAdapter);
//                                }
//                            });
                }
            }
        });
    }
}