function freq = compute_frequency_DC(t,v,thresholdMultiplier1,thresholdMultiplier2)
% This function returns frequency from a waveform and time input.
% threshold1 is the cutoff for detecting a downward spike in the waveform
% (whether it be for simple tones or heartbeat), and threshold2 should be a
% higher number to provide a hysteresis curve (so the counter for minimums
% does not toggle on and off with noise). 

% IF IT APPEARS THAT NOT ALL CYCLES ARE BEING INCLUDED OR THE FREQUENCY IS 
% TOO BIG OR SMALL, ADJUST THRESHOLD VALUES.

    plot(t,v); 
    shg;
    freq = 1/compute_period_DC(t,v,thresholdMultiplier1,thresholdMultiplier2); 
    % uses included period computer to obtain period
end

