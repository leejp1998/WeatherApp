package com.example.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<WeatherGovReportModel> {
    private static final String TAG = "WeatherArrayAdapter";
    private Context mContext;
    int mResource;

    public WeatherArrayAdapter(Context context, int resource, List<WeatherGovReportModel> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int number = getItem(position).getNumber();
        String name = getItem(position).getName();
        String startTime = getItem(position).getStartTime();
        String endTime = getItem(position).getEndTime();
        boolean isDaytime = getItem(position).isDaytime();
        int temperature = getItem(position).getTemperature();
        String windspeed = getItem(position).getWindSpeed();
        String icon = getItem(position).getIcon();
        String shortForecast = getItem(position).getShortForecast();
        String detailedForecast = getItem(position).getDetailedForecast();



        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView ivWeatherIcon = (ImageView) convertView.findViewById(R.id.iv_weather_icon);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvTemp = (TextView) convertView.findViewById(R.id.tv_cur_temp);
       // TextView tvMinTemp = (TextView) convertView.findViewById(R.id.tv_min_temp);
        TextView tvShortForecast = (TextView) convertView.findViewById(R.id.shortForecast);
        TextView tvWindSpeed = (TextView) convertView.findViewById(R.id.tv_windspeed);

        new DownloadImageTask(ivWeatherIcon).execute(icon);
        tvDate.setText(name);
        tvTemp.setText("Temp \n" + String.valueOf(temperature) + "°F");
       // tvMinTemp.setText("Min: " + String.valueOf(temperature) + "°C");
        tvShortForecast.setText("Forecast \n " + String.valueOf(shortForecast));
        tvWindSpeed.setText("Humidity \n" + String.valueOf(windspeed));

//        String applicable_date = getItem(position).getApplicable_date().substring(5);
//        String weather_state_abbr = getItem(position).getWeather_state_abbr(); // use weather state abbr to find the icon
//        weatherIcon_id = getWeatherIcon(weather_state_abbr);
//        float temp = getItem(position).getThe_temp();
//        float min_temp = getItem(position).getMin_temp();
//        float max_temp = getItem(position).getMax_temp();
//        int humidity = getItem(position).getHumidity();
//
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView = inflater.inflate(mResource, parent, false);
//
//        ImageView ivWeatherIcon = (ImageView) convertView.findViewById(R.id.iv_weather_icon);
//        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
//        TextView tvTemp = (TextView) convertView.findViewById(R.id.tv_cur_temp);
//        TextView tvMinTemp = (TextView) convertView.findViewById(R.id.tv_min_temp);
//        TextView tvShortForecast = (TextView) convertView.findViewById(R.id.tv_max_temp);
//        TextView tvHumidity = (TextView) convertView.findViewById(R.id.tv_humidity);
//
//        ivWeatherIcon.setImageResource(weatherIcon_id);
//        tvDate.setText(applicable_date);
//        tvTemp.setText("Temp \n" + String.valueOf(temp) + "°C");
//        tvMinTemp.setText("Min: " + String.valueOf(min_temp) + "°C");
//        tvShortForecast.setText("Max: " + String.valueOf(max_temp) + "°C");
//        tvHumidity.setText("Humidity \n" + String.valueOf(humidity) + "%");

        return convertView;
    }

    private int getWeatherIcon(String weather_state_abbr)
    {
        int icon = -1;

        if(weather_state_abbr.equals("c")){
            icon = R.drawable.c;
        } else if(weather_state_abbr.equals("h")){
            icon = R.drawable.h;
        } else if(weather_state_abbr.equals("hc")){
            icon = R.drawable.hc;
        } else if(weather_state_abbr.equals("hr")){
            icon = R.drawable.hr;
        } else if(weather_state_abbr.equals("lc")){
            icon = R.drawable.lc;
        } else if(weather_state_abbr.equals("lr")){
            icon = R.drawable.lr;
        } else if(weather_state_abbr.equals("s")){
            icon = R.drawable.s;
        } else if(weather_state_abbr.equals("sl")){
            icon = R.drawable.sl;
        } else if(weather_state_abbr.equals("sn")){
            icon = R.drawable.sn;
        } else if(weather_state_abbr.equals("t")){
            icon = R.drawable.t;
        } else {
            // weather state abbr is not registered
            icon = -99;
        }

        return icon;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.i("DownloadError", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
