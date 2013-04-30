package com.appsolut.composition.pitch_detection;

import java.util.ArrayList;
import java.util.Arrays;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;
import java.nio.DoubleBuffer;
import java.io.DataInputStream;

public class MidiGenerator {
	
	private static final String TAG = "FreqList";//Do not put useless names on things
	

	private final static int DEFAULT_CLIP_RATE = 5;//Number of frequencies/second
	private int bpm;
	private int ppq = 192;
	private static final int OFF_VAL = -1;

	// MIDI resources
	private MidiFile midiFile;
	private MidiTrack tempoTrack;
	private MidiTrack noteTrack;
	
	public MidiGenerator(int _bpm) {
	    // MIDI Instantiation
	    midiFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION);
	    tempoTrack = new MidiTrack();
	    noteTrack = new MidiTrack();
	    bpm=_bpm;
	    // Tempo track
	    TimeSignature ts = new TimeSignature();
	    ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
	    Tempo t = new Tempo();
	    t.setBpm(_bpm);
	    tempoTrack.insertEvent(ts);
	    tempoTrack.insertEvent(t);
	    midiFile.addTrack(tempoTrack);
	}
	

	public MidiFile generateMidi(DataInputStream audio_stream, long sampleRate) {
		return generateMidi(audio_stream,sampleRate,DEFAULT_CLIP_RATE);
	}
	
	/**
	 * Taking the output from getMidiNumsWithTicks schedules midi events and produces a midi file
	 * 
	 * @param audio The audio to be converted to midi
	 * @param sampleRate The sample rate the audio was recorded at
	 * @param clipRate The number of frequencies found in the audio per second
	 * @return
	 */
	public MidiFile generateMidi(DataInputStream audio_stream, long sampleRate, int clipRate){
		ArrayList<Integer> midiNums = new ArrayList<Integer>();
		ArrayList<Long> durations = new ArrayList<Long>();
		double[] buffered_audio = new double[44100];
		AudioAnalyser aa = new AudioAnalyser(bpm,ppq,sampleRate,clipRate);

		
		Pair<Integer[],Long[]> analysedAudio = aa.analyseAudio(buffered_audio);
		
		midiNums.addAll(Arrays.asList(analysedAudio.left));
		durations.addAll(Arrays.asList(analysedAudio.right));
		
		long onTick = 0;
		int midiNum = OFF_VAL;
		for(int i=0;i<midiNums.size();i++){
			midiNum = midiNums.get(i);
			if(midiNum != OFF_VAL){
				noteTrack.insertNote(0, midiNum, 127, onTick,durations.get(i));
			}
			onTick += durations.get(i);
		}
		midiFile.addTrack(noteTrack);
		return midiFile;
		
	}
	
	
	

}
