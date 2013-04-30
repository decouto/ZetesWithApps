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

import com.appsolut.composition.ProjectOverviewActivity;
import com.appsolut.composition.pitch_detection.MidiGenerator;
import com.leff.midi.MidiFile;

public class GenerateMidiTask extends AsyncTask<Void, Integer, MidiFile>{
    
    // User flow
    private Context mContext;
    private TaskCallback mCallback;
    private long project_id;
    private ProjectModel model;
    
    // Dialog
    ProgressDialog pd_conversion;
    
    // MIDI generation
    private MidiGenerator midi_generator;
    
    // File descriptors
    private File dir;
    private File audio_file;
    
    public GenerateMidiTask (Context context, TaskCallback callback, long project_id) {
        // User flow
        this.mContext = context.getApplicationContext();
        this.mCallback = callback;
        this.project_id = project_id;
        model = new ProjectModel(mContext, project_id);

        pd_conversion = new ProgressDialog(context);
        
        // MIDI generation
        midi_generator = new MidiGenerator(model.getBpm());
        dir = model.getProjectDir();
    }
    
    @Override
    protected void onPreExecute() {
        // Prepare dialog
        pd_conversion.setTitle("Processing Audio");
        pd_conversion.setMessage("Hold on while our leprechaun transcribes your song...");
        pd_conversion.setIndeterminate(false);
        pd_conversion.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_conversion.show();
    }
    
    @Override
    protected MidiFile doInBackground(Void...voids) {
        // Open file resource
        audio_file = new File(dir, (int)project_id + ".rawwav");
        
        MidiFile track = null;
        try {
            // Read file into byte array

            track = midi_generator.generateMidi(audio_file, 44100);
            
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
        File file = new File(dir, (int)project_id + ".midi");
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
