package com.example.cst2335finalgroupproject.SongLyricsSearch;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SoccerMatchHighlights.GameList;
import com.example.cst2335finalgroupproject.SongLyricsSearch.Database.FavSongDB;
import com.example.cst2335finalgroupproject.SongLyricsSearch.Entity.FavLyricsEntity;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class LyricFavSongActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The implemented adapter for list view
     */
    private MyListAdapter myAdapter;

    /**
     * A list view used to display search history
     */
    private ListView listView;

    /**
     * A list store the content of list view
     */
    private ArrayList<FavLyricsEntity> elements = new ArrayList<>();

    /**
     * An instance of favorite song database class
     */
    private FavSongDB favSongDB;

    /**
     * An object of SQLiteDatabase which is SQL util class
     */
    private SQLiteDatabase sqLiteDatabase;

    /**
     * variables used for fragment
     */
    public static final String ITEM_SELECTED = "SONG";
    public static final String ITEM_CONTENT = "CONTENT";
    public static final String ITEM_ID = "ID";

    /**
     * store fragment instance after click item in list view.
     */
    private Fragment lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_fav_song_list);

        // tool bar
        Toolbar toolBar = findViewById(R.id.lyric_toolbar);
        setSupportActionBar(toolBar);

        // navigation bar
        DrawerLayout drawerLayout = findViewById(R.id.lyric_fav_drawer_layout);
        Log.i("drawerLayout", drawerLayout == null ? "yes" : "no");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolBar, R.string.lyric_navigation_open, R.string.lyric_navigation_close);
        Log.i("toggle", drawerLayout == null ? "yes" : "no");
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.lyric_navigation_view);
        // without this line, the image will not display in navigation bar
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        Button button = findViewById(R.id.lyric_button_back_to_front);
        button.setOnClickListener(click -> {
            Intent backToSearch = new Intent(LyricFavSongActivity.this, LyricSearchActivity.class);
            startActivity(backToSearch);
        });

        listView = findViewById(R.id.lyric_fav_song_list);
        listView.setAdapter(myAdapter = new MyListAdapter());

        favSongDB = new FavSongDB(this);
        sqLiteDatabase = favSongDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(FavSongDB.TABLE_NAME,
                new String[]{FavSongDB.COL_ID, FavSongDB.COL_ARTIST, FavSongDB.COL_TITLE, FavSongDB.COL_CONTENT},
                null, null, null, null, FavSongDB.COL_ID);
        int i = 0;
        while (cursor.moveToNext()) {
            elements.add(new FavLyricsEntity(cursor.getString(cursor.getColumnIndex(FavSongDB.COL_ARTIST)),
                    cursor.getString(cursor.getColumnIndex(FavSongDB.COL_TITLE)),
                    cursor.getLong(cursor.getColumnIndex(FavSongDB.COL_ID)),
                    cursor.getString(cursor.getColumnIndex(FavSongDB.COL_CONTENT))));
        }

        boolean isTablet = findViewById(R.id.lyric_fav_song_content_frame_layout) != null;

        listView.setSelection(elements.size());

        listView.setOnItemClickListener((list, item, position, id) -> {

            LyricDetailsFragment dFragment = new LyricDetailsFragment();
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, elements.get(position).toString());
            dataToPass.putString(ITEM_CONTENT, elements.get(position).getContent());
            dataToPass.putString(ITEM_ID, "Database ID: " + id);

            if (isTablet) {
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.lyric_fav_song_content_frame_layout, dFragment)
                        .commit();
            } else {
                Intent nextActivity = new Intent(LyricFavSongActivity.this, LyricEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

            // store the fragment instance
            lastFragment = dFragment;
        });

        listView.setOnItemLongClickListener((parent, view, pos, id) -> {

            // build up a AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to delete this?");
            builder.setMessage("The selected row is: " + pos + "\nThe database id is: " + id);

            builder.setPositiveButton("Delete", (dialog, which) -> {
                elements.remove(pos);
                sqLiteDatabase.delete(FavSongDB.TABLE_NAME, FavSongDB.COL_ID + " = ? ",
                        new String[]{String.valueOf(id)});
                myAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Delete Successfully", Toast.LENGTH_LONG).show();

                // Only when fragment is displayed, long click will delete the fragment
                // avoid the crush when directly delete from list view without click to show the fragment.
                if (lastFragment != null) {
                    if (isTablet) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(
                                        getSupportFragmentManager()
                                                .findFragmentById(R.id.lyric_fav_song_content_frame_layout))
                                .commit();
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        });

    }

    /**
     * Implement the BaseAdapter interface
     * Provide data to fill and update list view
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public FavLyricsEntity getItem(int position) {
            return elements.get(position);
        }

        public long getItemId(int position) {
            return elements.get(position).getDbId();
        }

        public View getView(int position, View old, ViewGroup parent) {

            FavLyricsEntity favLyricsEntity = getItem(position);
            View newView;
            ViewHolder viewHolder;

            LayoutInflater inflater = getLayoutInflater();

            if (old == null) {
                newView = inflater.inflate(R.layout.lyric_search_history, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.search_history_layout = newView.findViewById(R.id.lyric_search_history_layout);
                viewHolder.search_history_text = newView.findViewById(R.id.lyric_search_history_text);
                newView.setTag(viewHolder);
            } else {
                newView = old;
                viewHolder = (ViewHolder) newView.getTag();
            }

            viewHolder.search_history_layout.setVisibility(View.VISIBLE);
            viewHolder.search_history_text.setText(favLyricsEntity.toString());

            return newView;
        }

        /**
         * Hold the List view place
         */
        class ViewHolder {
            LinearLayout search_history_layout;
            TextView search_history_text;
        }
    }

    /**
     * Used to display tool bar items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lyric_toolbar_items, menu);
        return true;
    }

    /**
     * Used to capture and react with tool bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.lyric_toolbar_goto_findcity:
                Intent goToGeoData = new Intent(LyricFavSongActivity.this, GeoDataSource.class);
                startActivity(goToGeoData);
                break;
            case R.id.lyric_toolbar_goto_soccerhighlight:
                Intent goToSoccer = new Intent(LyricFavSongActivity.this, GameList.class);
                startActivity(goToSoccer);
                break;
            case R.id.lyric_toolbar_goto_deezer:
                Intent goToDeezer = new Intent(LyricFavSongActivity.this, DeezerSongSearchActivity.class);
                startActivity(goToDeezer);
                break;
            case R.id.lyric_toolbar_overflow:
                Toast.makeText(this, "This is the Lyrics Search activity, written by Eric Wu", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * Implement interface method, to reactive with navigation items
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.lyric_navigation_help_item:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setTitle(R.string.lyric_navagation_help_title);
//                helpBuilder.setMessage(R.string.lyric_navagation_help_search);

                TextView textView = new TextView(this);
                textView.setText(R.string.lyric_navagation_help_search);

                // set up two buttons
                helpBuilder.setPositiveButton("OK", null);

                // create and show the dialog
                AlertDialog helpAlertDialog = helpBuilder.create();
                helpAlertDialog.setView(textView, 0, 0, 0, 0);
                helpAlertDialog.show();
                break;
            case R.id.lyric_navigation_api_item:
                String apiLink = "https://lyricsovh.docs.apiary.io/#";
                Intent launchBrower = new Intent(Intent.ACTION_VIEW, Uri.parse(apiLink));
                startActivity(launchBrower);
                break;
            case R.id.lyric_navigation_donate_item:
                AlertDialog.Builder donationBuilder = new AlertDialog.Builder(this);
                donationBuilder.setTitle(R.string.lyric_navigation_donate_title);
                donationBuilder.setMessage(R.string.lyric_navigation_donate_number);

                EditText editText = new EditText(this);
                editText.setHint(R.string.lyric_navigation_donate_number);
                editText.setInputType(TYPE_CLASS_NUMBER);

                // set up two buttons
                donationBuilder.setPositiveButton("Thank You", null);
                donationBuilder.setNegativeButton("Cancel", null);

                // create and show the dialog
                AlertDialog donationAlertDialog = donationBuilder.create();
                donationAlertDialog.setView(editText, 0, 0, 0, 0);

                donationAlertDialog.setOnShowListener(dialog -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                });
                donationAlertDialog.show();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.lyric_fav_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}