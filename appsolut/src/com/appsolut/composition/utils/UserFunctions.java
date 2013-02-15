package com.appsolut.composition.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
 
public class UserFunctions {
 
    private JSONParser jsonParser;
 
    // Testing using Jebbit Staging server
    //private static String serverURL = "http://www.jebbitmoms.com/mobile_api/test.php";
    private static String serverURL = "http://www.jebbitmoms.com/mobile_api/";
 
    // Tag types
    private static String TAG_LOGIN = "login";
    private static String TAG_REGISTER = "register";
    //TODOprivate static String TAG_UPDATE_ACCOUNT = "update_account";
 
    // Constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
 
    /**
     * Make a request to log a user in
     * @param email the user's email address
     * @param password the user's password
     * @return JSONObject holding result from API
     */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", TAG_LOGIN));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        
        // Getting JSON object
        JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
        return json;
    }
 
    /**
     * Make a request to register a user
     * @param name the user's name
     * @param email the user's email address
     * @param birthday the user's birthday in format MM/DD/YYYY
     * @param gender the user's gender (male|female)
     * @param location the user's location
     * @param gradYear the user's graduation year
     * @param password1 the user's password
     * @param password2 the user's confirmation password
     * @return JSONObject holding result from API
     */
    public JSONObject registerUser(String name, String email, String birthday, String gender, String location, String gradYear, String password1, String password2){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", TAG_REGISTER));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("birthday", birthday));
        params.add(new BasicNameValuePair("location", location));
        params.add(new BasicNameValuePair("grad_year", gradYear));
        params.add(new BasicNameValuePair("password1", password1));
        params.add(new BasicNameValuePair("password2", password2));
        
        // Getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
        return json;
    }
 
    /**
     * Get the login status of the device
     * @param context the activity context
     * @return true if user is logged in
     */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
 
    /**
     * Log a user out by resetting the database
     * @param context the application context
     * @return true
     */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
 
}