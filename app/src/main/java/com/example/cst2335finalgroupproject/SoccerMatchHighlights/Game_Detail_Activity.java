package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.cst2335finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Game_Detail_Activity extends AppCompatActivity {
    private Button saveBtn,goToFavBtn;
    private ProgressBar pb2;
    private TextView teamName;
    private TextView gameDate;
    private TextView gameVedioUrl;
    private ImageView imageView;
    private String gameUrl,imageUrl;
    private VideoView videoView;
    private MediaController mc;
    private Button playBtn,go_wacth_btn;
    private SQLiteDatabase db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        setText();
        GameImageHttpRequest req = new GameImageHttpRequest();
        req.execute(imageUrl);
        SoccerDB soccerDB = new SoccerDB(this);
        db = soccerDB.getWritableDatabase();
        playBtn = findViewById(R.id.start);
        playBtn.setOnClickListener(b->{
            playVedio();
        });
        String twoTeam = teamName.getText().toString();
        String date = gameDate.getText().toString();
        String gameUrl = gameVedioUrl.getText().toString();
        saveBtn = findViewById(R.id.soc_saveBtn);
        saveBtn.setOnClickListener(b->{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Save as favorite? ").setMessage("would you like save this game to your favorite list?"
            ).setPositiveButton("Yes",(click,arg)->{
                ContentValues newRowValue = new ContentValues();
                newRowValue.put(SoccerDB.TEAM_COL,twoTeam);
                newRowValue.put(SoccerDB.DATE_COL,date);
                newRowValue.put(SoccerDB.URL_COL,gameUrl);
                newRowValue.put(SoccerDB.IMG_COL,imageUrl);
                 long id = db.insert(SoccerDB.TABLE_NAME,null,newRowValue);
                 if(id>0)
                Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("No",(click,arg)->{
                Snackbar.make(saveBtn,"you selected no", Snackbar.LENGTH_SHORT).show();
            }).create().show();
        });
        goToFavBtn = findViewById(R.id.soc_goFav);
        goToFavBtn.setOnClickListener(b->{
            Intent goToFav = new Intent(this,Favorite_Game_List.class);
            startActivity(goToFav);
        });
        go_wacth_btn = findViewById(R.id.soc_goWacthLive);
        go_wacth_btn.setOnClickListener(click->{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(gameUrl) );startActivity(i);
        });


        pb2 = findViewById(R.id.pb2);
        pb2.setVisibility(View.VISIBLE);
    }
    private void playVedio(){
        videoView = findViewById(R.id.soccer_video);
        gameUrl = gameVedioUrl.getText().toString();
        Uri uri = Uri.parse("https://www.scorebat.com/embed/v/5f0dfb0f9ec5b/?s=2");
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

    }
    private void setText(){
        imageView = findViewById(R.id.soccer_img_view);
        teamName = findViewById(R.id.teamName);
        gameDate = findViewById(R.id.soc_game_date);
        gameVedioUrl = findViewById(R.id.videoUrl);
        Intent listIntent = getIntent();
        teamName.setText(listIntent.getStringExtra("gametitle"));
        gameDate.setText(getResources().getString(R.string.soccer_dateTXT) + listIntent.getStringExtra("date"));
        gameVedioUrl.setText(listIntent.getStringExtra("gamevedio"));
        imageUrl = listIntent.getStringExtra("imageUrl");

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
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    publishProgress(100);
                   // FileOutputStream outputStream = openFileOutput(iconName + ".jpg", Context.MODE_PRIVATE);
                   // image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                   // outputStream.flush();
                    //outputStream.close();
                }

                publishProgress(100);
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
            pb2.setVisibility(View.VISIBLE);
            pb2.setProgress(value[0]);

        }
        @Override
        public void onPostExecute(String fromDoInBackground)
        {   //myList
            imageView.setImageBitmap(image);
            pb2.setVisibility(View.INVISIBLE);

        }
    }
}