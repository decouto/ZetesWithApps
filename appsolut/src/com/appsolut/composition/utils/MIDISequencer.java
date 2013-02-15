package com.appsolut.composition.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class MIDISequencer {
    
    // Note lengths (derived from 32 ticks to the crotchet
    static final int SEMIQUAVER = 4;    // 16th note
    static final int QUAVER = 8;        // Eight note
    static final int CROTCHET = 16;     // Quarter note
    static final int MINIM = 32;        // Half note
    static final int SEMIBREVE = 64;    // Whole note
    
    // MIDI file standard header
    static final int[] header = new int[] {
        0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, // ---
        0x00, 0x00,                                     // single-track format
        0x00, 0x01,                                     // one track
        0x00, 0x10,                                     // 16 ticks per crotchet
        0x4d, 0x54, 0x72, 0x6B                          // ---
    };
    
    // MIDI file standard footer
    static final int footer[] = new int[] {
        0x01, 0xFF, 0x2F, 0x00                          // ---
    };
    
    // MIDI event to set the tempo
    static final int tempoEvent[] = new int[] {
        0x00, 0xFF, 0x51, 0x03,                         // ---
        0x0F, 0x42, 0x40                                // Default 1 million usec per crotchet
    };
    
    // MIDI event to set the key signature
    // (only necessary for editing applications) 
    static final int keySigEvent[] = new int[] {
        0x00, 0xFF, 0x59, 0x02,                         // ---
        0x00,                                           // C
        0x00                                            // major
    };
    
    // MIDI event to set the time signature
    // (only necessary for editing applications)
    static final int timeSigEvent[] = new int[] {
        0x00, 0xFF, 0x58, 0x04,                          // ---
        0x04,                                            // numerator
        0x02,                                            // denominator (2==4, because it's a power of 2)
        0x30,                                            // ticks per click (not used)
        0x08                                             // 32nd notes per crotchet 
    };
    
    // The collection of events to play, in time order
    protected Vector<int[]> playEvents;
    
    /**
     * Construct a new MidiSequencer with an empty playback event list
     */
    public MIDISequencer() {
        playEvents = new Vector<int[]>();
    }
    
    
    
    /**
     * Convert an array of integers which are assumed to
     * contain unsigned bytes into an array of bytes
     * @param ints array of integers containing unsigned bytes
     * @return array of bytes
     */
    protected static byte[] intArrayToByteArray (int[] ints) {
        int l = ints.length;
        byte[] out = new byte[l];
        for (int i = 0; i < l; i++) {
            out[i] = (byte) ints[i];
        }
        return out;
    }
    
    /**
     * Store a note-on event
     * @param delta the note offset in number of ticks
     * @param note the note to turn on. Refer to MIDI spec for details
     * @param velocity the velocity with which the note is played
     */
    public void noteOn (int delta, int note, int velocity) {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x90;
        data[2] = note;
        data[3] = velocity;
        playEvents.add (data);
    }
    
    /**
     * Store a note-off event
     * @param delta the length of the note in ticks
     * @param note the note to turn off
     */
    public void noteOff (int delta, int note) {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x80;
        data[2] = note;
        data[3] = 0;
        playEvents.add (data);
    }
    
    /**
     * Store a program-change event at current position
     * @param prog the program change event. Refer to MIDI spec for details
     */
    public void progChange (int prog) {
        int[] data = new int[3];
        data[0] = 0;
        data[1] = 0xC0;
        data[2] = prog;
        playEvents.add (data);
    }
    
    /**
     * Store a note-on event followed by a note-off event a note length later.
     * There is no delta value Ñ the note is assumed to follow the previous one with no gap.
     * @param duration the duration of the note in ticks
     * @param note the note to play. Refer to MIDI spec for details
     * @param velocity the velocity with which the note is played
     */
    public void noteOnOffNow (int duration, int note, int velocity) {
        noteOn (0, note, velocity);
        noteOff (duration, note);
    }
    
    /**
     * Write a sequence of notes with a fixed velocity
     * @param sequence the note sequence defined as an int[]
     *        where one note is two elements: [note1, length1, note2, length2]
     *        (in this case rests have a note value of -1)
     * @param velocity the fixed velocity
     */
    public void noteSequenceFixedVelocity (int[] sequence, int velocity)
    {
      boolean lastWasRest = false;
      int restDelta = 0;
      for (int i = 0; i < sequence.length; i += 2) {
        int note = sequence[i];
        int duration = sequence[i + 1];
        if (note < 0) {
          // This is a rest
          restDelta += duration;
          lastWasRest = true;
        }
        else {
          // A note, not a rest
          if (lastWasRest) {
            noteOn (restDelta, note, velocity);
            noteOff (duration, note);
          }
          else {
            noteOn (0, note, velocity);
            noteOff (duration, note);
          }
          restDelta = 0;
          lastWasRest = false;
        }
      }
    }
    
    /**
     * Write the stored MIDI events to a file
     * @param filename the full path of the file to write to
     */
    public void writeToFile (String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream (filename);
        
        // Write the standard header
        fos.write (intArrayToByteArray (header));

        // Calculate the amount of track data
        // _Do_ include the footer but _do not_ include the 
        // track header

        int size = tempoEvent.length + keySigEvent.length + timeSigEvent.length + footer.length;

        for (int i = 0; i < playEvents.size(); i++) {
            size += playEvents.elementAt(i).length;
        }

        // Write out the track data size in big-endian format
        // Valid for up to 64k of data 
        int high = size / 256;
        int low = size - (high * 256);
        fos.write ((byte) 0);
        fos.write ((byte) 0);
        fos.write ((byte) high);
        fos.write ((byte) low);


        // Write the standard metadata
        fos.write (intArrayToByteArray (tempoEvent));
        fos.write (intArrayToByteArray (keySigEvent));
        fos.write (intArrayToByteArray (timeSigEvent));

        // Write out the events (notes, etc.)
        for (int i = 0; i < playEvents.size(); i++) {
            fos.write (intArrayToByteArray (playEvents.elementAt(i)));
        }

        // Write the footer and close
        fos.write (intArrayToByteArray (footer));
        fos.close();
    }
}
