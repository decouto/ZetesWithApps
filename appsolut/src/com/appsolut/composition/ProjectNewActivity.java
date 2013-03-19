package com.appsolut.composition;

import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class ProjectNewActivity extends SherlockActivity {
    
    private Context mContext;
    
    // recording elements
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_new);
        getSupportActionBar().hide();
        mContext = this;
        
        RecordButton btn_record = new RecordButton(mContext, 80);
        LinearLayout ll_buttons = (LinearLayout) findViewById(R.id.ll_new_project_buttons);
        ll_buttons.addView(btn_record);
        
        // set recording values
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "SongScribe/audioTest.3gp";
    }
    
    class RecordButton extends Button {
        boolean mRecording = false;
        
        public RecordButton(Context mContext, int size) {
            super(mContext);
            setBackgroundResource(R.drawable.record_button_start_drawable);
            setLayoutParams(new LayoutParams(size, size));
            setOnClickListener(clicker);
        }
        
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                //onRecord(mRecording);
                if (mRecording) {
                    setBackgroundResource(R.drawable.record_button_start_drawable);
                    stopRecording();
                } else {
                    setBackgroundResource(R.drawable.record_button_stop_drawable);
                    startRecording();
                }
                mRecording = !mRecording;
            }
        };
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
        Toast.makeText(mContext, "Started Recording", Toast.LENGTH_SHORT).show();
    }
    
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Toast.makeText(mContext, "Stopped Recording", Toast.LENGTH_SHORT).show();
    }

}