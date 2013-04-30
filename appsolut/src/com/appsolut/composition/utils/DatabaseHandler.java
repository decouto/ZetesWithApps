package com.appsolut.composition.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LongSparseArray;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static Variables
    // Database Version
    private static final int DATABASE_VERSION = 6;
 
    // Database Name
    private static final String DATABASE_NAME = "appsolut";
    
    // User table name
    private static final String TABLE_USER = "user";
    
    // User table column names
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_EMAIL_ADDRESS = "email_address";
    public static final String KEY_DATE_LOGGED_IN = "date_logged_in";
 
    // Composition table name
    private static final String TABLE_COMPOSITIONS = "compositions";
 
    // Composition table column names
    public static final String KEY_UUID = "uuid";
    public static final String KEY_COMPOSITION_NAME = "composition_name";
    public static final String KEY_COMPOSITION_BPM = "bpm";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DATE_CREATED = "date_created";
    public static final String KEY_TEMP = "temp";
    
    // Media table name
    private static final String TABLE_MEDIA = "media";
    
    // Media table column names
    // private static final String KEY_UUID = "uuid";
    public static final String KEY_COMPOSITION_ID = "composition_id";
    public static final String KEY_SERVER_STATUS = "server_status";
    public static final String KEY_MEDIA_TYPE = "media_type";
    // private static final String KEY_DATE_CREATED = "date_created";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_UUID + " INTEGER PRIMARY KEY, "
                + KEY_USER_ID + " INTEGER, "
                + KEY_USER_NAME + " TEXT, "
                + KEY_EMAIL_ADDRESS + " TEXT, "
                + KEY_DATE_LOGGED_IN + " TEXT "
                + ");";
        
        String CREATE_COMPOSITION_TABLE = "CREATE TABLE " + TABLE_COMPOSITIONS + " ("
                + KEY_UUID + " INTEGER PRIMARY KEY, "
                + KEY_COMPOSITION_NAME + " TEXT, "
                + KEY_COMPOSITION_BPM + " INTEGER, "
                + KEY_FILE_NAME + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_DATE_CREATED + " TEXT, "
                + KEY_TEMP + " INTEGER DEFAULT 1"
                + ");";
        
        String CREATE_MEDIA_TABLE = "CREATE TABLE " + TABLE_MEDIA + " ("
                + KEY_UUID + "INTEGER PRIMARY KEY, "
                + KEY_COMPOSITION_ID + " INTEGER, "
                + KEY_SERVER_STATUS + " TEXT, "
                + KEY_DATE_CREATED + " TEXT"
                + ");";
        
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COMPOSITION_TABLE);
        db.execSQL(CREATE_MEDIA_TABLE);
    }
 
    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPOSITIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
 
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Check if a composition exists with a specified name
     * @param name the name in question
     * @return true if a composition exists with the name in question
     */
    public boolean compositionNameExists(String name) {
        String query = "SELECT * FROM " + TABLE_COMPOSITIONS + " WHERE " + KEY_COMPOSITION_NAME + " = '" + name + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int row_count = cursor.getCount();
        cursor.close();
        db.close();
        
        return (row_count > 0);
    }
 
    /**
     * Store composition details in database
     * @param name the composition's name
     * @param description the composition's description
     * @param dateCreated of form "mm/dd/yyyy"
     * */
    public long addComposition(String name, int bpm, String description, String dateCreated) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_COMPOSITION_NAME, name);         // Name
        values.put(KEY_COMPOSITION_BPM, bpm);           // BPM
        values.put(KEY_FILE_NAME, name);                // File name
        values.put(KEY_DESCRIPTION, description);       // Description
        values.put(KEY_DATE_CREATED, dateCreated);      // Date created
 
        // Inserting Row
        long result = db.insert(TABLE_COMPOSITIONS, null, values);
        db.close(); // Closing database connection
        
        return result;
    }
    
    /**
     * Update a composition with select values
     * @param project_id the project's uuid
     * @param args the values to update; composition_name, bpm, file_name, description, date_created
     * @return the number of rows affected
     */
    public long updateComposition(long project_id, ContentValues args) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        long result = db.update(TABLE_COMPOSITIONS, args, KEY_UUID + "=" + project_id, null);
        db.close();
        
        return result;
    }
    
    public void removeComposition(long project_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPOSITIONS, KEY_UUID + "=" + project_id, null);
        db.close();
    }
 
    /**
     * Getting composition data from database
     * */
    public HashMap<String, String> getCompositionDetails(long project_id){
        HashMap<String,String> project = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPOSITIONS + " WHERE " + KEY_UUID + " = '" + project_id +"';";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            project.put(KEY_UUID, cursor.getString(0));
            project.put(KEY_COMPOSITION_NAME, cursor.getString(1));
            project.put(KEY_COMPOSITION_BPM, cursor.getString(2));
            project.put(KEY_FILE_NAME, cursor.getString(3));
            project.put(KEY_DESCRIPTION, cursor.getString(4));
            project.put(KEY_DATE_CREATED, cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        return project;
    }
    
    public ArrayList<Long> getAllCompositionIds() {
        ArrayList<Long> result = new ArrayList<Long>();
        
        String query = "SELECT " + KEY_UUID + " FROM " + TABLE_COMPOSITIONS + " WHERE 1;";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // Move to first row
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long project_id = Long.parseLong(cursor.getString(0));
            result.add(project_id);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        
        return result;
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
 
        return rowCount;
    }
    
    /**
     * Returns a LongSparseArray containing the names of all composition projects
     * @return LongSparseArray of composition names
     */
    public LongSparseArray<String> getCompositionNames() {
        String nameQuery = "SELECT * FROM " + TABLE_COMPOSITIONS + " WHERE 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(nameQuery, null);
        LongSparseArray<String> result = new LongSparseArray<String>();
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        db.close();
        
        return result;
    }
    
    /**
     * Checks to see if a user's credentials are stored in the database
     * @return true if user is logged in
     */
    public boolean isLoggedIn() {
        String query = "SELECT * FROM " + TABLE_USER + " WHERE 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int row_count = cursor.getCount();
        cursor.close();
        db.close();
        
        return (row_count > 0);
    }
    
    /**
     * Logs the current user out by removing all rows from the User table
     */
    public void logUserOut() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
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
    
    /**
     * Remove temporary compositions
     */
    public void sanitizeCompositions() {
        String nameQuery = "DELETE FROM " + TABLE_COMPOSITIONS + " WHERE " + KEY_TEMP + " = 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(nameQuery, null);
        LongSparseArray<String> result = new LongSparseArray<String>();
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        db.close();
    }
    
    /**
     * Create new multimedia entry
     * @param project_id
     * @param date_created
     * @return
     */
    public long addMedia(long project_id, String date_created) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_COMPOSITION_ID, project_id);     // Composition id
        values.put(KEY_SERVER_STATUS, "false");         // Server status
        values.put(KEY_DATE_CREATED, date_created);     // Date created
 
        // Inserting Row
        long result = db.insert(TABLE_MEDIA, null, values);
        db.close(); // Closing database connection
        
        return result;
    }
    
    public long[] getMediaForProject(long project_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT " + KEY_UUID + " FROM " + TABLE_MEDIA + " WHERE " + KEY_COMPOSITION_ID + " = " + project_id + ";";
        
        Cursor cursor = db.rawQuery(query, null);
        long[] result = new long[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            result[i] = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        
        return result;
    }
 
}