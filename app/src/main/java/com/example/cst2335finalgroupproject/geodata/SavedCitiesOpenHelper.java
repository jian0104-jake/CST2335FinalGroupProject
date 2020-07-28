package com.example.cst2335finalgroupproject.geodata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedCitiesOpenHelper extends SQLiteOpenHelper {

    /**
     * The name of database
     */
    protected final static String DATABASE_NAME = "SavedCitiesDB";

    /**
     * The table's name
     */
    protected final static String TABLE_NAME = "SavedCities";

    /**
     * The version of database
     */
    protected final static int VERSION_NUM = 1;

    /**
     * The id of database, should be increment automatically
     */
    protected final static String COL_ID = "_id";

    /**
     * The name of the city
     */
    protected final static String COL_NAME = "Name";


    /**
     * Region name
     */
    protected final static String COL_REGION = "Region";

    /**
     * Country name
     */
    protected final static String COL_COUNTRY = "Country";


    /**
     * Currency
     */
    protected final static String COL_CURRENCY = "Currency";


    /**
     * Latitude
     */
    protected final static String COL_LATITUDE = "Latitude";


    /**
     * Longitude
     */
    protected final static String COL_LONGITUDE = "Longitude";

    /**
     * constructor
     * @param ctx context
     */
    public SavedCitiesOpenHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * This method is called when the database first created
     * @param db database
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " text,"
                + COL_REGION + " text,"
                + COL_COUNTRY + " text,"
                + COL_CURRENCY + " text,"
                + COL_LATITUDE + " decimal,"
                + COL_LONGITUDE + " decimal);");
    }

    /**
     * This method is called when the database is updated.
     * @param db Database
     * @param oldVersion Old version of the database
     * @param newVersion New version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
