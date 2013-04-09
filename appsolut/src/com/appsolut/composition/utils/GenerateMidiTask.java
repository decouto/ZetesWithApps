package com.appsolut.composition.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.appsolut.composition.ProjectOverviewActivity;
import com.appsolut.composition.pitch_detection.WaveToMidi;

public class GenerateMidiTask extends AsyncTask<Void, Void, File>{
    
    // User flow
    private Context mContext;
    private TaskCallback mCallback;
    private long project_id;
    
    // MIDI generation
    private WaveToMidi midi_generator;
    private MediaExtractor mMediaExtractor;
    private MediaCodec  mMediaCodec;
    private MediaFormat mMediaFormat;
    
    private ByteBuffer[] codecOutputBuffers;
    
    // File descriptors
    private String input_dir_path;
    private String output_dir_path;
    private File audio_file;
    
    public GenerateMidiTask (Context context, TaskCallback callback, long project_id) {
        // User flow
        this.mContext = context;
        this.mCallback = callback;
        this.project_id = project_id;
        
        // MIDI generation
        midi_generator = new WaveToMidi();
        input_dir_path = mContext.getFilesDir().getAbsolutePath()
                +File.separator
                +"projects"
                +File.separator
                +project_id
                +File.separator;
        output_dir_path = Environment.getExternalStorageDirectory().getAbsolutePath()
                +File.separator
                +"SongScrbe"
                +File.separator
                +"projects"
                +File.separator
                +project_id
                +File.separator;
        File directory = new File(output_dir_path);
        directory.mkdirs();
    }
    
    protected void onPreExecute() {
        // Open file and convert to double[]
        audio_file = new File(input_dir_path + project_id + ".3gp");
        ParcelFileDescriptor pfd;
        AssetFileDescriptor fd_audio_file;
        try {
            pfd = ParcelFileDescriptor.open(audio_file, ParcelFileDescriptor.MODE_READ_ONLY);
            fd_audio_file = new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
            
            mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(fd_audio_file.getFileDescriptor(), fd_audio_file.getStartOffset(), fd_audio_file.getLength());
            
            mMediaFormat = mMediaExtractor.getTrackFormat(0);
            String mMime = mMediaFormat.getString(MediaFormat.KEY_MIME);
            
            mMediaCodec = MediaCodec.createDecoderByType(mMime);
            mMediaCodec.configure(mMediaFormat, null, null, 0);
            mMediaCodec.start();
            
            mMediaExtractor.selectTrack(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    protected File doInBackground(Void...voids) {
        codecOutputBuffers = mMediaCodec.getOutputBuffers();
        MediaCodec.BufferInfo buf_info = new MediaCodec.BufferInfo();
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(buf_info, 0);
        byte[] pcm = new byte[buf_info.size];
        codecOutputBuffers[outputBufferIndex].get(pcm, 0, buf_info.size);
        
        double[] audio = new double[pcm.length];
        // TODO convert 
        
        // Run MIDI generation
        File midi_output = midi_generator.audioToMidiFile(audio, mMediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        return midi_output;
    }
    
    protected void onPostExecute(File... files) {
        // Copy them files
        File midi = files[0];
        File out_path = new File(output_dir_path + project_id + ".midi");
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
        
        // Close and launch
        Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
        projectIntent.putExtra("project_id", project_id);
        mCallback.done(projectIntent, true);
    }

}
