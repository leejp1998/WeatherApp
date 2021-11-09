package com.example.weatherapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_ID_WITH_GEOLOC = "https://www.metaweather.com/api/location/search/?lattlong=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";

    // Using weathergov API
    public static final String QUERY_FOR_GEOLOC_WEATHERGOV = "https://api.weather.gov/points/"; // {latitude, longitude}

    Context context;
    String cityID;
    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener{
        void onError(String message);

        void onResponse(String cityID);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener)
    {
        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cityID = "";
                        try {
                            JSONObject cityInfo = response.getJSONObject(0);
                            cityID = cityInfo.getString("woeid");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT).show();
                        volleyResponseListener.onResponse(cityID);
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something Wrong");
            }
        });

        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);

        //return cityID;
    }


    public void getCityIDWithGeo(Location location, VolleyResponseListener volleyResponseListener)
    {
        // convert location long_lat into String
        String geolocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        Log.d("tag", geolocation);
        String url = QUERY_FOR_CITY_ID_WITH_GEOLOC + geolocation;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        cityID = "";
                        try {
                            JSONObject cityInfo = response.getJSONObject(0);
                            cityID = cityInfo.getString("woeid");
                            Log.d("woeid from geo: ", cityID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        volleyResponseListener.onResponse(cityID);
                    }
                }, new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError error) {
            volleyResponseListener.onError("Error occurred");
        }
    });
        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface ForeCastByIDResponse{
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByID(String cityID, ForeCastByIDResponse foreCastByIDResponse)
    {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        Log.d("city ID", cityID);
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;

        // get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                            for(int i=0; i < consolidated_weather_list.length(); i++)
                            {
                                WeatherReportModel one_day_weather = new WeatherReportModel();
                                JSONObject selected_day_from_api = (JSONObject) consolidated_weather_list.get(i);

                                one_day_weather.setId(selected_day_from_api.getInt("id"));
                                one_day_weather.setWeather_state_name(selected_day_from_api.getString("weather_state_name"));
                                one_day_weather.setWeather_state_abbr(selected_day_from_api.getString("weather_state_abbr"));
                                one_day_weather.setWind_direction_compass(selected_day_from_api.getString("wind_direction_compass"));
                                one_day_weather.setCreated(selected_day_from_api.getString("created"));
                                one_day_weather.setApplicable_date(selected_day_from_api.getString("applicable_date"));
                                one_day_weather.setMin_temp(selected_day_from_api.getLong("min_temp"));
                                one_day_weather.setMax_temp(selected_day_from_api.getLong("max_temp"));
                                one_day_weather.setThe_temp(selected_day_from_api.getLong("the_temp"));
                                one_day_weather.setWind_speed(selected_day_from_api.getLong("wind_speed"));
                                one_day_weather.setWind_direction(selected_day_from_api.getLong("wind_direction"));
                                one_day_weather.setAir_pressure(selected_day_from_api.getLong("air_pressure"));
                                one_day_weather.setHumidity(selected_day_from_api.getInt("humidity"));
                                one_day_weather.setVisibility(selected_day_from_api.getLong("visibility"));
                                one_day_weather.setPredictability(selected_day_from_api.getInt("predictability"));
                                weatherReportModels.add(one_day_weather);
                            }

                            foreCastByIDResponse.onResponse(weatherReportModels);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        foreCastByIDResponse.onError("Error");
                    }
                });
        // get the property called "consolidated_weather" which is an json array

        // get each item in the array and assign it to a new WeatherReportModel object

        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface GetCityForecastByNameCallback{
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback)
    {
        // fetch the city id given the city name
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                // now we have the city id!
                getCityForecastByID(cityID, new ForeCastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        // now we have the weather report
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
        // fetch the city forecast given the city id
    }

    public interface GetCityForecastByLocationCallback{
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    /*
        Using WeatherGov API, get weather forecast of the geolocation
        In: Location, GetCityForecastByLocationCallback
        Out: ArrayList<WeatherReportModel>
     */
    public void getCityForecastByLocation(Location location, GetCityForecastByLocationCallback getCityForecastByLocationCallback)
    {
        String url = QUERY_FOR_GEOLOC_WEATHERGOV + location.toString();
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray forecast = response.getJSONArray(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getCityForecastByLocationCallback.onResponse(weatherReportModels);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                getCityForecastByLocationCallback.onError("Error occurred");
            }
        });
    }

    public interface GetCityNameByLocationCallback{
        void onError(String message);
        void onResponse(String cityName, String stateName, List<WeatherGovReportModel> forecastReports);
    }

    /*
        Using WeatherGov API, get weather forecast of the geolocation
        In: Location, GetCityForecastByLocationCallback
        Out: ArrayList<WeatherReportModel>
     */
    public void getCityNameByLocation(Location location, GetCityNameByLocationCallback getCityNameByLocationCallback)
    {
        // convert location long_lat into String
        String geolocation = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        String url = QUERY_FOR_GEOLOC_WEATHERGOV + geolocation;

        // Requests JsonObject from API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject locationInfo = response.getJSONObject("properties");

                            // Retrieve cityName and stateName from the JsonObject
                            JSONObject cityInfo = locationInfo.getJSONObject("relativeLocation").getJSONObject("properties");
                            String cityName = cityInfo.getString("city");
                            String stateName = cityInfo.getString("state");
                            Log.d("getCityNameByLocation", "city Name: " + cityName);

                            // JSONObject provides a url to the forecast. Call jsonObject and get the forecast from this url
                            String forecastURL = locationInfo.getString("forecast");
                            JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, forecastURL, null,
                                    new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            List<WeatherGovReportModel> forecastReports =  new ArrayList<WeatherGovReportModel>();
                                            try {
                                                JSONArray forecasts = response.getJSONObject("properties").getJSONArray("periods");
                                                Log.d("getCityNameByLocation", "forecast jsonArray: " + forecasts);
                                                for(int i=0; i < forecasts.length(); i++)
                                                {
                                                    WeatherGovReportModel one_day_weather = new WeatherGovReportModel();
                                                    JSONObject selected_day_from_api = (JSONObject) forecasts.get(i);

                                                    one_day_weather.setNumber(selected_day_from_api.getInt("number"));
                                                    one_day_weather.setName(selected_day_from_api.getString("name"));
                                                    one_day_weather.setStartTime(selected_day_from_api.getString("startTime"));
                                                    one_day_weather.setEndTime(selected_day_from_api.getString("endTime"));
                                                    one_day_weather.setDaytime(selected_day_from_api.getBoolean("isDaytime"));
                                                    one_day_weather.setTemperature(selected_day_from_api.getInt("temperature"));
                                                    one_day_weather.setWindSpeed(selected_day_from_api.getString("windSpeed"));
                                                    one_day_weather.setWindDirection(selected_day_from_api.getString("windDirection"));
                                                    one_day_weather.setIcon(selected_day_from_api.getString("icon"));
                                                    one_day_weather.setShortForecast(selected_day_from_api.getString("shortForecast"));
                                                    one_day_weather.setDetailedForecast(selected_day_from_api.getString("detailedForecast"));
                                                    forecastReports.add(one_day_weather);
                                                }

                                                getCityNameByLocationCallback.onResponse(cityName, stateName, forecastReports);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener(){
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i("onErrorResponse", error.toString());
                                        }
                                    }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError{
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("User-Agent", "MyWeatherApp/v1.0 (https://github.com/leejp1998/WeatherApp; leejp1998@gmail.com)");
                                    return params;
                                }
                            };
                            MySingleton.getInstance(context).addToRequestQueue(request2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponse", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "MyWeatherApp/v1.0 (https://github.com/leejp1998/WeatherApp; leejp1998@gmail.com)");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);

    }
}
