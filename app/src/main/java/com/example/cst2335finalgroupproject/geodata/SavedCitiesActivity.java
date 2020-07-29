package com.example.cst2335finalgroupproject.geodata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SoccerMatchHighlights.GameList;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricSearchActivity;
import com.example.cst2335finalgroupproject.geodata.entity.City;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class SavedCitiesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {



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
     * stores the google map component
     */

    private GoogleMap mMap;


    /**
     * stores the location value to pass into google map
     */
    private LatLng cityLocation ;

    /**
     * stores the title value to pass into google map
     */
    private String mapTitle;


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


        //toolbar
        Toolbar toolBar = findViewById(R.id.geo_toolbar);
        setSupportActionBar(toolBar);




        // navigation bar
        DrawerLayout drawerLayout = findViewById(R.id.geo_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolBar, R.string.geo_navigation_open, R.string.geo_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.geo_navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        savedCitiesListView.setOnItemLongClickListener((p,b,pos,id)->{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            City city = cities.get(pos);
            alertDialogBuilder.setTitle( "Do you want to remove " + city.getName() +", "+ city.getRegion() + ", "+city.getCountry() +"？"  )

                    .setPositiveButton(R.string.geo_yes,(click,arg)->{
                        deleteSavedCity(id);
                        cities.remove(pos);
                        if(cityFragment !=null){
                            this.getSupportFragmentManager().beginTransaction().remove(cityFragment).commit();
                        }
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

            mapFragment.getMapAsync(this);

            cityLocation = new LatLng(cities.get(pos).getLatitude(), cities.get(pos).getLongitude());
            mapTitle =cities.get(pos).getName();
        });
    }




    /**
     * Initialize menu tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.geo_toolbar, menu);
        return true;
    }

    /**
     * handle toolbar menu item click event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deezer_toolbar:
                Intent goToGeo = new Intent(this, DeezerSongSearchActivity.class);
                startActivity(goToGeo);
                break;
            case R.id.songLyrics_toolbar:
                Intent goToLyrics = new Intent(this, LyricSearchActivity.class);
                startActivity(goToLyrics);
                break;
            case R.id.soccer_toolbar:
                Intent goToSoccer = new Intent(this, GameList.class);
                startActivity(goToSoccer);
                break;
            case R.id.geo_menu_item_about:
                Toast.makeText(this, R.string.geo_menu_about, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    /**
     * Implement the interface method for navigation items
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.geo_nav_item_help:
                new AlertDialog.Builder(this).setTitle(R.string.help).setMessage(R.string.geo_instruction)
                        .setPositiveButton("OK", (click, arg) ->{
                        })
                        .create().show();

                break;
            case R.id.geo_nav_item_about:
                String apiLink = "https://www.geodatasource.com/web-service";
                Intent launchBrower = new Intent(Intent.ACTION_VIEW, Uri.parse(apiLink));
                startActivity(launchBrower);
                break;
            case R.id.geo_nav_item_donate:
                final EditText etAmount = new EditText(this);
                etAmount.setHint("Enter amount");
                etAmount.setInputType(TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(this).setTitle(R.string.donate_alert_msg).setMessage(R.string.donate_msg)
                        .setView(etAmount)
                        .setPositiveButton(R.string.btn_donate_text, (click, arg) ->{

                        })
                        .setNegativeButton(R.string.btn_cancel_text, null)
                        .show();

                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.geo_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
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





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(cityLocation).title(mapTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cityLocation));
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