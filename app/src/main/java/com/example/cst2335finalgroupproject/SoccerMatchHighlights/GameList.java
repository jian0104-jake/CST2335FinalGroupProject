package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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

public class GameList extends AppCompatActivity {

    private ArrayList<String> elements = new ArrayList<>( Arrays.asList( "One game", "Two game" ) );
    private MyListAdapter myAdapter;
    private String gameTitle;
    private String gameDate;
    private String videoUrl;
    private TextView detailTV;
    List<SoccerDetails> soccerDetailsList = new ArrayList<>();
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        Button btn = findViewById(R.id.showBtn);
        pb = findViewById(R.id.pb1);
        pb.setVisibility(View.VISIBLE);
        String url = "https://www.scorebat.com/video-api/v1/";
        GameListHttpRequest myRequest = new GameListHttpRequest();
        myRequest.execute(url);
        btn.setOnClickListener(b ->{
            Intent goToDe = new Intent(GameList.this,Game_Detail_Activity.class);
            startActivity(goToDe);
        });
        ListView myList = findViewById(R.id.gameList);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("You selected game "+ gameTitle).setMessage("Do you want to go to game details?"
                    ).setPositiveButton("Yes",(click,arg)->{
                Intent goToProfile = new Intent(GameList.this,Game_Detail_Activity.class);
                //goToProfile.putExtra("Email",EmailField.getText().toString());
                startActivity(goToProfile);
                Toast.makeText(this, "you are going to detail page", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("No",(click,arg)->{
                Snackbar.make(btn, "you are going to stay on this page", Snackbar.LENGTH_SHORT).show();
            }).create().show();
        });
    }

    private class SoccerDetails {
        String title;
        String date;
        String vedioUrl;
      public SoccerDetails(String title, String date, String vedioUrl){
          this.date = date;
          this.title = title;
          this.vedioUrl = vedioUrl;
      }

    }

    private class GameListHttpRequest extends AsyncTask< String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(strings[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading:   Look at slide 26
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
                try {
                    for (int i = 0; i < gameDtails.length(); i++) {
                        JSONObject soccerItems = gameDtails.getJSONObject(i);
                        gameTitle = soccerItems.getString("title");
                        videoUrl = soccerItems.getString("url");
                        gameDate = soccerItems.getString("date");
                        soccerDetailsList.add(new SoccerDetails(gameTitle,gameDate,videoUrl));
                    }

                }catch(JSONException e){

                }
            }
            catch (Exception e)
            {

            }
            publishProgress(100);
            return "Done";
        }

        @Override
        public void onProgressUpdate(Integer...value){
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(value[0]);

        }
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


