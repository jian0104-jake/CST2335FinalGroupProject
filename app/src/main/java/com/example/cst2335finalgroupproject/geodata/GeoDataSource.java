package com.example.cst2335finalgroupproject.geodata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongDetailActivity;
import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
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

    /**
     * latitude edit text
     */
    private EditText latitudeEdit;

    /**
     * longitude edit text
     */
    private EditText longitudeEdit;

    /**
     * stores the latitude and longitude data last time user inputs
     */
    private SharedPreferences prefs = null;

    /**
     * database for saved cities
     */
    private SQLiteDatabase db;



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
        progressBar.setVisibility(View.INVISIBLE);

        latitudeEdit = findViewById(R.id.latitudeEdit);
        longitudeEdit = findViewById(R.id.longitudeEdit);

        searchCityListView = findViewById(R.id.searchCitiesListView);
        searchCityListView.setAdapter(myListAdapter = new MyListAdapter());

        prefs = getSharedPreferences("searchInfo", Context.MODE_PRIVATE);
        String latitudeSaved = prefs.getString("latitude","");
        String longitudeSaved = prefs.getString("longitude","");
        latitudeEdit.setText(latitudeSaved);
        longitudeEdit.setText(longitudeSaved);


        //database setup
        SavedCitiesOpenHelper dbOpener = new SavedCitiesOpenHelper(this);
        db = dbOpener.getWritableDatabase();


        searchBtn.setOnClickListener(e->{
            cities.clear();
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://api.geodatasource.com/city?key=QHSHZ2TNSAKTUGFC73CUTV1ZNJUESDXK&lat="+latitudeEdit.getText()+"&lng="+longitudeEdit.getText()+"&format=JSON";
            GeoCityQuery forecastQuery = new GeoCityQuery();
            forecastQuery.execute(url);
            saveSharedPrefs(latitudeEdit.getText().toString(),longitudeEdit.getText().toString());
        });

        favoriteGeoBtn.setOnClickListener(e-> {
            Intent intent = new Intent(GeoDataSource.this, SavedCitiesActivity.class);
            startActivity(intent);
        });

        searchCityListView.setOnItemClickListener((p,b,pos,id)->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            City city = cities.get(pos);
            alertDialogBuilder.setTitle((pos+1) +":  "+ city.getName() +", "+ city.getRegion() + ", "+city.getCountry() + ", " + city.getCurrency() + " in " + city.getLatitude() +", "+city.getLongitude()  )

                    .setPositiveButton(R.string.geo_show_in_google_map,(click,arg)->{
                        GeoUtil.openGoogleNavi(Double.toString(city.getLatitude()),Double.toString(city.getLongitude()),this);
                    })

                    .setNegativeButton(R.string.geo_save_to_favorite,(click,arg)->{

                        ContentValues newSaveCityContent = new ContentValues();
                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_NAME, city.getName());

                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_COUNTRY, city.getCountry());
                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_REGION, city.getRegion());
                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_CURRENCY, city.getCurrency());
                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_LATITUDE, city.getLatitude());
                        newSaveCityContent.put(SavedCitiesOpenHelper.COL_LONGITUDE, city.getLongitude());

                        long newId = db.insert(SavedCitiesOpenHelper.TABLE_NAME,null,newSaveCityContent);

                        Toast.makeText(this, R.string.geo_toast_message,Toast.LENGTH_LONG).show();
                    })
                    .create().show();
        });
    }


    /**
     * Save last searched latitude and longitude as sharedpreference in the phone
     *
     * @param latitude the latitude users inputs in their last use
     * @param longitude the longitude users inputs in their last use
     */
    private void saveSharedPrefs(String latitude, String longitude) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.commit();
    }



    /**
     *  MyListAdapter for the searched cities
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
            tView.setText((position+1) +":  "+city.getName() + ", " + city.getRegion() + ", " + city.getCountry());

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
            publishProgress(25);

            try {

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                publishProgress(50);

                //From part 3: slide 19
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                publishProgress(75);


                // convert string to JSON: Look at slide 27:
                JSONObject cityInfo = new JSONObject(result);

                country = cityInfo.getString("country");
                region = cityInfo.getString("region");
                city = cityInfo.getString("city");
                currency = cityInfo.getString("currency_code");
                latitude = cityInfo.getDouble("latitude");
                longitude = cityInfo.getDouble("longitude");

                cities.add(new City(city,region,country,currency,latitude,longitude));
                publishProgress(100);


            } catch (Exception e) {
                return "Wrong";
            }
            return "done";
        }

        /**
         *
         * @param args parameters from publishProgress method
         */
            public void onProgressUpdate(Integer ... args)
            {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(args[0]);

            }

        /**
         *
         * @param fromDoInBackground string passed from doInBackground
         */
        public void onPostExecute(String fromDoInBackground)
            {
                myListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

            }
        }


}