package com.example.cst2335finalgroupproject.geodata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.geodata.entity.City;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SavedCitiesActivity extends AppCompatActivity {

    /**
     * Saved cities database
     */
    private SQLiteDatabase db;

    /**
     *  the list adapter for the content for the list view
     */
    private SavedCitiesListAdapter myListAdapter;

    /**
     * the list view for saved cities
     */
    private ListView savedCitiesListView;

    /**
     * stores the saved cities
     */
    private ArrayList<City> cities = new ArrayList<>();


    /**
     * stores the city fragment
     */
    private Fragment cityFragment;


    /**
     *  onCreate method
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cities);
        setTitle(getString(R.string.geo_title_saved_cities));

        savedCitiesListView = findViewById(R.id.savedCitiesListView);
        savedCitiesListView.setAdapter(myListAdapter = new SavedCitiesListAdapter());

        loadDataFromDatabase();


        savedCitiesListView.setOnItemLongClickListener((p,b,pos,id)->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            City city = cities.get(pos);
            alertDialogBuilder.setTitle( "Do you want to remove " + city.getName() +", "+ city.getRegion() + ", "+city.getCountry() +"？"  )

                    .setPositiveButton(R.string.geo_yes,(click,arg)->{
                        deleteSavedCity(id);
                        cities.remove(pos);
                        this.getSupportFragmentManager().beginTransaction().remove(cityFragment).commit();
                        myListAdapter.notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(savedCitiesListView,R.string.geo_remove_success, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    })

                    .setNegativeButton(R.string.geo_no,(click,arg)->{ })
                    .create().show();
            return true;
        });


        savedCitiesListView.setOnItemClickListener((list,view,pos,id)->{
            Bundle dataToPass = new Bundle();
            dataToPass.putLong("ID", id );
            dataToPass.putString("cityInfo",cities.get(pos).toString());
            dataToPass.putString("lat",Double.toString(cities.get(pos).getLatitude()));
            dataToPass.putString("lng",Double.toString(cities.get(pos).getLongitude()));
            cityFragment = new CityMapFragment(); //add a DetailFragment
            cityFragment.setArguments( dataToPass ); //pass it a bundle for information
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, cityFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment

        });
    }


    /**
     * Load saved cities in the database
     */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        SavedCitiesOpenHelper dbOpener = new SavedCitiesOpenHelper(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {dbOpener.COL_ID, SavedCitiesOpenHelper.COL_NAME,SavedCitiesOpenHelper.COL_REGION,SavedCitiesOpenHelper.COL_COUNTRY,SavedCitiesOpenHelper.COL_CURRENCY,SavedCitiesOpenHelper.COL_LATITUDE,SavedCitiesOpenHelper.COL_LONGITUDE};
        Cursor results = db.query(false, SavedCitiesOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_ID);
        int nameIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_NAME);
        int regionIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_REGION);
        int countryIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_COUNTRY);
        int currencyIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_CURRENCY);
        int latitudeIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_LATITUDE);
        int longitudeIndex = results.getColumnIndex(SavedCitiesOpenHelper.COL_LONGITUDE);


        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            long id = results.getLong(idColIndex);
            String name = results.getString(nameIndex);
            String region = results.getString(regionIndex);
            String country = results.getString(countryIndex);
            String currency = results.getString(currencyIndex);
            double latitude =results.getDouble(latitudeIndex);
            double longitude =results.getDouble(longitudeIndex);
            City city = new City(name,region,country,currency,latitude,longitude);
            city.setId(id);
            //add the City to the array list:
            cities.add(city);
        }
        myListAdapter.notifyDataSetChanged();
    }

    private void deleteSavedCity(long id){
        db.delete(SavedCitiesOpenHelper.TABLE_NAME, SavedCitiesOpenHelper.COL_ID + "= ?", new String[] {Long.toString(id)});
    }





    /**
     *  MyListAdapter for the searched cities
     */
    private class SavedCitiesListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public City getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
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


}