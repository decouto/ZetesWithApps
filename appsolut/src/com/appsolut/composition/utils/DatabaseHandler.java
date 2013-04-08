package com.appsolut.composition.utils;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LongSparseArray;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "appsolut";
 
    // Composition table name
    private static final String TABLE_COMPOSITIONS = "compositions";
 
    // Composition Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_COMPOSITION_NAME = "composition_name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE_CREATED = "date_created";
    
    // User table name
    private static final String TABLE_USER = "user";
    
    // User Table Columns names
    private static final String KEY_UUID = "uuid";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL_ADDRESS = "email_address";
    private static final String KEY_DATE_LOGGED_IN = "date_logged_in";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_UUID + " INTEGER, "
                + KEY_USER_NAME + " TEXT, "
                + KEY_EMAIL_ADDRESS + " TEXT, "
                + KEY_DATE_LOGGED_IN + " TEXT "
                + ");";
        
        String CREATE_COMPOSITION_TABLE = "CREATE TABLE " + TABLE_COMPOSITIONS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_COMPOSITION_NAME + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_DATE_CREATED + " TEXT "
                + ");";
        
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COMPOSITION_TABLE);
    }
 
    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPOSITIONS);
 
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
     * Storing composition details in database
     * @param dateCreated String of form "mm/dd/yyyy"
     * */
    public long addComposition(String name, String description, String dateCreated) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_COMPOSITION_NAME, name);                     // Name
        values.put(KEY_DESCRIPTION, description);       // Description
        values.put(KEY_DATE_CREATED, dateCreated);      // Date created
 
        // Inserting Row
        long result = db.insert(TABLE_COMPOSITIONS, null, values);
        db.close(); // Closing database connection
        
        return result;
    }
    
    public long updateComposition(long project_id, String name, String description, int bpm) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues args = new ContentValues();
        args.put(KEY_COMPOSITION_NAME, name);
        args.put(KEY_DESCRIPTION, description);
        // TODO args.put(
        
        long result = db.update(TABLE_COMPOSITIONS, args, KEY_ID + "=" + project_id, null);
        return result;
    }
    
    public void removeComposition(long project_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPOSITIONS, KEY_ID + "=" + project_id, null);
        db.close();
    }
 
    /**
     * Getting composition data from database
     * */
    public HashMap<String, String> getCompositionDetails(long project_id){
        HashMap<String,String> project = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPOSITIONS + " WHERE " + KEY_ID + " = '" + project_id +"';";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            project.put("name", cursor.getString(1));
            project.put("description", cursor.getString(2));
            project.put("dateCreated", cursor.getString(3));
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
 
}