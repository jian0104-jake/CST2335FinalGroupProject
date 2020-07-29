package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricSearchActivity;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:ZiyueWang
 * @date:07/28/2020
 *
 */
public class GameList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private MyListAdapter myAdapter;
    private String gameTitle;
    private String gameDate;
    private String videoUrl;
    private TextView detailTV;
    private String imageUrl;
    /**
     * The Soccer details list.
     */
    List<SoccerDetails> soccerDetailsList = new ArrayList<>();
    private ProgressBar pb;
    /**
     * The Video list.
     */
    List<String> videoList = new ArrayList<>();

    /**
     *this method is used to  accomplish the function of all buttons, and to determine whether to use fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        boolean isTablet = findViewById(R.id.soc_fragmentLocation) != null;
        if(!isTablet){
        Toolbar tBar = findViewById(R.id.soc_list_toolbar);
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.list_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.soc_list_nav);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);}
        Button btn = findViewById(R.id.showBtn);
        pb = findViewById(R.id.pb1);
        pb.setVisibility(View.VISIBLE);
        String url = "https://www.scorebat.com/video-api/v1/";
        GameListHttpRequest myRequest = new GameListHttpRequest();
        myRequest.execute(url);
        btn.setOnClickListener(b ->{
            Intent goToDe = new Intent(GameList.this,Favorite_Game_List.class);
            startActivity(goToDe);
        });

        ListView myList = findViewById(R.id.gameList);
        myList.setAdapter( myAdapter = new MyListAdapter());

        myList.setOnItemClickListener(((parent, view, position, id) -> {
            Bundle dataToPass = new Bundle();

            String gtitle = soccerDetailsList.get(position).title;
            String gdate = soccerDetailsList.get(position).date;
            String gurl = soccerDetailsList.get(position).vedioUrl;
            String iurl = soccerDetailsList.get(position).imgUrl;

            String source = "listPage";
            dataToPass.putString("gametitle", gtitle);
            dataToPass.putString("date", gdate);
            dataToPass.putString("gamevedio", gurl);
            dataToPass.putString("imageUrl", iurl);
            dataToPass.putString("sourcePage",source);
            if (isTablet) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getResources().getString(R.string.soccer_alert_title) + gtitle).setMessage(R.string.soccer_alert_msg
                ).setPositiveButton(R.string.soccer_postive, (click, arg) -> {
                    SoccerDetailsFragment dFragment = new SoccerDetailsFragment(); //add a DetailFragment
                    dFragment.setArguments(dataToPass); //pass it a bundle for information
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.soc_fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .commit();
                    Toast.makeText(this, R.string.soccer_toast_txt, Toast.LENGTH_SHORT).show();
                }).setNegativeButton(R.string.soccer_negative, (click, arg) -> {
                    Snackbar.make(btn, R.string.soccer_snackbar_msg, Snackbar.LENGTH_SHORT).show();
                }).create().show();

            } else {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getResources().getString(R.string.soccer_alert_title) + gtitle).setMessage(R.string.soccer_alert_msg
                ).setPositiveButton(R.string.soccer_postive, (click, arg) -> {
                    Intent nextActivity = new Intent(this, GameDetailActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivity(nextActivity); //make the transition
                    Toast.makeText(this, R.string.soccer_toast_txt, Toast.LENGTH_SHORT).show();
                }).setNegativeButton(R.string.soccer_negative, (click, arg) -> {
                    Snackbar.make(btn, R.string.soccer_snackbar_msg, Snackbar.LENGTH_SHORT).show();
                }).create().show();
            }
        }));
    }

    /**
     * this method is used to inflate tool bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * this method is used to finish different function when user
     * selected different menu on toolbar
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.geo_toolbar:
                Intent goToGeo = new Intent(this, GeoDataSource.class);
                startActivity(goToGeo);
                break;
            case R.id.songLyrics_toolbar:
                Intent goToLyrics = new Intent(this, LyricSearchActivity.class);
                startActivity(goToLyrics);
                break;
            case R.id.deezer_toolbar:
                Intent goToDeezer = new Intent(this, DeezerSongSearchActivity.class);
                startActivity(goToDeezer);
                break;
            case R.id.help_item:
                Toast.makeText(this, R.string.soc_tbar_msg, Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    /**
     * this method is used to finish different function when user
     * selected different menu on navigation drawer
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            case R.id.api:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse("https://www.scorebat.com/video-api/") );
                startActivity(i);
                break;
            case R.id.instruction:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.soc_instruction_title).setMessage(R.string.soc_intro_msg
                ).setPositiveButton(R.string.soc_intro_positive, (click, arg) -> {})
                   .create().show();
                break;
            case R.id.donate:
                final EditText et = new EditText(this);
                et.setHint("$$$");

                new AlertDialog.Builder(this).setTitle(R.string.donate_alert_msg).setMessage(R.string.donate_msg)
                        .setView(et)
                        .setPositiveButton("Thank you", (click,arg) ->{

                        })
                        .setNegativeButton("cancel", null)
                        .show();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.list_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    /**
     * class SoccerDetials
     * this class is used to store details of soccer game
     */

    private class SoccerDetails {
        /**
         * The Title.
         */
        String title;
        /**
         * The Date.
         */
        String date;
        /**
         * The Vedio url.
         */
        String vedioUrl;
        /**
         * The Img url.
         */
        String imgUrl;

        /**
         * Instantiates a new Soccer details.
         *
         * @param title    the title
         * @param date     the date
         * @param vedioUrl the vedio url
         * @param imgUrl   the img url
         */
        public SoccerDetails(String title, String date, String vedioUrl,String imgUrl){
          this.date = date;
          this.title = title;
          this.vedioUrl = vedioUrl;
          this.imgUrl = imgUrl;
      }

    }

    /**
     *class GameListHttpRequest
     * this class is used to read and access the url and get the data back
     */

    private class GameListHttpRequest extends AsyncTask< String, Integer, String> {
        /**
         * Read a JsonArray and get the data back
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(strings[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                publishProgress(50);
                String result = sb.toString(); //result is the whole string
                JSONArray gameDtails = new JSONArray(result);
                String gurl;
                try {
                    for (int i = 0; i < gameDtails.length(); i++) {
                        JSONObject soccerItems = gameDtails.getJSONObject(i);
                        JSONArray vediolist  = soccerItems.getJSONArray("videos");
                        for(int k =0 ; k < vediolist.length();k++){
                            JSONObject embedObject = vediolist.getJSONObject(k);
                            String e = embedObject.getString("embed");
                            String[] x = e.split("frame");
                            videoUrl = x[1].substring(6,x[1].length()-2);
                        }
                        gameTitle = soccerItems.getString("title");
                        gameDate = soccerItems.getString("date");
                        imageUrl = soccerItems.getString("thumbnail");
                        soccerDetailsList.add(new SoccerDetails(gameTitle,gameDate,videoUrl,imageUrl));
                    }
                    publishProgress(70);

                }catch(JSONException e){

                }
            }
            catch (Exception e)
            {

            }
            publishProgress(100);
            return "Done";
        }

        /**
         * set the progress bar visible
         * @param value
         */

        @Override
        public void onProgressUpdate(Integer...value){
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(value[0]);

        }

        /**
         * set progress bar invisible and notify the listview data changed after http request executed
         * @param fromDoInBackground
         */
        @Override
        public void onPostExecute(String fromDoInBackground)
        {
            pb.setVisibility(View.INVISIBLE);
            myAdapter.notifyDataSetChanged();

        }
    }
    private class MyListAdapter extends BaseAdapter {

       public int getCount() { return soccerDetailsList.size();}
       public SoccerDetails getItem(int position) { return  soccerDetailsList.get(position); }

        public long getItemId(int position) { return position ; }

        /**
         * set the text of each listview based on the title in soccerDetailsList
         * @param position
         * @param old
         * @param parent
         * @return
         */

        @Override
        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.detail_layout, parent, false);

            //set what the text should be for this row:
            detailTV = newView.findViewById(R.id.detailTV);
            detailTV.setText( getItem(position).title );

            //return it to be put in the table
            return newView;
        }
    }

    }


