package com.example.cst2335finalgroupproject.SoccerMatchHighlights;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Favorite_Game_List extends AppCompatActivity {
    private List<FavSoccerDetails>  favSoccerList = new ArrayList();
    private SQLiteDatabase db;
    private TextView favTV;
    private  MyListAdapter myAdapter;
    private Button goToListbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_game_list);
        loadfromDB();
        ListView myList = findViewById(R.id.soc_fav_list);
        myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemClickListener((parent, view, position, id)->{
            String gtitle = favSoccerList.get(position).title;
            String gdate = favSoccerList.get(position).date;
            String gurl = favSoccerList.get(position).vedioUrl;
            String iurl = favSoccerList.get(position).imgUrl;

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getResources().getString(R.string.soccer_alert_title) + gtitle).setMessage(R.string.soccer_alert_msg
            ).setPositiveButton(R.string.soccer_postive,(click, arg)->{
                Intent goToDetail = new Intent(this,Game_Detail_Activity.class);
                goToDetail.putExtra("gametitle",gtitle);
                goToDetail.putExtra("date",gdate);
                goToDetail.putExtra("gamevedio",gurl);
                goToDetail.putExtra("imageUrl",iurl);
                startActivity(goToDetail);
                Toast.makeText(this, R.string.soccer_toast_txt, Toast.LENGTH_SHORT).show();
            }).setNegativeButton(R.string.soccer_negative,(click, arg)->{
                Snackbar.make(myList, R.string.soccer_snackbar_msg, Snackbar.LENGTH_SHORT).show();
            }) .setNeutralButton(R.string.soccer_neu,(click, arg)->{

                db.delete(SoccerDB.TABLE_NAME,SoccerDB.TEAM_COL + "=?",new String[]{favSoccerList.get(position).title});
                 favSoccerList.remove(position);
                 Snackbar.make(myList, R.string.soc_delete_msg, Snackbar.LENGTH_SHORT).show();
                 myAdapter.notifyDataSetChanged();
            }).create().show();
        });
        goToListbtn = findViewById(R.id.soc_gohome_btn);
        goToListbtn.setOnClickListener(b->{
            Intent goToList = new Intent(this,GameList.class);
            startActivity(goToList);
        });

    }
    class FavSoccerDetails{
        String title;
        String date;
        String vedioUrl;
        String imgUrl;

        public FavSoccerDetails(String title, String date, String vedioUrl,String imgUrl){
            this.date = date;
            this.title = title;
            this.vedioUrl = vedioUrl;
            this.imgUrl = imgUrl;


        }
    }


    private void loadfromDB(){
        SoccerDB dbOpener = new SoccerDB(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {SoccerDB.TEAM_COL, SoccerDB.DATE_COL,SoccerDB.IMG_COL,SoccerDB.URL_COL,};
        Cursor results = db.query(false, SoccerDB.TABLE_NAME, columns, null, null, null, null, null, null);

        int nameColIndx = results.getColumnIndex(SoccerDB.TEAM_COL);
        int dateColIndx = results.getColumnIndex(SoccerDB.DATE_COL);
        int urlColIndx = results.getColumnIndex(SoccerDB.URL_COL);
        int imgColIndx = results.getColumnIndex(SoccerDB.IMG_COL);

        while (results.moveToNext()){
            String team = results.getString(nameColIndx);
            String  date = results.getString(dateColIndx);
            String url = results.getString(urlColIndx);
            String img = results.getString(imgColIndx);
           //long id = results.getLong(idColIndx);
            favSoccerList.add(new FavSoccerDetails(team,date,url,img));}
    }
    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return favSoccerList.size();}




        public FavSoccerDetails getItem(int position) { return  favSoccerList.get(position); }

        public long getItemId(int position) { return position ; }

        @Override
        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.soc_fav_list_rowlayout, parent, false);

            //set what the text should be for this row:
           favTV = newView.findViewById(R.id.soc_fav_detailTV);
           favTV.setText( getItem(position).title );

            //return it to be put in the table
            return newView;
        }
    }
}