package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricsSearchActivity;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SoccerDetailsFragment extends Fragment {

    private Button saveBtn,goToFavBtn;
    private ProgressBar pb2;
    private TextView teamName;
    private TextView gameDate;
    private TextView gameVedioUrl;
    private ImageButton imageButton;
    private String imageUrl;
    private Button goWacthBtn;
    private SQLiteDatabase db ;
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;

    public SoccerDetailsFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        View result = inflater.inflate(R.layout.fragment_soccer_details, container, false);
        Toolbar tbar = result.findViewById(R.id.soc_fragdetail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbar);
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
        saveBtn = result.findViewById(R.id.soc_saveBtn);
        //if save button is clicked,  data is going to be saved in database
        saveBtn.setOnClickListener(b->{
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
                Snackbar.make(saveBtn,"you selected no", Snackbar.LENGTH_SHORT).show();
            }).create().show();
        });
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
        imageButton.setOnClickListener(click->{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(gameUrl) );startActivity(i);
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
        parentActivity = (AppCompatActivity)context;
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