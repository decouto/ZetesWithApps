package com.appsolut.composition.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.appsolut.composition.ProjectOverviewActivity;
import com.appsolut.composition.pitch_detection.WaveToMidi;

public class GenerateMidiTask extends AsyncTask<Void, Void, File>{
    
    // User flow
    private Context mContext;
    private TaskCallback mCallback;
    private long project_id;
    
    // Dialog
    ProgressDialog pd_conversion;
    
    // MIDI generation
    private WaveToMidi midi_generator;
    
    // File descriptors
    private File dir;
    private File audio_file;
    
    public GenerateMidiTask (Context context, TaskCallback callback, long project_id) {
        // User flow
        this.mContext = context;
        this.mCallback = callback;
        this.project_id = project_id;
        
        // MIDI generation
        midi_generator = new WaveToMidi();
        dir = new File( Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + "SongScribe"
                + File.separator
                + "projects"
                + File.separator
                + project_id
                + File.separator);
        dir.mkdirs();
    }
    
    @Override
    protected void onPreExecute() {
        // Open file resource
        audio_file = new File(dir, project_id + ".rawwav");
        int length = (int) audio_file.length() / 8;
        
        // Prepare dialog
        pd_conversion = new ProgressDialog(mContext);
        pd_conversion.setTitle("Working...");
        pd_conversion.setIndeterminate(false);
        pd_conversion.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_conversion.setMax(length);
        pd_conversion.show();
    }
    
    @Override
    protected File doInBackground(Void...voids) {
        File result = null;
        try {
            // Read file into byte array
            byte [] fileData = new byte[(int) audio_file.length()];
            DataInputStream dis = new DataInputStream((new FileInputStream(audio_file)));
            dis.readFully(fileData);
            dis.close();
            
            // Convert byte array into double array
            double[] data = ByteBuffer.wrap(fileData).asDoubleBuffer().array();
            
            // Convert to MIDI file
            result = midi_generator.audioToMidiFile(data, 16000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    protected void onPostExecute(File midi) {        
        // Copy them files
        File out_path = new File(dir, project_id + ".midi");
        try {
            InputStream in = new FileInputStream(midi);
            OutputStream out = new FileOutputStream(out_path);
            
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Close Dialog
        pd_conversion.dismiss();
        
        // Close and launch
        Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
        projectIntent.putExtra("project_id", project_id);
        mCallback.done(projectIntent, true);
    }

}
