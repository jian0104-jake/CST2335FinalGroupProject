package com.example.cst2335finalgroupproject.DeezerSongSearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst2335finalgroupproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * show details of a specific song
 */
public class DeezerSongDetailActivity extends AppCompatActivity {
    public static final String KEY_SONG_NAME = "SONG_NAME";
    public static final String KEY_SONG_DURATION = "SONG_DURATION";
    public static final String KEY_ALBUM_NAME = "ALBUM_NAME";
    public static final String KEY_ALBUM_COVER = "ALBUM_COVER";

    /**
     * progress bar to show when loading image
     */
    private ProgressBar progressBar;

    /**
     * hold reference to image view to display album cover image
     */
    private ImageView imgAlbumCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_detail);
        setTitle(getString(R.string.activity_title_song_detail));

        Intent fromMain = getIntent();
        String songName = fromMain.getStringExtra(KEY_SONG_NAME);
        String songDuration = fromMain.getStringExtra(KEY_SONG_DURATION);
        String albumName = fromMain.getStringExtra(KEY_ALBUM_NAME);
        String albumCover = fromMain.getStringExtra(KEY_ALBUM_COVER);

        progressBar = findViewById(R.id.process_bar);
        imgAlbumCover = findViewById(R.id.imgAlbumCover);

        TextView tvSongName = findViewById(R.id.tvSongName);
        tvSongName.setText(String.format(getString(R.string.song_name_template), songName));

        TextView tvSongDuration = findViewById(R.id.tvSongDuration);
        tvSongDuration.setText(String.format(getString(R.string.duration_template), songDuration));

        TextView tvAlbumName = findViewById(R.id.tvAlbumName);
        tvAlbumName.setText(String.format(getString(R.string.album_name_template), albumName));


        progressBar.setVisibility(View.VISIBLE);
        GetImage getImage = new GetImage();
        getImage.execute(albumCover);
    }

    class GetImage extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            String[] parts = url.split("/");
            String localFile = parts[parts.length - 1];
            try {
                if (fileExistance(localFile)) {
                    return loadFromLocalStorage(localFile);
                } else {
                    return loadImageFromRemote(url, localFile);
                }
            } catch(IOException ioe) {
                Log.e("DSS", "Failed to load image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imgAlbumCover.setImageBitmap(bitmap);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

        public boolean fileExistance(String fname) {
            Log.i("DSS", "Image file to find: " + fname);
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        private Bitmap loadImageFromRemote(String sUrl, String imagefile) throws IOException {
            Bitmap bm = null;

            URL url = new URL(sUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                bm = BitmapFactory.decodeStream(connection.getInputStream());
                saveToLocalStorage(imagefile, bm);
            }
            return bm;
        }

        private void saveToLocalStorage(String imagefile, Bitmap image) throws IOException {
            FileOutputStream outputStream = openFileOutput(imagefile, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        }

        private Bitmap loadFromLocalStorage(String imagefile) throws IOException {
            Log.i("WF", "Found in local storage, use it directly");
            try(FileInputStream fis =  openFileInput(imagefile)) {
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                Log.e("DSS", "File not found: " + imagefile);
            }
            return null;
        }
    }



}