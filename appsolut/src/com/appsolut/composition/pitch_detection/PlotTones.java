/**
 * Java implementation of David Couto's pitch detection algorithm originally written for Matlab.
 * Original algorithm is in a file called FFTMyFunction.m and reproduced in a note in this project.
 */
package com.appsolut.composition.pitch_detection;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

import android.util.Log;  


public class PlotTones {

   // Logs
    private static final boolean LOG_INPUT_FREQS = true;
    private static final boolean LOG_MAX_INDS = false;
    private static final boolean LOG_PROM_FREQS = false;
    private static final boolean LOG_FREQ_GRAPH = false;
    
    
	/**
	 * Takes audio and finds the most prominent frequency for each part.
	 * 
	 * @param audio
	 * @param sampleRate
	 * @param clipRate | The number of frequencies to produce per second
	 * @return
	 */
	private static final String TAG_PRE = "Pre-smoothing";
	
	static int[] audioToFreqs(double[] audio,long sampleRate,int clipRate){
		double lenAudioInSecs = audio.length*1.0/sampleRate;
		int WINDOW_SIZE = 16384;
		int SLIDE = 4096;
		int numClips = (int) (clipRate*lenAudioInSecs);
		int[] inpFreqs = audioToAllFreqs(audio,sampleRate,WINDOW_SIZE,SLIDE);
		return aveFreqs(inpFreqs, numClips);
	}
	
	/**
	 * Calculates the frequency for a large number of points using a window 
	 * 
	 * @param audio
	 * @param sampleRate 
	 * @param windowSize 
	 * @param slide | The amount the window is moved over every iteration
	 * @return
	 */
	static int[] audioToAllFreqs(double[] audio,long sampleRate, int windowSize, int slide){
		int numWindows = audio.length/slide; 
		int[] inpFreqs = new int[numWindows];
		double[] windowedAudio = new double[windowSize];
		//Iterating along the input Audio calculate the prominent frequency in a window centered on every sample
		//Move the window over slide units every time
		for(int i=0; i<numWindows; i++){
			window(windowedAudio,audio,i*slide + windowSize);
			inpFreqs[i] = getProminentFrequencies(windowedAudio,sampleRate,1,null)[0];
		}
		if (LOG_INPUT_FREQS) Log.v(TAG_PRE,Arrays.toString(inpFreqs)); // TODO
		smoothFreqs(inpFreqs);
		return inpFreqs;
	}
	
	/**
	 * Modifies the input array to make it smooter
	 * @modifies inp
	 * @param inp the array of frequencies to be smoothed
	 */
	private static void smoothFreqs(int[] inp){
		//TODO: Implement me!
	}
	
	/**
	 * takes inpFreqs and averages (does not just bin) it into an array of size numClips
	 * 
	 * @param inpFreqs
	 * @param numClips
	 * @return
	 */
	static int[] aveFreqs(int[] inpFreqs, int numClips){
		int lenClipInSamples = inpFreqs.length/numClips;
		//Average the values in inpFreqs to get the correct number of Pitches
		int[] pitches = new int[numClips];
		for(int i=0; i<numClips; i++){
			for(int j=i*lenClipInSamples; j<(i+1)*lenClipInSamples; j++){
				if(j >= inpFreqs.length){
					j = inpFreqs.length-1;
				}
				pitches[i] += inpFreqs[j];
			}
			pitches[i] /= lenClipInSamples;
		}
		return pitches;
	}
	
	/**
	 * Modifies an array in place to give the Hann function of the input data centered on the prescribed index
	 * 
	 * Formula for Hann: w(i) = .5 * (1 - Math.cos(2*Math.PI*i/(width-1)))
	 * 
	 * @param inp
	 * @param center
	 * @param width
	 * @modifies out 
	 */
	static void window(double[] out, double[] inp, int center){
		int width = out.length;
		int stInd = center - width/2;
		int stopInd = center + width/2;
		if(stopInd >= inp.length){
			stInd = inp.length - 1 - width;
		}
		for(int i=0;i<width;i++){
			if(stInd+i<0 || stInd+i >= inp.length){
				out[i] = 0;
			}else{
				out[i] = inp[stInd+i] * .5 * (1 - Math.cos(2*Math.PI*i/(width-1)));//Hann Window
				//out[i] = inp[stInd+i];//Rectangular window, for debugging
			}
		}
	}
	
	/**
	 * Uses Fourier analysis to find the most prominent frequencies in a given section of audio
	 *  
	 * @param inputWaveform
	 * @param sampleRate
	 * @param numTones
	 * @param noiseFreqs
	 * @return an array of frequencies sorted by prominence in descending order
	 */
	static int[] getProminentFrequencies(double[] inputWaveform, long sampleRate, int numTones, double[] noiseFreqs){
		int BINS = (int) Math.round(Math.pow(2,14));
		
		double[] working_wave = new double[2*inputWaveform.length];
		
		for(int i=0;i<inputWaveform.length;i++){
			working_wave[i] = inputWaveform[i];
		}
		
		normalArray(working_wave);//normalizes the working wave by subtracting its average value from every element
		DoubleFFT_1D fftBase = new DoubleFFT_1D(BINS);

		fftBase.realForwardFull(working_wave);
		
		double[] magnitudes = new double[inputWaveform.length];
		for(int i=0;i<magnitudes.length;i++){//Loads the magnitudes of the complex numbers into an array
			magnitudes[i] = working_wave[2*i]*working_wave[2*i] + working_wave[2*i+1]*working_wave[2*i+1];
		}
		
		int[] promInds 	=   getIndsWithMaxVals(magnitudes,numTones);//Finds the indexes of the most prominent frequencies
		
		int[] promFreqs = 	new int[promInds.length];//Converts these indexes into frequencies
		for(int i=0; i<promInds.length;i++){
			promFreqs[i] = (int) (1+promInds[i]*sampleRate/(BINS*2.0));
		if (LOG_PROM_FREQS) Log.v("ProminentFreqs",Arrays.toString(promFreqs)); // TODO
		if (LOG_FREQ_GRAPH) Log.v("FreqGraph",Arrays.toString(working_wave)); // TODO
		}
		
		return promFreqs;
	}// end run method body
	
	/**
	 * Puts an array into a (presumably) smaller array with a specified number of bins. Specifies bin values by rounding.
	 * Does not modify the original list.
	 * 
	 * Small note. Defines values to start exactly at their index rather than halfway between indices.
	 * @param inpArray
	 * @param numberBins
	 * @return
	 */
	static double[] getBinArray(double[] inpArray, int numberBins){
		double[] outArray = new double[numberBins];
		for(int i=0;i<inpArray.length;i++){
			int bin = (int)(i*(numberBins+0.0)/inpArray.length);
			outArray[bin] += inpArray[i];
		}
		return outArray;
	}
	
	/**
	 * Finds numMaxes of indices with associated largest values in an array.
	 * Returns a list of the indices sorted from largest to smallest associated value.
	 * @param inpArray
	 * @param numMaxes
	 * @return
	 */
	static int[] getIndsWithMaxVals(double[] inpArray, int numMaxes){
		int[] outArray = new int[numMaxes];
		double[] prominence = new double[numMaxes];
		for(int i=0;i<numMaxes;i++){
			prominence[i] = -1;
			outArray[i] = -1;
		}
		double final_el = 0;  // TODO remove this
		for(int i=0;i<inpArray.length;i++){
			double el = inpArray[i];
			double comp = prominence[prominence.length-1];
			if(el > comp){
				for(int j=0;j<prominence.length;j++){
					if(el>prominence[j]){//insert the element preserving the lists sort
						for(int k=prominence.length-1; k>j; k--){
							prominence[k] = prominence[k-1];
							outArray[k] = outArray[k-1];	
						}
						prominence[j] = el;
						outArray[j] = i;
						break;
					}
					
				}//end j for loop
			}
			final_el = el;   // TODO remove this
		}// end i for loop
		if (LOG_MAX_INDS) Log.v("MaxInds", ""+final_el);  // TODO remove this
		return outArray;
	}
	
	/**
	 * Normalizes an array of doubles by finding its average value and subtracting it from every element.
	 *
	 * @modifies input array
	 * @param inpArray
	 */
	static void normalArray(double[] inp){
		//Get the average value of the binned input wave (working_wave) then use it to normalize the working wave
		double arrayAve = 0;
		for(double d: inp){
			arrayAve += d;
		}
		arrayAve /= inp.length;
		for(int i=0; i<inp.length;i++){
			inp[i] -= arrayAve;
		}
	}


}