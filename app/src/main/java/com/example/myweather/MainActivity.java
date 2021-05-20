package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    EditText city;
    TextView dataDisplay;
    String cityName;

    String weatherMain="";
    String weatherDescription="";
    String weatherTemp="";
    String weatherHumidity="";
    String weatherTempFeel="";

    Context obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city=(EditText) findViewById(R.id.editTextCityName);
        dataDisplay=(TextView)findViewById(R.id.textDisplay);
        obj=this;


    }

    public class DataDownloader extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            String Data="";
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
//                Log.i("Reading data","Starting Input Stream");

                InputStream inputStream=connection.getInputStream();
                int data=inputStream.read();

                while(data!=-1){
//                    Log.i("Reading data",data+"");
                    Data+=(char)data;
                    data=inputStream.read();
                }
                return Data;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Reading data","ERROR");
            }
            return null;
        }
    }

    public void showData(View view){
        cityName= (String) city.getText().toString();
        if(cityName.trim() !=""){

            //Closing the keyboard
            try {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }

            //Data Processing
            JSONObject jsonData;
            //Getting Data as String and manuplating as json
            DataDownloader getData=new DataDownloader();
            try {
                cityName=cityName.trim()==null?"London":cityName;
                String apiUrl="https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid=1502ebb78823bbf9705726d6378bafbe";
                String apiData=getData.execute(apiUrl).get();
                if(apiData==null){
                    Toast.makeText(this, "Error: Make Sure Internet Is Connected", Toast.LENGTH_SHORT).show();

                }
                //Parsing to JSON
                jsonData=new JSONObject(apiData);

                JSONArray jsonArr=new JSONArray(jsonData.getString("weather"));
//            Log.i("Reading Data","Weather"+jsonArr);

                //Other details of weather (Main)
                JSONObject jsonDetails=jsonData.getJSONObject("main");

                //Code below works fine and gets the data for the given city
                weatherMain=jsonArr.getJSONObject(0).getString("main");
                weatherDescription=jsonArr.getJSONObject(0).getString("description");
                weatherTemp=jsonDetails.getString("temp")+"K";
                weatherHumidity=jsonDetails.getString("humidity");
                weatherTempFeel=jsonDetails.getString("feels_like");

                //Logging The Weather
                Log.i("Weather Main",weatherMain);
                Log.i("Weather Description",weatherDescription);
                Log.i("Weather Temp",weatherTemp);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(obj, "City Not Found", Toast.LENGTH_SHORT).show();
            }
            setData();

        }
        else {
            Toast.makeText(this, "Enter City Name", Toast.LENGTH_SHORT).show();
        }

    }

    //Sets data on user screen
    private void setData() {
        dataDisplay.setText(weatherMain);
        ((TextView)findViewById(R.id.tempratureTextView)).setText(weatherTemp);
        ((TextView)findViewById(R.id.discriptionTextView)).setText(weatherDescription);
    }
}