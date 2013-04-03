package com.appsolut.composition;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.DatabaseHandler;

public class ProjectNewActivity extends SherlockActivity {
    
    private Context mContext;
    
    // layout elements
    RecordButton btn_record;
    
    // project details
    private DatabaseHandler db;
    private long project_id;
    
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
        
        // create layout elements
        btn_record = new RecordButton(mContext, 80);
        LinearLayout ll_buttons = (LinearLayout) findViewById(R.id.ll_new_project_buttons);
        ll_buttons.addView(btn_record);
        
        // project details
        db = new DatabaseHandler(mContext);
        project_id = db.addComposition("temp", "", "", "", "", "", "");
        if (project_id < 0) {
            finish();
        }
        
        // set recording values
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/SongScribe/temp/" + project_id + ".3gp";
        File temp_dir = new File(mFileName);
        temp_dir.mkdirs();
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
        btn_record.setEnabled(false);
        
        // Build confirmation
        AlertDialog.Builder ad_confirm_recording = new AlertDialog.Builder(this);

        ad_confirm_recording.setTitle("Confirm Recording");
        ad_confirm_recording.setMessage("How'd that sound? Want to keep that recording, or give it another go?");

        final EditText et_recording_name = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        et_recording_name.setLayoutParams(lp);
        ad_confirm_recording.setView(et_recording_name);

        ad_confirm_recording.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO
            }
        });
        ad_confirm_recording.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO
            }
        });
        ad_confirm_recording.setNegativeButton("Re-record", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO
            }
        });

        ad_confirm_recording.show();
    }

}