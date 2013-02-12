function freqs = plot_keys(num_keys)
% Return a vector of frequencies associated with 
% pressed keys.

% Get a vector of zeros to store key frequencies
freqs = zeros(1,num_keys);
for i=1:num_keys
    disp('Press and hold next music key, then hit space');
    pause
    [t,v] = read_ni(0.1,50000);
    freqs(i) = compute_frequency_DC(t,v);
end
figure;
plot(freqs);
end

