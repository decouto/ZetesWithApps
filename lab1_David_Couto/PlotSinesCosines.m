function [frequency] = PlotSinesCosines(inputTime,inputFunction,numberOfTones)
% This function is a first draft attempt at single tone and multitone
% frequency detection. One inputs how many tones are expected, the time
% component of the function, and the values of the function, and the
% corresponding frequency(ies) for the function are given.

% Be careful of time incrementing to avoid aliasing in frequency domain

listAverageSinCos = [];
x = [1:1:5000]; %defines frequency range in Hz
t = inputTime;
for i = 1:5000
    listAverageSinCos = [listAverageSinCos abs(mean(inputFunction.*sin(i*t*2*pi).*cos(i*t*2*pi)))]; 
    %first attempt at combining sin and cos to eliminate phase problems
    %with the input function (this version ended up creating an error
    %factor of 2 in the frequency that is corrected in the frequency list
    %below).
end
figure;
plot(x,listAverageSinCos); 
%similar to the FFT, this function outputs an absolute value of magnitude
%in its graph, and only the first half of the data is used. This is why
%there are two peaks at opposite ends of the spectrum.
MaxToMin = sort(listAverageSinCos(1:length(listAverageSinCos)/2),'descend');
%this list is organized from max to min values in order to be sifted for
%peaks later
peaks = [];
for i = 1:length(MaxToMin)
    temp = find(listAverageSinCos == MaxToMin(i));
    if temp == 1 && listAverageSinCos(temp + 1) < MaxToMin(i)
        %if the max is the first point (occurs in the heartbeat signal)
        peaks = [peaks temp];
    else
        if listAverageSinCos(temp - 1) < MaxToMin(i) && listAverageSinCos(temp + 1) < MaxToMin(i)
            %defines a peak as a value that is greater than the points
            %before and after it and lists them
            peaks = [peaks temp];
        else %if not a peak then ignore
        end
    end
    if length(peaks) >= numberOfTones
        %no need to waste time processing noise
        break
    end
end
frequency = [];
for i = 1:numberOfTones 
    frequency = [frequency 2*x(peaks(i))]; %corrected frequency(ies)
end
end