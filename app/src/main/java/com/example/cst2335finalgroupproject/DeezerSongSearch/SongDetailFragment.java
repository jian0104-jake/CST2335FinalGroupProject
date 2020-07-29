package com.example.cst2335finalgroupproject.DeezerSongSearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cst2335finalgroupproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDetailFragment extends Fragment {

    public static final String KEY_SONG_NAME = "SONG_NAME";
    public static final String KEY_SONG_DURATION = "SONG_DURATION";
    public static final String KEY_ALBUM_NAME = "ALBUM_NAME";
    public static final String KEY_ALBUM_COVER = "ALBUM_COVER";

    /**
     * song name
     */
    private String songName;
    /**
     * song duration
     */
    private String songDuration;
    /**
     * album name
     */
    private String albumName;
    /**
     * album cover image url
     */
    private String albumCover;

    /**
     * progress bar to show when loading image
     */
    private ProgressBar progressBar;

    /**
     * hold reference to image view to display album cover image
     */
    private ImageView imgAlbumCover;

    /**
     * hold a reference to parent activity
     */
    private AppCompatActivity parentActivity;

    public SongDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songName = getArguments().getString(KEY_SONG_NAME);
            songDuration = getArguments().getString(KEY_SONG_DURATION);
            albumName = getArguments().getString(KEY_ALBUM_NAME);
            albumCover = getArguments().getString(KEY_ALBUM_COVER);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);

        progressBar = view.findViewById(R.id.process_bar);
        imgAlbumCover = view.findViewById(R.id.img_album_cover);

        TextView tvSongName = view.findViewById(R.id.tv_song_name);
        tvSongName.setText(String.format(getString(R.string.song_name_template), songName));

        TextView tvSongDuration = view.findViewById(R.id.tv_song_duration);
        tvSongDuration.setText(String.format(getString(R.string.duration_template), songDuration));

        TextView tvAlbumName = view.findViewById(R.id.tv_album_name);
        tvAlbumName.setText(String.format(getString(R.string.album_name_template), albumName));

        progressBar.setVisibility(View.VISIBLE);

        GetImage getImage = new GetImage();
        getImage.execute(albumCover);

        return view;
    }


    /**
     * get MD5 of a string
     * reference to: https://stackoverflow.com/questions/13152736/how-to-generate-an-md5-checksum-for-a-file-in-android
     */
    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");

            mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
            String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
            while ( md5.length() < 32 ) {
                md5 = "0"+md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
        } // Encryption algorithm
        return "";
    }

    class GetImage extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            String localFile = getMD5EncryptedString(url) + ".jpg";

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
            File file = parentActivity.getBaseContext().getFileStreamPath(fname);
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
            FileOutputStream outputStream = parentActivity.openFileOutput(imagefile, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        }

        private Bitmap loadFromLocalStorage(String imagefile) throws IOException {
            Log.i("WF", "Found in local storage, use it directly");
            try(FileInputStream fis =  parentActivity.openFileInput(imagefile)) {
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                Log.e("DSS", "File not found: " + imagefile);
            }
            return null;
        }
    }


}