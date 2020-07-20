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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GameList extends AppCompatActivity {

    private ArrayList<String> elements = new ArrayList<>( Arrays.asList( "One game", "Two game" ) );
    private MyListAdapter myAdapter;
    private String gameTitle;
    private String gameDate;
    private String videoUrl;
    private TextView detailTV;
    private String imageUrl;
    List<SoccerDetails> soccerDetailsList = new ArrayList<>();
    private ProgressBar pb;
    List<String> videoList = new ArrayList<>();
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
            Intent goToDe = new Intent(GameList.this,Favorite_Game_List.class);
            startActivity(goToDe);
        });
        ListView myList = findViewById(R.id.gameList);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener((parent, view, position, id) -> {
            String gtitle = soccerDetailsList.get(position).title;
            String gdate = soccerDetailsList.get(position).date;
            String gurl = soccerDetailsList.get(position).vedioUrl;
            String iurl = soccerDetailsList.get(position).imgUrl;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getResources().getString(R.string.soccer_alert_title) + gtitle).setMessage(R.string.soccer_alert_msg
                    ).setPositiveButton(R.string.soccer_postive,(click, arg)->{
                Intent goToDetail = new Intent(GameList.this,Game_Detail_Activity.class);
                goToDetail.putExtra("gametitle",gtitle);
                goToDetail.putExtra("date",gdate);
                goToDetail.putExtra("gamevedio",gurl);
                goToDetail.putExtra("imageUrl",iurl);
                startActivity(goToDetail);
                Toast.makeText(this, R.string.soccer_toast_txt, Toast.LENGTH_SHORT).show();
            }).setNegativeButton(R.string.soccer_negative,(click, arg)->{
                Snackbar.make(btn, R.string.soccer_snackbar_msg, Snackbar.LENGTH_SHORT).show();
            }).create().show();
        });
    }

    private class SoccerDetails {
        String title;
        String date;
        String vedioUrl;
        String imgUrl;
      public SoccerDetails(String title, String date, String vedioUrl,String imgUrl){
          this.date = date;
          this.title = title;
          this.vedioUrl = vedioUrl;
          this.imgUrl = imgUrl;
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


