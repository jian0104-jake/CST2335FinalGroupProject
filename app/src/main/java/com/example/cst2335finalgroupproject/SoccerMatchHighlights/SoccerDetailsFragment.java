package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricsSearchActivity;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SoccerDetailsFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private Button saveOrRemoveBtn,goToFavBtn;
    private ProgressBar pb2;
    private TextView teamName;
    private TextView gameDate;
    private TextView gameVedioUrl;
    private ImageButton imageButton;
    private String imageUrl;
    private Button goWacthBtn;
    private SQLiteDatabase db ;
    private Bundle dataFromActivity;
    public SharedPreferences prefs;



    public SoccerDetailsFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        View result = inflater.inflate(R.layout.fragment_soccer_details, container, false);
        Toolbar tbar = result.findViewById(R.id.soc_fragdetail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbar);


            DrawerLayout drawer = result.findViewById(R.id.fragtail_drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(),
                    drawer, tbar, R.string.open, R.string.close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = result.findViewById(R.id.soc_fragdetail_nav);
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
        imageButton = result.findViewById(R.id.soccer_img_btn);
        teamName = result.findViewById(R.id.teamName);
        gameDate = result.findViewById(R.id.soc_game_date);
        gameVedioUrl = result.findViewById(R.id.videoUrl);
        teamName.setText(dataFromActivity.getString("gametitle"));
        gameDate.setText(dataFromActivity.getString("date"));
        gameVedioUrl.setText(dataFromActivity.getString("gamevedio"));
        imageUrl = dataFromActivity.getString("imageUrl");
        GameImageHttpRequest req = new GameImageHttpRequest();
        req.execute(imageUrl);
        SoccerDB soccerDB = new SoccerDB(this.getContext());
        db = soccerDB.getWritableDatabase();

        String twoTeam = teamName.getText().toString();
        String date = gameDate.getText().toString();
        String gameUrl = gameVedioUrl.getText().toString();
        String source = dataFromActivity.getString("sourcePage");
        saveOrRemoveBtn = result.findViewById(R.id.soc_saveBtn);
        if(source.equals("listPage")){
            saveOrRemoveBtn.setText("Save to favorite list");
            saveOrRemoveBtn.setOnClickListener(b->{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setTitle("Save as favorite? ").setMessage("would you like save this game to your favorite list?"
                ).setPositiveButton("Yes",(click,arg)->{
                    ContentValues newRowValue = new ContentValues();
                    newRowValue.put(SoccerDB.TEAM_COL,twoTeam);
                    newRowValue.put(SoccerDB.DATE_COL,date);
                    newRowValue.put(SoccerDB.URL_COL,gameUrl);
                    newRowValue.put(SoccerDB.IMG_COL,imageUrl);
                    long id = db.insert(SoccerDB.TABLE_NAME,null,newRowValue);
                    if(id>0)//if database insertion fails, id = -1
                        Toast.makeText(this.getContext(), "saved successfully", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("No",(click,arg)->{
                    Snackbar.make(saveOrRemoveBtn,"you selected no", Snackbar.LENGTH_SHORT).show();
                }).create().show();
            });
        }else if(source.equals("favList")){
            saveOrRemoveBtn.setText("Remove From the favorite list");
            saveOrRemoveBtn.setOnClickListener(click->{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setTitle((R.string.soc_removeQue)).setMessage(R.string.soc_removeMsg
                ).setPositiveButton("Yes",(c,arg)->{
                    db.delete(SoccerDB.TABLE_NAME,SoccerDB.TEAM_COL + "=?",new String[]{twoTeam});
                    Toast.makeText(this.getContext(),R.string.soc_removeToast,Toast.LENGTH_SHORT).show();


                }).setNegativeButton("No",(c,arg)->{
                    Snackbar.make(saveOrRemoveBtn,"you selected no", Snackbar.LENGTH_SHORT).show();
                }).create().show();
            });

        }


        goToFavBtn = result.findViewById(R.id.soc_goFav);
        goToFavBtn.setOnClickListener(b->{
            Intent goToFav = new Intent(this.getContext(),Favorite_Game_List.class);
            startActivity(goToFav);
        });
//        goWacthBtn = result.findViewById(R.id.soc_goWacthLive);
//        goWacthBtn.setOnClickListener(click->{
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData( Uri.parse(gameUrl) );startActivity(i);
//        });
           prefs = this.getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);

        imageButton.setOnClickListener(click->{
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("gameUrl",gameUrl);
            editor.commit();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(gameUrl) );
            startActivity(i);
        });

        setHasOptionsMenu(true);

        return result;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.geo_toolbar:
                Intent goToGeo = new Intent(this.getContext(), GeoDataSource.class);
                startActivity(goToGeo);
                break;
            case R.id.songLyrics_toolbar:
                Intent goToLyrics = new Intent(this.getContext(), LyricsSearchActivity.class);
                startActivity(goToLyrics);
                break;
            case R.id.deezer_toolbar:
                Intent goToDeezer = new Intent(this.getContext(), DeezerSongSearchActivity.class);
                startActivity(goToDeezer);
                break;
            case R.id.help_item:
                Toast.makeText(this.getContext(), R.string.soc_tbar_msg, Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.api:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse("https://www.scorebat.com/video-api/") );
                startActivity(i);
                break;
            case R.id.instruction:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setTitle(R.string.soc_instruction_title).setMessage(R.string.soc_intro_msg
                ).setPositiveButton(R.string.soc_intro_positive, (click, arg) -> {})
                        .create().show();
            case R.id.donate:
                final EditText et = new EditText(this.getContext());
                et.setHint("$$$");

                new AlertDialog.Builder(this.getContext()).setTitle(R.string.donate_alert_msg).setMessage(R.string.donate_msg)
                        .setView(et)
                        .setPositiveButton("Thank you", (click,arg) ->{

                        })
                        .setNegativeButton("cancel", null)
                        .show();

        }
        //DrawerLayout drawerLayout = result.findViewById(R.id.list_drawer_layout);
       // drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private class GameImageHttpRequest extends AsyncTask< String, Integer, String> {
        private Bitmap image = null;
        @Override
        protected String doInBackground(String... strings) {
            try {
                // URL imgUrl = new URL(imageUrl);
                URL imgUrl = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) imgUrl.openConnection();
                urlConnection.connect();
               // publishProgress(25);
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    //publishProgress(50);
                }
                //publishProgress(100);
                return "Done";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";
        }

        @Override
        public void onProgressUpdate(Integer...value){
           // pb2.setVisibility(View.VISIBLE);
           // pb2.setProgress(value[0]);

        }
        @Override
        public void onPostExecute(String fromDoInBackground)
        {   //myList
            imageButton.setImageBitmap(image);
           // pb2.setVisibility(View.INVISIBLE);

        }
    }

}