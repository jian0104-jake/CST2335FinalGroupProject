package com.example.cst2335finalgroupproject.geodata;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class GeoUtil {

    public static void openGoogleNavi(String lat, String lng, AppCompatActivity activity) {

        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(mapIntent);
        }
    }
}
