package com.appsolut.composition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.DatabaseHandler;
import com.appsolut.composition.utils.GenerateMidiTask;
import com.appsolut.composition.utils.RecordWavTask;
import com.appsolut.composition.utils.TaskCallback;

public class ProjectNewActivity extends SherlockActivity implements TaskCallback {
    
    private Context mContext;
    
    // layout elements
    private EditText et_project_name;
    private EditText et_project_bpm;
    private EditText et_project_description;
    private FrameLayout fl_record_btn;
    // private FrameLayout fl_playback_btn;
    private Button btn_cancel;
    private Button btn_save;
    
    private RecordButton btn_record;
    //private PlaybackButton btn_playback;
    
    // project details
    private DatabaseHandler db;
    private SharedPreferences shared_preferences;
    private long project_id;
    private String project_name;
    private String project_date_created;
    private int project_bpm;
    
    private boolean recording_exists = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_new);
        getSupportActionBar().hide();
        mContext = this;
        
        // import layout elements
        et_project_name = (EditText) findViewById(R.id.et_project_name);
        et_project_bpm = (EditText) findViewById(R.id.et_project_bpm);
        et_project_description = (EditText) findViewById(R.id.et_project_description);
        fl_record_btn = (FrameLayout) findViewById(R.id.fl_record_btn);
        //fl_playback_btn = (FrameLayout) findViewById(R.id.fl_playback_btn);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_save = (Button) findViewById(R.id.btn_save_project);
        
        // create layout elements
        btn_record = new RecordButton(mContext, 120);
        fl_record_btn.addView(btn_record);
        fl_record_btn.setForegroundGravity(Gravity.BOTTOM);
        //btn_playback = new PlaybackButton(mContext, 80);
        //btn_playback.setVisibility(View.INVISIBLE);
        //fl_playback_btn.addView(btn_playback);
        
        // project details
        db = new DatabaseHandler(mContext);
        project_id = db.addComposition("temp", 120, "", "");
        if (project_id < 0) {
            Toast.makeText(getApplicationContext(), "Composition creation failed", Toast.LENGTH_LONG).show();
            finish();
        }
        
        // setting defaults
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        project_date_created = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date());
        project_name = project_date_created + " - " + project_id;
        project_bpm = Integer.parseInt(shared_preferences.getString("prefDefaultBPM", "120"));
        et_project_name.setText(project_name);
        et_project_bpm.setText(String.valueOf(project_bpm));
        
        // set listeners
        btn_cancel.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb_confirm_cancel = new AlertDialog.Builder(mContext);
                adb_confirm_cancel.setTitle("Cancel New Project");
                adb_confirm_cancel
                        .setMessage("Are you sure you want to cancel your new project?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.removeComposition(project_id);
                                dialog.dismiss();
                                finish();                                
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog ad_confirm_cancel = adb_confirm_cancel.create();
                ad_confirm_cancel.show();
            }
        });
        btn_save.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!recording_exists) {
                    Toast.makeText(mContext, "No recording exists!", Toast.LENGTH_LONG).show();
                } else {
                    // Sanitize inputs
                    String et_name = et_project_name.getText().toString();
                    String name = et_name.equals("") ? project_name : et_name;
                    String description = et_project_description.getText().toString();
                    String et_bpm = et_project_bpm.getText().toString();
                    int bpm = et_bpm.matches("\\d{2,3}") ? Integer.parseInt(et_bpm) : project_bpm;
                    
                    // Update composition details
                    ContentValues args = new ContentValues();
                    args.put(DatabaseHandler.KEY_COMPOSITION_NAME, name);
                    args.put(DatabaseHandler.KEY_COMPOSITION_BPM, bpm);
                    args.put(DatabaseHandler.KEY_DESCRIPTION, description);
                    args.put(DatabaseHandler.KEY_DATE_CREATED, project_date_created);
                    args.put(DatabaseHandler.KEY_TEMP,0); 
                    db.updateComposition(project_id, args);
                    
                    // Convert to MIDI
                    GenerateMidiTask generateMidi = new GenerateMidiTask(mContext, ProjectNewActivity.this, project_id);
                    generateMidi.execute();
                }
            }
        });
    }
    
    class RecordButton extends Button {
        boolean mRecording = false;
        RecordWavTask recordTask;
        
        public RecordButton(Context mContext, int size) {
            super(mContext);
            setBackgroundResource(R.drawable.record_button_start_drawable);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
            params.weight = 1;
            params.gravity = Gravity.BOTTOM;
            setLayoutParams(params);
            setOnClickListener(clicker);
            recordTask = new RecordWavTask(mContext);
        }
        
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                if (mRecording) {
                    setBackgroundResource(R.drawable.record_button_start_drawable);
                    recordTask.cancel(true);
                    recording_exists = true;
                } else {
                    setBackgroundResource(R.drawable.record_button_stop_drawable);
                    recordTask.execute(project_id);
                }
                mRecording = !mRecording;
            }
        };
    }

    @Override
    public void done(Intent callbackIntent, Boolean finish) {
        
        if (callbackIntent != null) {
            startActivity(callbackIntent);
        }
        
        if (finish != null && finish) {
            finish();
        }
    }
    
    

}