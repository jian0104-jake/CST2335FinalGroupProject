package com.example.cst2335finalgroupproject.geodata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.geodata.entity.City;
import com.google.android.material.snackbar.Snackbar;

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



        searchCityListView = findViewById(R.id.searchCitiesListView);
        searchCityListView.setAdapter(myListAdapter = new MyListAdapter());

        searchBtn.setOnClickListener(e->{
            progressBar.setVisibility(View.VISIBLE);
            cities.add(new City("Ottawa","Canada","Ontario","Canada dollar","13.3333","4.44444"));
            cities.add(new City("xxx","aaaa","ffff","ddd","13.3333","4.44444"));
            cities.add(new City("xfsdf","aaaa","ffff","ddd","13.3333","4.44444"));
            cities.add(new City("sasfsdfsdsss","aaaa","ffff","ddd","13.3333","4.44444"));
            myListAdapter.notifyDataSetChanged();
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
}