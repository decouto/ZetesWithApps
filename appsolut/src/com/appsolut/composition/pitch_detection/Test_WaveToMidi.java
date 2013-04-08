package com.appsolut.composition.pitch_detection;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test_WaveToMidi {

	@Test
	public void testFreqsToRawIntervals() {
		int[] testFreqs = {440,880,220,392,494};
		double[] exInts = {0,12,-12,-2,2};
		assertArrayEquals(exInts,WaveToMidi.freqsToRawIntervals(testFreqs).right,.1);
		
	}
	@Test
	public void testSnapIntervals(){
		int[] testFreqs = {440,880,220,392,494};
		int[] exOut = {69,81,57,67,71};
		int[] acOut = WaveToMidi.snapIntervals(WaveToMidi.freqsToRawIntervals(testFreqs));
		assertArrayEquals(exOut,acOut);
	}

}
