package com.example.cst2335finalgroupproject.SongLyricsSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cst2335finalgroupproject.R;

import java.util.ArrayList;

public class FavSongActivity extends AppCompatActivity {

    /**
     *  The implemented adapter for list view
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_song_list);

        listView = findViewById(R.id.fav_song_list);
        listView.setAdapter(myAdapter = new MyListAdapter());

        favSongDB = new FavSongDB(this);
        sqLiteDatabase = favSongDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(FavSongDB.TABLE_NAME,
                new String[]{FavSongDB.COL_ID, FavSongDB.COL_ARTIST, FavSongDB.COL_TITLE},
                null, null, null, null, FavSongDB.COL_ID);
        if (cursor.moveToNext()) {
            elements.add(new FavLyricsEntity(cursor.getString(cursor.getColumnIndex(FavSongDB.COL_ARTIST)),
                    cursor.getString(cursor.getColumnIndex(FavSongDB.COL_TITLE)),
                    cursor.getLong(cursor.getColumnIndex(FavSongDB.COL_ID))));
        }



        listView.setSelection(elements.size());

        listView.setOnItemLongClickListener((parent, view, pos, id) -> {

            // build up a AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to delete this?");
            builder.setMessage("The selected row is: " + pos + "\nThe database id is: " + id);

            builder.setPositiveButton("Delete", (dialog, which) -> {
                elements.remove(pos);
                sqLiteDatabase.delete(FavSongDB.TABLE_NAME, FavSongDB.COL_ID + " = ? ",
                        new String[]{ String.valueOf(id)});
                myAdapter.notifyDataSetChanged();
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
                newView = inflater.inflate(R.layout.search_history, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.search_history_layout = newView.findViewById(R.id.search_history_layout);
                viewHolder.search_history_text = newView.findViewById(R.id.search_history_text);
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
}