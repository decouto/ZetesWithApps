function read_ni_continuous(sample_rate)
% Takes a sample rate, initializes NI and starts generating and
% reading data
d = daq.getDevices();
ID = d.get('ID');

% Create the session, release the ni, then recreate
ni_s = daq.createSession('ni');
ni_s.stop();
ni_s.reset();
ni_s = daq.createSession('ni');

% Add an input
ni_s.addAnalogInputChannel(ID,'ai0','Voltage');
ni_s.Rate = sample_rate;
ni_s.IsContinuous = true;
%ni_s.DurationInSeconds = 60;

% Add the input listener
lhf = ni_s.addlistener('DataAvailable', @(src,event) plot(event.TimeStamps,event.Data));

%Start it up
ni_s.startBackground();

pause

delete(lhf);

%close('all');
ni_s.reset();
end



