function period = compute_period_DC( time_matrix, voltage_matrix, thresholdMutlitplier1, thresholdMutlitplier2 )
% This function returns period from a waveform and time input.
% threshold1 is the cutoff for detecting a downward spike in the waveform
% (whether it be for simple tones or heartbeat), and threshold2 should be a
% higher number to provide a hysteresis curve (so the counter for minimums
% does not toggle on and off with noise). To skip the period calculation
% step and jump right to frequency, please use compute_frequency_DC.m

% IF IT APPEARS THAT NOT ALL CYCLES ARE BEING INCLUDED OR THE PERIOD IS TOO
% BIG OR SMALL, ADJUST THRESHOLD VALUES.

minVoltage = min(voltage_matrix); %finds minimum value to set thresholds from
threshold1 = minVoltage*thresholdMutlitplier1; %cutoff for downward detection
threshold2 = minVoltage*thresholdMutlitplier2; %hysteresis cutoff
beat = [];
flag = 0; %does not allow logging of another heartbeat until the hysteris threshold has been cleared
for i = 1:length(voltage_matrix)
    if flag == 0 && i > 1 && voltage_matrix(i) < threshold1 && voltage_matrix(i-1) > threshold1
        % if below threshold1, on a negative slope, and hystersis flag is not active
        beat = [beat time_matrix(i)]; % stores time marker for heartbeat
        flag = 1; %must now clear hysteresis threshold in order to log another beat
    elseif voltage_matrix(i) > threshold2
        %if above hysteresis threshold clear flag
        flag = 0;
    end
end
averageTime = [];
for i = 1:(length(beat) - 1)
    if i > 1 % disclude first point
        averageTime = [averageTime (beat(i) - beat(i - 1))];
        % stores time difference between consecutive beats
    end
end
period = mean(averageTime); % the period is calculated as the average of the time differences 
end