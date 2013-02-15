package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class SplashLoginActivity extends SherlockActivity {
    
    private Context mContext;
    
    // layout elements
    private TextView tv_skip_login;
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().hide();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        
        // import layout elements
        tv_skip_login = (TextView) findViewById(R.id.tv_skip_login);
        et_email = (EditText) findViewById(R.id.et_login_email);
        et_password = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        
        // add listeners
        tv_skip_login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(mContext, DashboardActivity.class);
                dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dashboardIntent);
                finish();
            }
        });
        
        btn_login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                Toast.makeText(mContext, String.format("Login with email: %s and password: %s", email, password), Toast.LENGTH_LONG).show();
            }
        });
    }

}
