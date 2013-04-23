package com.appsolut.composition.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;

public class RecordWavTask extends AsyncTask<Long, Void, Void> {
    
    // Capture audio at 16kHz
    private int frequency;
    private int channelConfiguration;
    private int audioEncoding;
    private final int bufferSize;
    
    // Recording
    private AudioRecord audioRecord;
    
    // Buffers
    private short[] buffer;
    private byte[] fileBuffer;
    
    // Storage IO
    private FileOutputStream fos;
    private File sdCard;
    private File dir;
    private File file;

    
    public RecordWavTask() {
        // Capture audio at 16kHz
        frequency = 16000;
        channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        
        // Recording
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                frequency, channelConfiguration,
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
    protected Void doInBackground(Long... params) {
        double project_id = params[0];
        String str_project_id = String.valueOf((int) project_id);
        
        // Storage IO
        sdCard = Environment.getExternalStorageDirectory();
        dir = new File(sdCard.getAbsolutePath()
                + File.separator
                + "SongScribe"
                + File.separator
                + "projects"
                + File.separator
                + str_project_id
                + File.separator);
        dir.mkdirs();
        file = new File(dir, str_project_id + ".rawwav");
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Capture Audio
        audioRecord.startRecording();
        
        // Write to .wav file
        while (!isCancelled() ) {
            
            audioRecord.read(buffer, 0, bufferSize);
            
            for (int i = 0; i < buffer.length; i++) {
                fileBuffer[i*2] = (byte)(buffer[i] & (short)0xFF);
                fileBuffer[i*2 + 1] = (byte)(buffer[i] >> 8);
            }
            
            try {
                fos.write(fileBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    protected void onCancelled() {
        // Do nothing
    }
    
    @Override
    // Invoked on UI thread if not cancelled
    protected void onPostExecute(Void voids) {
        // Do nothing because there is no need to update the UI
    }

}
