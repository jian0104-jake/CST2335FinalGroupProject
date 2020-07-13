package com.example.cst2335finalgroupproject.geodata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.geodata.entity.City;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
//https://api.geodatasource.com/city?key=QHSHZ2TNSAKTUGFC73CUTV1ZNJUESDXK&lat=45.4215&lng=-75.6972&format=JSON
//{"country":"CA","region":"Ontario","city":"Centretown","latitude":"45.4153","longitude":"-75.6964","currency_code":"CAD","currency_name":"Canadian Dollar","currency_symbol":"$","sunrise":"05:27","sunset":"20:49","time_zone":"-05:00","distance_km":"0.6924"}

/**
 * the main activity for geo data source
 */
public class GeoDataSource extends AppCompatActivity {
    /**
     *  the list adapter for the content for the list view
     */
    private MyListAdapter myListAdapter;

    /**
     * the list view for the nearby cities based on the latitude and longitude information
     */
    private ListView searchCityListView;

    /**
     * stores the cities searched
     */
    private ArrayList<City> cities = new ArrayList<>();

    /**
     * shows the progress of loading city list view
     */
    private ProgressBar progressBar;


    private EditText latitudeEdit;
    private EditText longitudeEdit;


    /**
     * start  geo data source activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_data_source);

        Button searchBtn = findViewById(R.id.searchGeoBtn);
        Button favoriteGeoBtn  = findViewById(R.id.favoriteGeoBtn);

        progressBar = findViewById(R.id.geoProcessBar);

        latitudeEdit = findViewById(R.id.latitudeEdit);
        longitudeEdit = findViewById(R.id.longitudeEdit);

        searchCityListView = findViewById(R.id.searchCitiesListView);
        searchCityListView.setAdapter(myListAdapter = new MyListAdapter());

        searchBtn.setOnClickListener(e->{
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://api.geodatasource.com/city?key=QHSHZ2TNSAKTUGFC73CUTV1ZNJUESDXK&lat="+latitudeEdit.getText()+"&lng="+longitudeEdit.getText()+"&format=JSON";
            GeoCityQuery forecastQuery = new GeoCityQuery();
            forecastQuery.execute(url);
        });

        favoriteGeoBtn.setOnClickListener(e-> Toast.makeText(this, R.string.geo_toast_message,Toast.LENGTH_LONG).show());

        searchCityListView.setOnItemClickListener((p,b,pos,id)->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            City city = cities.get(pos);
            alertDialogBuilder.setTitle((pos+1) +":  "+ city.getName() +", "+ city.getRegion() + ", "+city.getCountry() + ", " + city.getCurrency() + "in " + city.getLatitude() +", "+city.getLongitude()  )

                    .setPositiveButton("Show in Map",(click,arg)->{
                        Snackbar snackbar = Snackbar.make(searchCityListView,"Show in google map", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    })

                    .setNegativeButton("Save to Favorite",(click,arg)->{
                        Toast.makeText(this, R.string.geo_toast_message,Toast.LENGTH_LONG).show();
                    })
                    .create().show();
        });
    }

    /**
     *
     */
    private class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
        return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            City city = cities.get(position);

            View newView = null;
            TextView tView = null;
            LayoutInflater inflater = getLayoutInflater();
            newView = inflater.inflate(R.layout.city_row_layout, parent, false);
            tView     = newView.findViewById(R.id.cityName);
            tView.setText((position+1) +":  "+city.getName());

            return newView;
        }
    }

    /**
     *this class is used to fetch city data in the background
     */

    private class GeoCityQuery extends AsyncTask<String, Integer, String>{
        /**
         * stores country name from the api
         */
        private String country;

        /**
         * stores region name from the api
         */
        private String region;

        /**
         * stores city name from the api
         */
        private String city;
        /**
         * stores currency from the api
         */
        private String currency;

        /**
         * stores latitude  from the api
         */
        private double latitude;

        /**
         * stores longitude  from the api
         */
        private  double longitude;

        /**
         * fetch the api date in the background thread
         * @param args url
         * @return results used for onPostExecute()
         */
        @Override
        protected String doInBackground(String... args) {

            try {

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //From part 3: slide 19
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                // convert string to JSON: Look at slide 27:
                JSONObject cityInfo = new JSONObject(result);

                country = cityInfo.getString("country");
                region = cityInfo.getString("region");
                city = cityInfo.getString("city");
                currency = cityInfo.getString("currency_code");
                latitude = cityInfo.getDouble("latitude");
                longitude = cityInfo.getDouble("longitude");

                cities.add(new City(city,region,country,currency,latitude,longitude));

            } catch (Exception e) {
                return "Wrong";
            }
            return "done";
        }


            public void onProgressUpdate(Integer ... args)
            {


            }

            //Type3
            public void onPostExecute(String fromDoInBackground)
            {
                myListAdapter.notifyDataSetChanged();

            }
        }
}