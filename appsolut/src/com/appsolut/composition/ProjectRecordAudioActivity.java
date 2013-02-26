package com.appsolut.composition;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.ProjectModel;

public class ProjectRecordAudioActivity extends SherlockActivity {
    
    private Context mContext;
    private long project_id;
    private ProjectModel projectModel;
    
    // layout elements
    private LinearLayout ll_main;
    private TextView tv_project_name;
    private View view_metronome;
    
    // metronome elements
    private final static int LED_NOTIFICATION_ID = 15; 
    private Animation anim_metronome;
    private int bpm = 120;
    private int ms_per_beat = 1000 * 60 / bpm;
    
    // recording elements
    private RecordButton mRecordButton;
    private MediaRecorder mRecorder;
    private PlayButton mPlayButton;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        mContext = this;
        
        // get project ID
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null | (bundle.getLong("project_id") == 0)) {
            finish();
        }
        project_id = bundle.getLong("project_id");
        projectModel = new ProjectModel(mContext, project_id);
        Log.d("project", "loaded: " + project_id);
        
        // set activity base layout params
        setContentView(R.layout.activity_project_record);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(projectModel.getName());
        
        // import layout elements
        ll_main = (LinearLayout) findViewById(R.id.ll_record_main);
        tv_project_name = (TextView) findViewById(R.id.tv_project_name);
        view_metronome = (View) findViewById(R.id.view_metronome);
        
        // set layout values
        tv_project_name.setText(projectModel.getName());
        mRecordButton =  new RecordButton(mContext);
        mPlayButton =  new PlayButton(mContext);
        ll_main.addView(mRecordButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        ll_main.addView(mPlayButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        
        // set view metronome values
        anim_metronome = AnimationUtils.loadAnimation(this, R.anim.anim_metronome);
        anim_metronome.setStartOffset(ms_per_beat - 10);
        view_metronome.setAnimation(anim_metronome);
        
        // set LED metronome animation
        startMetronomeLED(bpm);
        
        // set recording values
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audioTest.3gp";
    }
    
    private void startMetronomeLED(int bpm) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification metronomeNotification = new Notification();
        metronomeNotification.ledARGB = 0xFF0000ff;
        metronomeNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
        metronomeNotification.ledOnMS = 1000;
        metronomeNotification.ledOffMS = 500;
        nm.notify(LED_NOTIFICATION_ID, metronomeNotification);
    }
    
    public void onPause() {
        super.onPause();
        
        // cancel LED notification
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(LED_NOTIFICATION_ID);
        
        // release recroder and player
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    class RecordButton extends Button {
        boolean mStartRecording = true;
        
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop Recording");
                } else {
                    setText("Start Recording");
                }
                mStartRecording = !mStartRecording;
            }
        };
        
        public RecordButton(Context mContext) {
            super(mContext);
            setText("Start Recording");
            setOnClickListener(clicker);
        }
    }
    
    class PlayButton extends Button {
        boolean mStartPlaying = true;
        
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop Playing");
                } else {
                    setText("Start Playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };
        
        public PlayButton(Context mContext) {
            super(mContext);
            setText("Start Playing");
            setOnClickListener(clicker);
        }
    }
    
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Log.d(LOG_TAG, mFileName);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }
    
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
