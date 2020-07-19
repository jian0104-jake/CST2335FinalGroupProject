package com.example.cst2335finalgroupproject.SongLyricsSearch;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cst2335finalgroupproject.R;

public class LyricDetailsFragment extends Fragment {

    /**
     * Store information passed from previous activity
     */
    private Bundle dataFromActivity;

    /**
     * The activity that build up fragment.
     */
    private AppCompatActivity parentActivity;

    public LyricDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.lyric_fragment_details, container, false);

        TextView favInfo = result.findViewById(R.id.lyric_fav_information);
        favInfo.setText(dataFromActivity.getString(LyricFavSongActivity.ITEM_SELECTED));

        TextView favContent = result.findViewById(R.id.lyric_fav_content_show);
        favContent.setText(dataFromActivity.getString(LyricFavSongActivity.ITEM_CONTENT));


        Button button = result.findViewById(R.id.lyric_fragment_hide_button);
        button.setOnClickListener(click ->{
            parentActivity.getSupportFragmentManager().beginTransaction()
                    .remove(this).commit();
        });
        return  result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}