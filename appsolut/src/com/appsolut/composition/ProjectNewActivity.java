package com.appsolut.composition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    private FrameLayout fl_playback_btn;
    private Button btn_cancel;
    private Button btn_save;
    
    private RecordButton btn_record;
    private PlaybackButton btn_playback;
    
    // project details
    private DatabaseHandler db;
    private SharedPreferences shared_preferences;
    private long project_id;
    private String project_name;
    private int project_bpm;
    
    // recording elements
    private static final String LOG_TAG = "AudioRecording";
    private static String mDirectoryPath;
    private static String mFileName;

    private MediaPlayer mPlayer;
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
        fl_playback_btn = (FrameLayout) findViewById(R.id.fl_playback_btn);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_save = (Button) findViewById(R.id.btn_save_project);
        
        // create layout elements
        btn_record = new RecordButton(mContext, 80);
        btn_playback = new PlaybackButton(mContext, 80);
        btn_playback.setVisibility(View.INVISIBLE);
        fl_record_btn.addView(btn_record);
        fl_playback_btn.addView(btn_playback);
        
        // project details
        db = new DatabaseHandler(mContext);
        project_id = db.addComposition("temp", "", "");
        if (project_id < 0) {
            Toast.makeText(getApplicationContext(), "Composition creation failed", Toast.LENGTH_LONG).show();
            finish();
        }
        
        // setting defaults
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        project_name = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date());
        project_bpm = Integer.parseInt(shared_preferences.getString("prefDefaultBPM", "130"));
        et_project_name.setText(project_name);
        et_project_bpm.setText(String.valueOf(project_bpm));
        
        // set recording values
        mDirectoryPath = getFilesDir().getAbsolutePath()
                +File.separator
                +"projects"
                +File.separator
                +project_id
                +File.separator;
        File directory = new File(mDirectoryPath);
        directory.mkdirs();
        mFileName = mDirectoryPath + project_id + ".3gp";
        
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
                                File directory = new File(mDirectoryPath);
                                directory.delete();
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
                    String et_name = et_project_name.getText().toString();
                    String name = et_name.equals("") ? project_name : et_name;
                    String description = et_project_description.getText().toString();
                    String et_bpm = et_project_bpm.getText().toString();
                    int bpm = et_bpm.matches("\\d{2,3}") ? Integer.parseInt(et_bpm) : project_bpm;
                    db.updateComposition(project_id, name, description, bpm);
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
            setLayoutParams(new LayoutParams(size, size));
            setOnClickListener(clicker);
            recordTask = new RecordWavTask();
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
    
    class PlaybackButton extends Button {
        boolean mPlaying = false;
        
        public PlaybackButton(Context mContext, int size) {
            super(mContext);
            setBackgroundResource(R.drawable.record_button_start_drawable);
            setLayoutParams(new LayoutParams(size, size));
            setOnClickListener(clicker);
        }
        
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                if (mPlaying) {
                    setBackgroundResource(R.drawable.record_button_start_drawable);
                    stopPlaying();
                } else {
                    setBackgroundResource(R.drawable.record_button_stop_drawable);
                    startPlaying();
                }
                mPlaying = !mPlaying;
            }
        };
    }
    
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            FileInputStream fileInputStream = new FileInputStream(mFileName);
            mPlayer.setDataSource(fileInputStream.getFD());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "mPlayer.prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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