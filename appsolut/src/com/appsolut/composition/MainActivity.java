package com.appsolut.composition;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {
    
    private TextView tv_welcome_message;
    private Button btn_welcome;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        
        tv_welcome_message = (TextView) findViewById(R.id.tv_welcome_message);
        btn_welcome = (Button) findViewById(R.id.btn_welcome);
        btn_welcome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tv_welcome_message.setText("Goodbye 21w.789!");
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Skip landing page item
        menu.add("Continue");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Continue")) {
            // Launch Login Splash activity
            Intent loginIntent = new Intent(getApplicationContext(), SplashLoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
        return true;
    }

}
