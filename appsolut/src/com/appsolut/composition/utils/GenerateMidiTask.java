package com.appsolut.composition.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.appsolut.composition.ProjectOverviewActivity;
import com.appsolut.composition.pitch_detection.WaveToMidi;
import com.leff.midi.MidiFile;

public class GenerateMidiTask extends AsyncTask<Void, Integer, MidiFile>{
    
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
        this.mContext = context.getApplicationContext();
        this.mCallback = callback;
        this.project_id = project_id;

        pd_conversion = new ProgressDialog(context);
        
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
        
        // Prepare dialog
        pd_conversion.setTitle("Working...");
        pd_conversion.setIndeterminate(false);
        pd_conversion.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_conversion.show();
    }
    
    @Override
    protected MidiFile doInBackground(Void...voids) {        
        MidiFile track = null;
        try {
            // Read file into byte array
            byte [] fileData = new byte[(int) audio_file.length()];
            DataInputStream dis = new DataInputStream((new FileInputStream(audio_file)));
            dis.readFully(fileData);
            dis.close();
            
            // Convert byte array into double array
            double[] data = new double[(int) Math.floor(audio_file.length() / 2)];
            ByteBuffer bb = ByteBuffer.wrap(fileData);
            for (int i = 0; i < data.length; i++) {
                data[i] = (double) bb.getShort();
            }
            
            // Convert to MIDI file
            track = midi_generator.audioToMidiFile(data, 16000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return track;
    }
    
    @Override
    protected void onProgressUpdate(Integer...integers) {
        pd_conversion.setProgress(integers[0]);
    }
    
    @Override
    protected void onPostExecute(MidiFile midiFile) {        
        // Create .MIDI files
        File file = new File(dir, project_id + ".midi");
        try {
            midiFile.writeToFile(file);
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
