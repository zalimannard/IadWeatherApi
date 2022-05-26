package com.example.iadweatherapi;

import static android.content.ContentValues.TAG;
import static android.os.FileUtils.copy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private LocationManager locationManager;
    ArrayList<City> cities = new ArrayList<>();

    Integer currentCity = 0;

    private TextView city;
    private TextView isCurrentPlace;
    private TextView weatherDescription;
    private TextView temperature;
    private TextView humidity;
    private TextView pressure;
    private ImageView weatherLogo;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        city = findViewById(R.id.City);
        isCurrentPlace = findViewById(R.id.isCurrentPlace);
        weatherLogo = findViewById(R.id.WeatherLogo);
        weatherDescription = findViewById(R.id.WeatherDescription);
        temperature = findViewById(R.id.Temperature);
        humidity = findViewById(R.id.Humidity);
        pressure = findViewById(R.id.Pressure);

        cities.add(new City("Текущее местоположение", 0.0, 0.0));
        cities.get(0).setLatitude_(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
        cities.get(0).setLongitude_(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());

        File fileo = new File(getExternalFilesDir(null), "mycities.txt");
        try
        {
            File file = fileo;
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null)
            {
                String[] cityStr = line.split("_");
                City city = new City(cityStr[0], Double.valueOf(cityStr[1]), Double.valueOf(cityStr[2]));
                cities.add(city);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                String.valueOf(cities.get(currentCity).getLatitude_()) + "&lon=" +
                String.valueOf(cities.get(currentCity).getLongitude_()) +
                "&appid=ed438edd3a5572a12570d7263cd69298&lang=ru&units=metric";
        new GetUrlData().execute(url);
    }

    private static float startX = 0;
    private static float startY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == 0)
        {
            startX = event.getX();
            startY = event.getY();
        } else if (event.getAction() == 1)
        {
            System.out.println();
            if (Math.abs(event.getX() - startX) > 300)
            {
                if (startX > event.getX())
                {
                    currentCity = Math.min(cities.size() - 1, currentCity + 1);
                } else
                {
                    currentCity = Math.max(0, currentCity - 1);
                }
                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                        String.valueOf(cities.get(currentCity).getLatitude_()) + "&lon=" +
                        String.valueOf(cities.get(currentCity).getLongitude_()) +
                        "&appid=ed438edd3a5572a12570d7263cd69298&lang=ru&units=metric";
                new GetUrlData().execute(url);
            }
            startX = 0.0f;
            startY = 0.0f;
        }
        System.out.println(event.getAction());
        System.out.println("Номер города: " + currentCity);
        return super.onTouchEvent(event);
    }

    private LocationListener locationListener = new LocationListener()
    {

        @Override
        public void onLocationChanged(Location location)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    };


    private class GetUrlData extends AsyncTask<String, String, String>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
//            city.setText("");
            isCurrentPlace.setText("");
//            weatherDescription.setText("");
//            temperature.setText("");
//            pressure.setText("");
//            humidity.setText("");
        }

        @Override
        protected String doInBackground(String... strings)
        {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try
            {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                if (connection != null)
                    connection.disconnect();
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            try
            {
                JSONObject obj = new JSONObject(result);
                city.setText(obj.getString("name"));
                if (currentCity == 0)
                {
                    isCurrentPlace.setText("Текущее местоположение");
                } else
                {
                    isCurrentPlace.setText("");
                }
                String imageName = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
                Context context = weatherLogo.getContext();

                Glide.with(context)
                        .load("https://openweathermap.org/img/wn/" + imageName + "@4x.png")
                        .into(weatherLogo);

                weatherDescription.setText(obj.getJSONArray("weather").getJSONObject(0).getString("description"));
                temperature.setText("Температура: " + obj.getJSONObject("main").getDouble("temp") + "°C");
                humidity.setText("Влажность воздуха " + obj.getJSONObject("main").getDouble("humidity") + "%");
                pressure.setText("Давление атм: " + obj.getJSONObject("main").getDouble("pressure") + " мм. рт. ст.");
            } catch (JSONException e)
            {
            }
        }
    }

    public void gotoCities(View v)
    {
        Intent intent = new Intent(this, CityListActivity.class);
        startActivity(intent);
    }
}
