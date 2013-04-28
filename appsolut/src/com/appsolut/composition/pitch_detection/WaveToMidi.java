package com.appsolut.composition.pitch_detection;

import java.util.ArrayList;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;


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
	
	public MidiFile audioToMidiFile(double[] audio, long sampleRate) {
		return audioToMidiFile(audio,sampleRate,DEFAULT_CLIP_RATE);
	}
	
	public MidiFile audioToMidiFile(double[] audio, long sampleRate, int clipRate){
        MidiFile midiFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION);
		MidiTrack track = new MidiTrack();
		Pair<Integer[],Long[]> midiNums = getMidiNumsWithTicks(audioToMidiNums(audio,sampleRate,clipRate));
		long onTick = 0;
		for(int i=0;i<midiNums.left.length;i++){
			track.insertNote(0, midiNums.left[i], 127, onTick,midiNums.right[i]);
			onTick += midiNums.right[i];
		}
		midiFile.addTrack(track);
		return midiFile;
		
	}
	public Pair<Integer[],Long[]> getMidiNumsWithTicks(int[] midiNums){
		long TICKS_PER_OCCURRENCE = 100;
		ArrayList<Integer> newMidiNums = new ArrayList<Integer>();
		ArrayList<Long> ticksPerMidiNum = new ArrayList<Long>();
		int lastNum = midiNums[0];
		int dur = 0;
		for(int m: midiNums){
			if(m != lastNum){
				newMidiNums.add(m);
				ticksPerMidiNum.add(dur*TICKS_PER_OCCURRENCE);
				lastNum = m;
			}else{
				dur++;
			}
		}
		Integer[] outMidiNums= new Integer[0];
		outMidiNums = newMidiNums.toArray(outMidiNums);
		Long[] outTicksPer = new Long[0];
		outTicksPer = ticksPerMidiNum.toArray(outTicksPer);
		return new Pair<Integer[],Long[]>(outMidiNums,outTicksPer);
		
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
