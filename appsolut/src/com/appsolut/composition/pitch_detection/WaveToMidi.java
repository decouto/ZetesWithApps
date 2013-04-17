package com.appsolut.composition.pitch_detection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.StringBuilder;
import com.leff.midi.event.*;
import com.leff.midi.*;
import java.lang.ArrayList;


public class WaveToMidi {
	private final static double[] FREQS_MIDI_OCT_0 = {	261.6255653006,
																277.1826309769,
																293.6647679174,
																311.1269837221,
																329.6275569129,
																349.2282314330,
																369.9944227116,
																391.9954359817,
																415.3046975799,
																440.0000000000,
																466.1637615181,
																493.8833012561};
	private final static int DEFAULT_CLIP_RATE = 5;
	
	public File audioToMidiFile(double[] audio, long sampleRate) {
		return audioToMidiFile(audio,sampleRate,DEFAULT_CLIP_RATE);
	}
	
	public File audioToMidiFile(double[] audio, long sampleRate, int clipRate){
		MidiTrack track = new MidiTrack();
		Pair<int[],long[]> midiNums = getMidiNumsWithTicks(audioToMidiNums(audio,sampleRate,clipRate));
		long onTick = 0;
		for(int i=0;i<midiNums.left.length;i++){
			track.insertNote(0, midiNums.left[i], 127, onTick,midiNums.right[i]);
			onTick += midiNums.right[i];
		}
		File f = new File("I don't know what to put here") ;
		FileOutputStream o = new FileOutputStream(f);
		track.writeToFile(o);
		return f;
		
	}
	public Pair<int[],long[]> getMidiNumsWithTicks(int[] midiNums){
		long TICKS_PER_OCCURRENCE = 100;
		ArrayList<Integer> newMidiNums = new ArrayList<Integer>();
		ArrayList<Long> ticksPerMidiNum = new ArrayList<Long>();
		for(int m: midiNums){
			newMidiNums.add(m);
			ticksPerMidiNum.add(TICKS_PER_OCCURRENCE);
		}
		int[] outMidiNums;
		outMidiNums = newMidiNums.toArray(outMidiNums);
		long[] outTicksPer;
		outTicksPer = ticksPerMidiNum.toArray(outTicksPer);
		return new Pair<int[],long[]>(outMidiNums,outTicksPer);
		
	}
	
	static int[] audioToMidiNums(double[] audio, long sampleRate, int clipRate){
		return 	snapIntervals(
				freqsToRawIntervals(
				PlotTones.audioToFreqs(audio, sampleRate, clipRate)));
	}
	
	static Pair<Integer,double[]> freqsToRawIntervals(int[] freqs){
		int baseFreq = freqs[0];
		double[] intervals = new double[freqs.length];
		for(int i=0; i<freqs.length; i++){
			intervals[i] = 12*Math.log(1.0*freqs[i]/baseFreq)/Math.log(2);//The number of half steps from baseFreq
		}
		return new Pair<Integer,double[]>(baseFreq,intervals);
	}
	
	static int[] snapIntervals(Pair<Integer,double[]> inp){
		int baseFreq = inp.left;
		double[] intervals = inp.right;
		int octaveNum = (int) Math.round(Math.log(baseFreq/FREQS_MIDI_OCT_0[9])/Math.log(2));
		double[] octave = new double[12];
		for(int i=0;i<octave.length;i++){
			octave[i] = Math.pow(2, octaveNum)*FREQS_MIDI_OCT_0[i];
		}
		double min = Integer.MAX_VALUE;
		int closestTone = 0;
		for(int i=0;i<octave.length; i++ ){
			if(Math.abs(baseFreq-octave[i])<min){
				min = Math.abs(baseFreq-octave[i]);
				closestTone = i;
			}
		}
		closestTone = closestTone + 60 + octaveNum*12;
		int[] midiNums = new int[intervals.length];
		for(int i=0; i<intervals.length; i++){
				midiNums[i] = (int) Math.round(intervals[i])+ closestTone;
		}
		return midiNums;
	}
	

}
