package com.example.iadweatherapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity implements View.OnClickListener
{
    ArrayList<City> cities = new ArrayList<>();
    ArrayList<City> myCities = new ArrayList<>();
    LinearLayout citiesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        citiesLayout = (LinearLayout) findViewById(R.id.Cities);

        File fileCities = new File(getExternalFilesDir(null), "cities.txt");
        File fileMyCities = new File(getExternalFilesDir(null), "mycities.txt");
        try
        {
            File file = fileCities;
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
        try
        {
            File file = fileMyCities;
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null)
            {
                String[] cityStr = line.split("_");
                City city = new City(cityStr[0], Double.valueOf(cityStr[1]), Double.valueOf(cityStr[2]));
                myCities.add(city);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < cities.size(); ++i)
        {
            boolean isChecked = false;
            for (City myCity : myCities)
            {
                if (myCity.getName_().equals(cities.get(i).getName_()))
                {
                    isChecked = true;
                }
            }
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);
            checkBox.setText(cities.get(i).getName_());
            checkBox.setChecked(isChecked);
            checkBox.setOnClickListener(this);
            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            checkParams.setMargins(10, 10, 10, 10);
            checkParams.gravity = Gravity.CENTER;
            citiesLayout.addView(checkBox);
        }
    }

    public void back(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveMyCities()
    {
        try (FileOutputStream fos = new FileOutputStream(new File(getExternalFilesDir(null), "mycities.txt")))
        {
            for (int i = 0; i < myCities.size(); ++i)
            {
                String text = myCities.get(i).getName_() + "_" +
                        String.valueOf(myCities.get(i).getLatitude_()) + "_" +
                        String.valueOf(myCities.get(i).getLongitude_()) + "\n";
                fos.write(text.getBytes());
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        City checkedCity = cities.get(view.getId());
        boolean isMyCity = false;
        for (City myCity : myCities)
        {
            if (myCity.getName_().equals(checkedCity.getName_()))
            {
                myCities.remove(myCity);
                isMyCity = true;
                break;
            }
        }
        if (!isMyCity)
        {
            myCities.add(checkedCity);
        }
        saveMyCities();
    }
}
