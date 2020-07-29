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
import android.widget.ImageButton;
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

/**
 * @Author:ZiyueWang
 * @date:07/28/2020
 *
 * class Game detail activity.
 *
 */
public class GameDetailActivity extends AppCompatActivity {

    private Button saveBtn, goToFavBtn;
    private ProgressBar pb2;
    private TextView teamName;
    private TextView gameDate;
    private TextView gameVedioUrl;
    private ImageButton imageButton;
    private String imageUrl;
    private Button goWacthBtn;
    private SQLiteDatabase db;

    /**
     * when user click the yes button to check the game detail,
     * if is on a phone , the compiler is going to look at this method.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        Bundle dataToPass = getIntent().getExtras();

        SoccerDetailsFragment dFragment = new SoccerDetailsFragment();
        dFragment.setArguments(dataToPass); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.soc_fragmentLocation, dFragment)
                .commit();
    }
}
