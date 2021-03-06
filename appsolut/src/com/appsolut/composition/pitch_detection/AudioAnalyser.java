package com.appsolut.composition.pitch_detection;

import java.util.ArrayList;

public class AudioAnalyser {
	int bpm;
	int ppq;
	long sampleRate;
	int clipRate;
	FrequencyFinder ff;
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
	
	public AudioAnalyser(int _bpm, int _ppq, long _sampleRate, int _clipRate){
		bpm = _bpm;
		ppq = _ppq;
		sampleRate = _sampleRate;
		clipRate = _clipRate;
		ff = new FrequencyFinder(sampleRate, clipRate);
	}
	
	public Pair<Integer[],Long[]> analyseAudio(double[] audio){
		return getMidiNumsWithTicks(audioToMidiNums(audio));
	}
	
	/**
	 * Takes an array of midi numbers without explicitly specified durations and explicitly defines their durations.
	 * 
	 * @param midiNums an array of midiNumbers each corresponding to a single unit of duration. OFF_VAL indicates silence.
	 * @param clipRate the number of frequencies found per second
	 * @return A pair of arrays representing midi numbers and their durations in ticks
	 */
	private Pair<Integer[],Long[]> getMidiNumsWithTicks(int[] midiNums){
		int calibration_mult = 10;//This is only here because I don't know what the formula for ticks/occurrence should be� DCD
		long TICKS_PER_OCCURRENCE = calibration_mult*bpm*ppq/(60*clipRate);
		ArrayList<Integer> newMidiNums = new ArrayList<Integer>();
		ArrayList<Long> ticksPerMidiNum = new ArrayList<Long>();
		int lastNum = midiNums[0];
		int dur = 1;
		for(int m: midiNums){
			if(m != lastNum){
				newMidiNums.add(m);
				ticksPerMidiNum.add(dur*TICKS_PER_OCCURRENCE);
				lastNum = m;
				dur = 1;
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
	
	/**
	 * Simple wrapper function that (using not so simple functions) turns input audio into a series of midi numbers without explicit duration.
	 * 
	 * @param audio The audio to be converted to midi
	 * @param sampleRate The sample rate the audio was recorded at
	 * @param clipRate The number of frequencies found in the audio per second
	 * @return an array of midi numbers
	 */
	private int[] audioToMidiNums(double[] audio){
		return 	snapIntervals(
				freqsToRawIntervals(
				ff.findFrequencies(audio)));
	}
	
	/**
	 * Takes an array of frequencies drawn from input audio and turns it into a set of intervals from the first frequency
	 * 
	 * @param freqs 
	 * @return The first frequency and an array of intervals. The first note itself is included in the array of intervals as 0.
	 */
	private Pair<Integer,double[]> freqsToRawIntervals(int[] freqs){
		int baseFreq = freqs[0];
		double[] intervals = new double[freqs.length];
		for(int i=0; i<freqs.length; i++){
			intervals[i] = 12*Math.log(1.0*freqs[i]/baseFreq)/Math.log(2);//The number of half steps from baseFreq
		}
		return new Pair<Integer,double[]>(baseFreq,intervals);
	}
	
	/**
	 * Takes a base note and array of intervals and produces an array of midi numbers.
	 * 
	 * @param inp
	 * @return an array of midi numbers
	 */
	private int[] snapIntervals(Pair<Integer,double[]> inp){
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
				midiNums[i] = (int) Math.round(intervals[i]) + closestTone;
		}
		smoothMidiNums(midiNums);
		return midiNums;
	}
	
	/**
	 * Uses a triangular average to smooth out the midi numbers
	 * @param inp
	 * @modifies inp
	 */
	private void smoothMidiNums(int[] inp){
		if(inp.length >= 3){
			int[] smoothedInp = new int[inp.length];
			for(int i=3; i<inp.length-3; i++){
				smoothedInp[i] = (4*inp[i] + 3*(inp[i-1]+inp[i+1]) + 2*(inp[i-2]+inp[i+2]) + 1*(inp[i-3]+inp[i+3]))/16;
			}
			smoothedInp[0] = inp[0];
			smoothedInp[1] = inp[1];
			smoothedInp[2] = inp[2];
			smoothedInp[inp.length-1] = inp[inp.length-1];
			smoothedInp[inp.length-2] = inp[inp.length-2];
			smoothedInp[inp.length-3] = inp[inp.length-3];
			inp = smoothedInp;
		}
	}
}
