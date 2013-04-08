package com.appsolut.composition.utils;

import java.io.File;

/**
 * @author DC
 *
 */
public interface WaveAnalyzer {
	/**
	 * 
	 * @param audio The raw audio data. Assuming a single channel.
	 * @param sampleRate The rate at which the audio was sampled
	 * @return a midi file
	 */
	public File audioToMidiFile(double[] audio, long sampleRate);
	/**
	 * 
	 * @param audio The raw audio data. Assuming a single channel.
	 * @param sampleRate The rate at which the audio was sampled
	 * @return a JFugue Music String
	 */
	public String audioToJFugueMusicString(double[] audio, long sampleRate);
}
