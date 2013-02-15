package com.appsolut.composition.utils;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "butthurt";
 
    // Composition table name
    private static final String TABLE_COMPOSITIONS = "compositions";
 
    // Composition Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE_CREATED = "dateCreated";
    private static final String KEY_LOC_RECORDING = "locRecording";
    private static final String KEY_LOC_MIDI = "locMIDI";
    private static final String KEY_LOC_SHEET = "locSheet";
    private static final String KEY_LOC_INFO = "locInfo";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_COMPOSITIONS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_DATE_CREATED + " TEXT, "
                + KEY_LOC_RECORDING + " TEXT, "
                + KEY_LOC_MIDI + " TEXT, "
                + KEY_LOC_SHEET + " TEXT, "
                + KEY_LOC_INFO + " TEXT " + ");";
        db.execSQL(CREATE_LOGIN_TABLE);
    }
 
    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPOSITIONS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * Storing composition details in database
     * @param dateCreated String of form "mm/dd/yyyy"
     * */
    public void addComposition(String name, String dateCreated, String locRecording, String locMIDI, String locSheet, String locInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);                     // Name
        values.put(KEY_DATE_CREATED, dateCreated);      // Date created
        values.put(KEY_LOC_RECORDING, locRecording);    // Recording location
        values.put(KEY_LOC_MIDI, locMIDI);              // MIDI file location
        values.put(KEY_LOC_SHEET, locSheet);			// Sheet music location
        values.put(KEY_LOC_INFO, locInfo);				// Info sheet location
 
        // Inserting Row
        db.insert(TABLE_COMPOSITIONS, null, values);
        db.close(); // Closing database connection
    }
 
    /**
     * Getting composition data from database
     * */
    public HashMap<String, String> getCompositionDetails(int project_id){
        HashMap<String,String> project = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPOSITIONS + " WHERE " + KEY_ID + " = '" + project_id +"';";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            project.put("name", cursor.getString(1));
            project.put("dateCreated", cursor.getString(2));
            project.put("locRecording", cursor.getString(3));
            project.put("locMIDI", cursor.getString(4));
            project.put("locSheet", cursor.getString(5));
            project.put("locInfo", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        return project;
    }
 
    /**
     * Getting composition collection status
     * @return rows in database
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COMPOSITIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
 
        // return row count
        return rowCount;
    }
    
    public SparseArray<String> getCompositionNames() {
        String nameQuery = "SELECT * FROM " + TABLE_COMPOSITIONS + " WHERE 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(nameQuery, null);
        SparseArray<String> result = new SparseArray<String>();
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result.put(cursor.getInt(0), cursor.getString(1));
        }
        return result;
    }
 
    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_COMPOSITIONS, null, null);
        db.close();
    }
 
}