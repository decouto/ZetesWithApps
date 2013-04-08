package com.appsolut.composition;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class SocialActivity extends SherlockActivity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        getSupportActionBar().hide();
    }

}
