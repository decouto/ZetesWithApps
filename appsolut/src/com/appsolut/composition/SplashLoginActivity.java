package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.DatabaseHandler;

public class SplashLoginActivity extends SherlockActivity {
    
    private Context mContext;
    
    // layout elements
    private TextView tv_skip_login;
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private CheckBox cb_remember_login;
    
    // database handler
    DatabaseHandler db;
    
    // shared preferences
    SharedPreferences shared_preferences;
    SharedPreferences.Editor preference_editor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().hide();
        mContext = this;
        
        // import layout elements
        tv_skip_login = (TextView) findViewById(R.id.tv_skip_login);
        et_email = (EditText) findViewById(R.id.et_login_email);
        et_password = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        cb_remember_login = (CheckBox) findViewById(R.id.cb_remember_login);
        
        // database handler
        db = new DatabaseHandler(mContext);
        db.sanitizeCompositions();
        
        // shared preferences
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preference_editor = shared_preferences.edit();
        boolean remember = shared_preferences.getBoolean("prefAccountRememberDetails", false);
        cb_remember_login.setChecked(remember);
        if (remember) {
            proceedToDashboard();
        }
        
        
        // add listeners
        tv_skip_login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                db.logUserOut();
                proceedToDashboard();
            }
        });
        
        btn_login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                db.logUserOut();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                Toast.makeText(mContext, String.format("Login with email: %s and password: %s", email, password), Toast.LENGTH_LONG).show();
            }
        });
        cb_remember_login.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preference_editor.putBoolean("prefAccountRememberDetails", isChecked);
                preference_editor.commit();
            } 
        });
    }
    
    private void proceedToDashboard() {
        Intent dashboardIntent = new Intent(mContext, MainActivity.class);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboardIntent);
        finish();
    }

}
