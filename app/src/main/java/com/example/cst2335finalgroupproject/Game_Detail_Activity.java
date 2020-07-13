package com.example.cst2335finalgroupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Game_Detail_Activity extends AppCompatActivity {
    private Button saveBtn;
    private ProgressBar pb2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(b->{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Save as favorite? ").setMessage("would you like save this game to your favorite list?"
            ).setPositiveButton("Yes",(click,arg)->{

                Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("No",(click,arg)->{
                Snackbar.make(saveBtn,"you selected no", Snackbar.LENGTH_SHORT).show();
            }).create().show();
        });
        pb2 = findViewById(R.id.pb2);
        pb2.setVisibility(View.VISIBLE);
    }

//    private class GameListHttpRequest extends AsyncTask< String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//             publishProgress(100);
//            return "Done";
//        }
//
//        @Override
//        public void onProgressUpdate(Integer...value){
//            pb2.setVisibility(View.VISIBLE);
//            pb2.setProgress(value[0]);
//
//        }
//        @Override
//        public void onPostExecute(String fromDoInBackground)
//        {   //myList
//            pb2.setVisibility(View.INVISIBLE);
//
//        }
//    }
}