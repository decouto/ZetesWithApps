function [t,v] = read_ni(seconds, sample_rate)
% Takes a sample rate and time interval, initializes NI and takes
% a set of data.
d = daq.getDevices();
ID = d.get('ID');

% Create the session, release the ni, then recreate
ni_s = daq.createSession('ni');
ni_s.stop();
ni_s.reset();
ni_s = daq.createSession('ni');

% Set up for analog input
ni_s.addAnalogInputChannel(ID,'ai0','Voltage');
ni_s.Rate = sample_rate;
ni_s.DurationInSeconds = seconds;

% Read the data
[v,t] = ni_s.startForeground;

% Release and return
ni_s.release();
end



