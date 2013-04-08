package pitch_detection;

import java.io.File;
import java.io.IOException;
import java.lang.StringBuilder;
import org.jfugue.Player;

public class WaveToMidi implements WaveAnalyzer {
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
	
	@Override
	public File audioToMidiFile(double[] audio, long sampleRate) {
		return audioToMidiFile(audio,sampleRate,DEFAULT_CLIP_RATE);
	}
	
	public File audioToMidiFile(double[] audio, long sampleRate, int clipRate){
		String JFugueString = this.audioToJFugueMusicString(audio, sampleRate, clipRate);
		Player player = new Player();
		File out = new File("midi_data");
		try {
			player.saveMidi(JFugueString, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public String audioToJFugueMusicString(double[] audio, long sampleRate){
		return audioToJFugueMusicString(audio,sampleRate,DEFAULT_CLIP_RATE);
	}
	public String audioToJFugueMusicString(double[] audio, long sampleRate, int clipRate) {
		int[] midiNums = audioToMidiNumbers(audio,sampleRate, clipRate);
		for(int i=0; i<midiNums.length;i++){
			if(midiNums[i]<=23){//Turn the note into a rest. This is well below the range of human voice. ~30hz.
				midiNums[i] = -1;
			}
		}
		StringBuilder out = new StringBuilder();
		out.append("T[");
		out.append(clipRate*60/32);
		out.append("]");
		int[] storedNote = new int[2];
		for(int i: midiNums){
			if(i == storedNote[0]){
				//Same as the last note. Update the duration of the stored note
				//Also works for rests
				storedNote[1]++;
			}else{
				if(storedNote[1] != 0){
					//Write the last note to the string.
					if(storedNote[0]== -1){
						out.append("R/");
					}else{
						out.append(" [");
						out.append(storedNote[0]);
						out.append("]/");
					}
				}
				out.append(.03125*storedNote[1]);
				//Update the stored note.
				storedNote[0] = i;
				storedNote[1] = 1;
			}	
		}
		return out.toString();
	}
	
	static int[] audioToMidiNumbers(double[] audio, long sampleRate, int clipRate){
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
