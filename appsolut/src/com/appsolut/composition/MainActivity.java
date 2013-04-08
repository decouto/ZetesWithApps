package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {
    
    private Context mContext;
    
    private Button btn_new_project;
    private Button btn_settings;
    private Button btn_project_list;
    private Button btn_social;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mContext = this;
        
        // Import layout elements
        btn_new_project = (Button) findViewById(R.id.btn_new_project);
        btn_settings = (Button) findViewById(R.id.btn_settings);
        btn_project_list = (Button) findViewById(R.id.btn_project_list);
        btn_social = (Button) findViewById(R.id.btn_social);
        
        // Set listeners
        btn_new_project.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Intent newProjectIntent = new Intent(mContext, ProjectNewActivity.class);
                newProjectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newProjectIntent);
            }
        });
        btn_settings.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsIntent);
            }
        });
        btn_project_list.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Intent projectListIntent = new Intent(mContext, ProjectListActivity.class);
                projectListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(projectListIntent);
            }
        });
        btn_social.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Intent socialIntent = new Intent(mContext, SocialActivity.class);
                socialIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(socialIntent);
            }
        });
    }

}
