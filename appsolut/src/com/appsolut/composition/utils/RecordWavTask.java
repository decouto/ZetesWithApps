package com.appsolut.composition.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.widget.Toast;

public class RecordWavTask extends AsyncTask<Long, Void, Boolean> {
    
    Context mContext;
    Boolean is_saved = false;
    
    // Capture audio at 44.1kHz
    private int sample_rate;
    private int channelConfiguration;
    private int audioEncoding;
    private final int bufferSize;
    
    // Recording
    private AudioRecord audioRecord;
    
    // Buffers
    private short[] buffer;
    private byte[] fileBuffer;
    
    // Storage IO
    private ProjectModel project_model;
    private FileOutputStream fos;
    private DataOutputStream dos;
    private File dir;
    private File file;

    
    public RecordWavTask(Context context) {
        
        mContext = context.getApplicationContext();
        
        // Capture audio at 44.1kHz
        sample_rate = 44100;
        channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        bufferSize = AudioRecord.getMinBufferSize(sample_rate, channelConfiguration, audioEncoding);
        
        // Recording
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sample_rate, channelConfiguration,
                audioEncoding, bufferSize);
        
        // Buffers
        buffer = new short[bufferSize];
        fileBuffer = new byte[bufferSize * 2];
    }
    
    @Override
    // Invoked on UI thread for interface prep
    protected void onPreExecute() {
        // Do nothing because there is no need to update the UI
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        double project_id = params[0];
        
        // Storage IO
        project_model = new ProjectModel(mContext, (long) project_id);
        dir = project_model.getProjectDir();
        file = new File(dir, project_id + ".rawwav");
        try {
            fos = new FileOutputStream(file, true);
            dos = new DataOutputStream(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Capture Audio
        audioRecord.startRecording();
        
        // Write to .wav file
        while (!isCancelled() ) {
            
            audioRecord.read(buffer, 0, bufferSize);
            
            for (int i = 0; i < buffer.length; i++) {
                try {
                    dos.writeShort(buffer[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            dos.close();
            fos.close();
            return (is_saved = true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return (is_saved = false);
    }
    
    @Override
    // Invoked on UI thread after doInBackground if cancelled
    protected void onCancelled() {
        String message = is_saved ? "Recording created successfully" : "WARNING: recording could not be saved";
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    // Invoked on UI thread after doInBackground if not cancelled
    protected void onPostExecute(Boolean result) {
        String message = result ? "Recording created successfully" : "WARNING: recording could not be saved";
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}
