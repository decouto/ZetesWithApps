package com.appsolut.composition.pitch_detection;
import java.io.*;


import static org.junit.Assert.*;

import org.junit.Test;

import WavFile.WavFile;
import WavFile.WavFileException;

public class Test_PlotTones{
	@Test
	public void testGetBinArray1() {
		double[] in = {1.0,1.0,2.0,2.0,3.0,3.0};
		int bins = 3;
		double[] exOut = {2.0,4.0,6.0};
		double[] out = PlotTones.getBinArray(in, bins);
		assertArrayEquals(exOut, out,0.00001);

	}
	@Test
	public void testGetBinArray2() {
		double[] in = {1.0,1.0,2.0,2.0,3.0};
		int bins = 3;
		double[] exOut = {2.0,4.0,3.0};
		double[] out = PlotTones.getBinArray(in, bins);
		assertArrayEquals(exOut, out,0.00001);
	}
	@Test
	public void testGetBinArray3() {
		double[] in = {1.0,1.0,2.0,2.0,3.0};
		int bins = 2;
		double[] exOut = {4.0,5.0};
		double[] out = PlotTones.getBinArray(in, bins);
		assertArrayEquals(exOut, out,0.00001);
	}
	@Test
	public void testGetIndsWithMaxVals(){
		double[] inpArray = {7.0, 4.0, 12.0, 15.0, 5.0, 2.0, 1.0, 30.0, 3.0};
		int numMaxes = 8;
		int[] exOut = {7,3,2,0,4,1,8,5};
		int[] acOut = PlotTones.getIndsWithMaxVals(inpArray, numMaxes);
		assertArrayEquals(exOut,acOut);
	}
	@Test
	public void testGetIndsWithMaxVals2(){
		double[] inpArray = {7.0, 7.0, 7.0, 5.0, 5.0};
		int numMaxes = 5;
		int[] exOut = {0,1,2,3,4};
		int[] acOut = PlotTones.getIndsWithMaxVals(inpArray, numMaxes);
		assertArrayEquals(exOut,acOut);
	}
	@Test
	public void testGetNormalArray1(){
		double[] inpArray = {5.0,5.0,5.0,5.0,5.0};
		double[] exOut = {0.0,0.0,0.0,0.0,0.0};
		double[] acOut = PlotTones.getNormalArray(inpArray);
		assertArrayEquals(exOut,acOut,0.00001);
	}
	@Test
	public void testGetNormalArray2(){
		double[] inpArray = {3.0,4.0,5.0,6.0,7.0};
		double[] exOut = {-2.0,-1.0,0.0,1.0,2.0};
		double[] acOut = PlotTones.getNormalArray(inpArray);
		assertArrayEquals(exOut,acOut,0.00001);
	}
//------------------------------------------------------------------------------------------------------------------------
	private int[] wavFileToOut(String filename){
		try{
		WavFile wavFile = WavFile.openWavFile(new File(filename));
		int numChannels = wavFile.getNumChannels();
		int sampleSize = 8820;
		double[] inputWaveform = new double[sampleSize*numChannels];
		wavFile.readFrames(inputWaveform,sampleSize);
		inputWaveform = PlotTones.getBinArray(inputWaveform,inputWaveform.length/numChannels);
		long sampleRate= wavFile.getSampleRate();
		double[] noiseFreqs = new double[inputWaveform.length];
		return PlotTones.getProminentFrequencies(inputWaveform, sampleRate, 1, noiseFreqs);
		}catch(Exception e){
			System.err.println(e);
		}
		return null;
	}
	private double[] intToDoubleArray(int[] inp){
		double[] out = new double[inp.length];
		for(int i=0; i<inp.length; i++){
			out[i] = inp[i];
		}
		return out;
	}
	private void assertArrayClose(int[] exOut, int[] acOut, double bound){
		assertArrayEquals(intToDoubleArray(exOut),intToDoubleArray(acOut),bound);
	}
	@Test
	public void testGetProminentFrequencies10000hz(){
		String filename = "src/test-tones/10000hz.wav";
		int[] exOut = {10000};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies1000hz(){
		String filename = "src/test-tones/1000hz.wav";
		int[] exOut = {1000};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies100hz(){
		String filename = "src/test-tones/100hz.wav";
		int[] exOut = {100};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies15000hz(){
		String filename = "src/test-tones/15000hz.wav";
		int[] exOut = {15000};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies50hz(){
		String filename = "src/test-tones/50hz.wav";
		int[] exOut = {50};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies200hz(){
		String filename = "src/test-tones/200hz.wav";
		int[] exOut = {200};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies500hz(){
		String filename = "src/test-tones/500hz.wav";
		int[] exOut = {500};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies12000hz(){
		String filename = "src/test-tones/12000hz.wav";
		int[] exOut = {12000};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetProminentFrequencies20hz(){
		String filename = "src/test-tones/20hz.wav";
		int[] exOut = {20};
		int[] acOut = wavFileToOut(filename);
		assertArrayClose(exOut,acOut,5.0);
	}
//------------------------------------------------------------------------------------------------------------------------	
	private int[] multipleWavFilesToOut(String[] filenames, double[] weights){
		try{
			int sampleSize = 44100;
			double[] inputWaveform = new double[sampleSize];
			long sampleRate=0;
			int counter=0;
			for(String filename: filenames){
				WavFile wavFile = WavFile.openWavFile(new File(filename));
				double[] wave = new double[sampleSize];
				wavFile.readFrames(wave,sampleSize);
				for(int i=0; i<inputWaveform.length;i++){
					inputWaveform[i] += weights[counter]*wave[i];
				}
				sampleRate= wavFile.getSampleRate();
				counter++;
			}
			double[] noiseFreqs = new double[inputWaveform.length];
			return PlotTones.getProminentFrequencies(inputWaveform, sampleRate, filenames.length, noiseFreqs);
		}catch (Exception e){
			System.err.println(e);
		}
		return null;
	}
	@Test
	public void testGetMultipleProminentFrequencies50hz200h500hz(){
		int[] exOut = {50,200,500};
		String[] filenames = {"src/test-tones/50hz.wav","src/test-tones/200hz.wav","src/test-tones/500hz.wav"};
		double[] weights = {1.2, 1.1, 1.0};
		int[] acOut = multipleWavFilesToOut(filenames,weights);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetMultipleProminentFrequencies20hz30h40hz(){
		int[] exOut = {20,30,40};
		String[] filenames = {"src/test-tones/20hz.wav", "src/test-tones/30hz.wav", "src/test-tones/40hz.wav"};
		double[] weights = {1.2, 1.1, 1.0};
		int[] acOut = multipleWavFilesToOut(filenames,weights);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetMultipleProminentFrequencies60hz50h40hz30hz20hz(){
		int[] exOut = {60,50,40,30,20};
		String[] filenames = {	"src/test-tones/60hz.wav",
								"src/test-tones/50hz.wav",
								"src/test-tones/40hz.wav",
								"src/test-tones/30hz.wav",
								"src/test-tones/20hz.wav"};
		double[] weights = {5, 4, 3, 2, 1};
		int[] acOut = multipleWavFilesToOut(filenames,weights);
		assertArrayClose(exOut,acOut,5.0);
	}
	@Test
	public void testGetMultipleProminentFrequencies60hz50h40hz30hz20hzTight(){
		int[] exOut = {60,50,40,30,20};
		String[] filenames = {	"src/test-tones/60hz.wav",
								"src/test-tones/50hz.wav",
								"src/test-tones/40hz.wav",
								"src/test-tones/30hz.wav",
								"src/test-tones/20hz.wav"};
		double[] weights = {1.4, 1.3, 1.2, 1.1, 1.0};
		int[] acOut = multipleWavFilesToOut(filenames,weights);
		assertArrayClose(exOut,acOut,5.0);
	}
//------------------------------------------------------------------------------------------------------------------------
	@Test
	public void testWindow1(){
		double[] inp = {10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10};
		double[] acOut = new double[10];
		double[] exOut = {0, 1, 4, 7, 9, 9, 7, 4, 1, 0};
		PlotTones.window(acOut, inp, 10);
		assertArrayEquals(exOut,acOut,1);
			
	}
	@Test
	public void testWindow2(){
		double[] inp = {10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10};
		double[] acOut = new double[20];
		PlotTones.window(acOut, inp, 10);
		double[] exOut = {0, 0, 1, 2, 3, 5, 7, 8, 9, 9, 9, 9, 8, 7, 5, 3, 2, 1, 0, 0};
		assertArrayEquals(exOut,acOut,1);
			
	}
	@Test
	public void testWindow3(){
		double[] inp = {10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10};
		double[] acOut = new double[30];
		PlotTones.window(acOut, inp, 10);
		double[] exOut = {0, 0, 0, 0, 0, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9, 9, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 0, 0, 0};
		assertArrayEquals(exOut,acOut,1);	
	}
	@Test
	public void testWindow4(){
		double[] inp = {10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10};
		double[] acOut = new double[10];
		PlotTones.window(acOut, inp, 40);
		System.out.println("test window");
		double[] exOut = {0, 1, 4, 7, 9, 9, 7, 4, 1, 0};
		assertArrayEquals(exOut,acOut,1);
	}
	@Test
	public void testWindow5(){
		double[] inp = {10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10,
						10,10,10,10,10,10,10,10,10,10};
		double[] acOut = new double[30];
		PlotTones.window(acOut, inp, 40);
		double[] exOut = {0, 0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9, 9, 9, 9, 8, 7, 6, 5, 4, 3, 2, 0, 0, 0, 0, 0};
		assertArrayEquals(exOut,acOut,1);
	}
//------------------------------------------------------------------------------------------------------------------------
	@Test
	public void testAveFreqs1(){
		int[] inp = {2,2,3,3,4,4};
		int[] exOut = {2,3,4};
		int[] acOut= PlotTones.aveFreqs(inp, 3);
		assertArrayEquals(exOut,acOut);
	}
	@Test
	public void testAveFreqs2(){
		int[] inp = {1,2,3,2,3,4,3,4,5};
		int[] exOut = {2,3,4};
		int[] acOut= PlotTones.aveFreqs(inp, 3);
		assertArrayEquals(exOut,acOut);
	}
	
//------------------------------------------------------------------------------------------------------------------------

	private int[] wavFileToAllPitches(String filename, int numSecs,int windowSize, int slideRate){
		try{
		WavFile wavFile = WavFile.openWavFile(new File(filename));
		int numChannels = wavFile.getNumChannels();
		long sampleRate= wavFile.getSampleRate();
		int sampleSize = (int) sampleRate*numSecs;
		double[] inputWaveform = new double[sampleSize*numChannels];
		wavFile.readFrames(inputWaveform,sampleSize);
		return PlotTones.audioToAllFreqs(inputWaveform, sampleRate,windowSize,slideRate);
		}catch(Exception e){
			System.err.println(e);
		}
		return null;
	}
	@Test
	public void testAudioToAllFreqs50hz(){
		String filename = "src/test-tones/50hz.wav";
		int[] acOut = this.wavFileToAllPitches(filename,1,8192,8192);
		int[] exOut = new int[acOut.length];
		for(int i=0; i<exOut.length; i++){
			exOut[i] = 50;
		}
		assertArrayClose(exOut,acOut,15.0);
	}
	@Test
	public void testAudioToAllFreqs500hz(){
		String filename = "src/test-tones/500hz.wav";
		int[] acOut = this.wavFileToAllPitches(filename,1,4410,4410);
		int[] exOut = new int[acOut.length];
		for(int i=0; i<exOut.length; i++){
			exOut[i] = 500;
		}
		assertArrayClose(exOut,acOut,15.0);
	}
	@Test
	public void testAudioToAllFreqs500hzBig(){
		String filename = "src/test-tones/500hz.wav";
		int[] acOut = this.wavFileToAllPitches(filename,1,44100,44100);
		int[] exOut = new int[acOut.length];
		for(int i=0; i<exOut.length; i++){
			exOut[i] = 500;
		}
		assertArrayClose(exOut,acOut,15.0);
	}
}
	
	
