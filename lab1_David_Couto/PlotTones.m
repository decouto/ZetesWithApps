function [frequency] = PlotTones(inputTime,inputFunction,numberOfTones)
% Second attempt at making at single tone and multitone
% frequency detection. One inputs how many tones are expected, the time
% component of the function, and the values of the function, and the
% corresponding frequency(ies) for the function are given. This attempt
% comes after my first draft (PlotSins.m) and impliments a Fourier
% Transform approach I learned while having a conversation about pitch 
% detection with my friend Ariel Wexler (6-2 2013). He suggested this more
% conventional means. 

% Be careful of time incrementing to avoid aliasing in frequency domain

listAverageSin = [];
listAverageCos = [];
x = [1:1:5000];
t = inputTime;
for i = 1:5000
    listAverageSin = [listAverageSin mean(inputFunction.*sin(i*t*2*pi))];
    listAverageCos = [listAverageCos mean(inputFunction.*cos(i*t*2*pi))];
    listMagnitude = sqrt(listAverageSin.^2 + listAverageCos.^2);
    %uses the approach of RMS of the sin and cos portions rather than a
    %messy multiplication of sin and cos as previously done
end
figure;
plot(x,listMagnitude); % no longer have two peaks at opposite ends of graph
MaxToMin = sort(listMagnitude(:),'descend');
%this list is organized from max to min values in order to be sifted for
%peaks later
peaks = [];
for i = 1:length(listMagnitude)
    temp = find(listMagnitude == MaxToMin(i));
    if temp == 1 && listMagnitude(temp + 1) < MaxToMin(i)
        %if the max is the first point (occurs in the heartbeat signal)
        peaks = [peaks temp];
    else
        if listMagnitude(temp - 1) < MaxToMin(i) && listMagnitude(temp + 1) < MaxToMin(i)
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
    frequency = [frequency x(peaks(i))]; % no longer need to correct frequency
end
end