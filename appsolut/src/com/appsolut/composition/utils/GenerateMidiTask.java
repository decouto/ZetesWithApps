package com.appsolut.composition.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

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
    private MediaExtractor mMediaExtractor;
    private MediaCodec  mMediaCodec;
    private MediaFormat mMediaFormat;
    private ByteBuffer[] codecOutputBuffers;
    
    private int outputBufferIndex;
    private MediaCodec.BufferInfo buf_info;
    
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
        
        Log.d("bchrobot", "Created directory");
    }
    
    protected void onPreExecute() {        
        Log.d("bchrobot", "Pre-execute: opening file resource");
        // Open file and convert to double[]
        audio_file = new File(input_dir_path + project_id + ".3gp");
        ParcelFileDescriptor pfd;
        AssetFileDescriptor fd_audio_file;
        Log.d("bchrobot", "Pre-execute: opening file as FD");
        try {
            pfd = ParcelFileDescriptor.open(audio_file, ParcelFileDescriptor.MODE_READ_ONLY);
            fd_audio_file = new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
            
            mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(fd_audio_file.getFileDescriptor(), fd_audio_file.getStartOffset(), fd_audio_file.getLength());

            Log.d("bchrobot", "Pre-execute: getting MediaFormat");
            
            mMediaFormat = mMediaExtractor.getTrackFormat(0);
            String mMime = mMediaFormat.getString(MediaFormat.KEY_MIME);
            
            mMediaCodec = MediaCodec.createDecoderByType(mMime);
            mMediaCodec.configure(mMediaFormat, null, null, 0);
            mMediaCodec.start();
            
            mMediaExtractor.selectTrack(0);

            codecOutputBuffers = mMediaCodec.getOutputBuffers();
            buf_info = new MediaCodec.BufferInfo();
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(buf_info, 0);
            

            Log.d("bchrobot", "Pre-execute: starting dialog");
            
            pd_conversion = new ProgressDialog(mContext);
            pd_conversion.setTitle("Working...");
            pd_conversion.setIndeterminate(false);
            pd_conversion.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd_conversion.setMax(buf_info.size / 2);
            pd_conversion.show();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    protected File doInBackground(Void...voids) {
        // byte[] pcm = new byte[buf_info.size];
        // codecOutputBuffers[outputBufferIndex].get(pcm, 0, buf_info.size);
        

        Log.d("bchrobot", "doInBackground: starting conversion");
        
        
        // Convert
        codecOutputBuffers[outputBufferIndex].order(ByteOrder.LITTLE_ENDIAN);
        double[] audio = new double[buf_info.size / 2];
        int i = 0;
        while (codecOutputBuffers[outputBufferIndex].remaining() > 2) {
            // read shorts (16bits) and cast them to doubles
            short t = codecOutputBuffers[outputBufferIndex].getShort();
            audio[i] = t;
            audio[i] /= 32768.0;
            i++;
        }
        
        Log.d("bchrobot", "doInBackground: running FFT");
        
        // Run MIDI generation
        File midi_output = midi_generator.audioToMidiFile(audio, mMediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        return midi_output;
    }
    
    protected void onPostExecute(File... files) {
        
        Log.d("bchrobot", "onPostExecute: copying file");
        
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
        

        pd_conversion.dismiss();
        
        Log.d("bchrobot", "onPostExecute: handling intents");
        
        // Close and launch
        Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
        projectIntent.putExtra("project_id", project_id);
        mCallback.done(projectIntent, true);
        
        Log.d("bchrobot", "onPostExecute: whew!");
    }

}
