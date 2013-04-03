package com.appsolut.composition;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class LocationActivity extends SherlockActivity {
    
    private Context mContext;
    private Location mLocation;
    private LocationManager manager;
    
    // layout elements
    private Button btn_location_settings;
    private Button btn_start_wifi;
    private Button btn_start_wifi_gps;
    private Button btn_start_gps;
    
    private Button btn_data_settings;
    private Button btn_start_data;
    private EditText et_type;
    private EditText et_location;
    private EditText et_number;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        
        // Main
        mContext = getApplicationContext();
        
        // import layout elements
        btn_location_settings = (Button) findViewById(R.id.btn_gps_options);
        btn_start_wifi = (Button) findViewById(R.id.btn_start_wifi);
        btn_start_wifi_gps = (Button) findViewById(R.id.btn_start_wifi_gps);
        btn_start_gps = (Button) findViewById(R.id.btn_start_gps);
        
        btn_data_settings = (Button) findViewById(R.id.btn_data_options);
        btn_start_data = (Button) findViewById(R.id.btn_data_test);
        et_type = (EditText) findViewById(R.id.et_type);
        et_number = (EditText) findViewById(R.id.et_number);
        et_location = (EditText) findViewById(R.id.et_location);
        
        // set listeners
        btn_location_settings.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        
        btn_data_settings.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        
        btn_start_wifi.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                btn_start_wifi.setBackgroundColor(getResources().getColor(R.color.red));
                LocationTracking tracker = new LocationTracking("wifi");
                tracker.execute();
            }
        });
        
        btn_start_wifi_gps.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                btn_start_wifi_gps.setBackgroundColor(getResources().getColor(R.color.red));
                LocationTracking tracker = new LocationTracking("wifi");
                tracker.execute();
            }
        });
        
        btn_start_gps.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                btn_start_gps.setBackgroundColor(getResources().getColor(R.color.red));
                LocationTracking tracker = new LocationTracking("wifi");
                tracker.execute();
            }
        });
        
        btn_start_data.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                btn_start_data.setBackgroundColor(getResources().getColor(R.color.light_grey));
                NetworkData dataTrack = new NetworkData(mContext, et_type.getText().toString(), et_location.getText().toString(), et_number.getText().toString());
                dataTrack.execute();
            }
        });
    }
    
    private class NetworkData extends AsyncTask<Void, Void, Void> {
        
        private Context mContext;
        private String filename;
        private String file_url;
        private ProgressDialog dialog;
        
        private long[] pings;
        private long rate_total;
        private long time_total;
        private long time_current;
        private ArrayList<Long> rates;
        
        private NetworkData(Context mContext, String type, String location, String number) {
            this.mContext = mContext;
            this.filename = location + "_" + type + "_" + number;
            this.file_url = "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";
            pings = new long[5];
            rates = new ArrayList<Long>();
        }
        
        protected void onPreExecute() {
            dialog = new ProgressDialog(LocationActivity.this);
            dialog.setMessage("downloading");
            dialog.show();  
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Get ping data    
            Log.d("bchrobot", "starting ping");
            HttpGet request = new HttpGet("http://www.mit.edu");
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            for(int i=0; i<5; i++) {
                Log.d("bchrobot", "ping: " + i);
                long time_before = System.currentTimeMillis();
                try {
                    HttpResponse response = httpClient.execute(request);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long time_after = System.currentTimeMillis();
                long time_differnece = time_after - time_before;
                pings[i] = time_differnece;
            }
            
            // Get download stats
            try {
                Log.d("bchrobot", "starting download");
                URL url = new URL(file_url);
                int seconds = 0;
                int count = 0;
                long total = 0;
                long last_sampled = 0;
                long time_before = System.currentTimeMillis();
                byte data[] = new byte[1024];
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                InputStream input = new BufferedInputStream(urlConnection.getInputStream());
                while ((count = input.read(data)) != -1) {
                    time_current = System.currentTimeMillis();
                    long difference = (long) Math.floor((time_current - time_before) / (1000 * 5));
                    total += count;
                    if (difference > seconds) {
                        seconds++;
                        long bytes = total - last_sampled;
                        last_sampled = total;
                        long rate = (bytes / 1000 / 5);
                        rates.add(rate);
                        Log.d("bchrobot", "period " + difference + ", rate: " + rate);
                    }
                }
                long time_after = System.currentTimeMillis();
                time_total = time_after - time_before;
                rate_total = 650924 / time_total;
                input.close();
                
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            return null;
        }
        
        protected void onPostExecute(Void v) {
            try {
                Log.d("bchrobot", "writing file");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        +File.separator
                        +"SongScribe"
                        +File.separator
                        +"locations"
                        +File.separator;
                File directory = new File(path);
                directory.mkdirs();
                File gpx = new File(path + filename + ".txt");
                FileWriter f = new FileWriter(gpx);
                
                f.write(filename + "\n");
                for (int i = 0; i < 5; i++) {
                    f.write(pings[i]+"\n");
                }
                for (int i = 0; i < rates.size(); i++) {
                    f.write(String.format("Period %d, rate: %s\n", i, String.valueOf(rates.get(i))));
                }
                f.write("Total rate: " + rate_total + "kb/s in time " + (time_total/1000));
                
                f.flush();
                f.close();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                } 
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.d("bchrobot", ioe.toString());
                Toast.makeText(mContext, "failed" + ioe.toString(), Toast.LENGTH_LONG).show();
            }
        }
        
    }
    
    
    private class MyLocationListener implements LocationListener{
   
        private Location this_location;

        public void onLocationChanged(Location location) {
            //int lat = (int) (location.getLatitude());
            //int lng = (int) (location.getLongitude());
            this_location = location;
        }

        @Override
        public void onProviderDisabled(String provider) {
            //
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 
        }
    }
    
    private class LocationTracking extends AsyncTask<Void, String, Void> {
        
        // Location
        private LocationManager manager;
        private Criteria criteria;
        private LocationListener listener;
        private Location location;
        
        private FileWriter fw;
        private ProgressDialog dialog;
        private String path;
        private int i = 0;
        
        public LocationTracking(String filename) {      
            
            // Location
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            listener = new MyLocationListener();
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            
            // Files
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +File.separator
                    +"SongScribe"
                    +File.separator
                    +"locations"
                    +File.separator;
            File directory = new File(path);
            directory.mkdirs();
            
            File gpx = new File(path + filename + ".csv");
            try {
                fw = new FileWriter(gpx);
                fw.write("name, latitude, longitude\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            dialog = new ProgressDialog(LocationActivity.this);
        }
        
        protected void onPreExecute(){
            dialog.setMessage("tracking");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while(!isCancelled()){
                try {
                    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String out = i + ", " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
                    fw.write(out);
                    publishProgress(out);
                    i++;
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try{
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        
        protected void onProgressUpdate(String... update) {
            dialog.setMessage(update[0]);
        }
        
        protected void onCancelled() {
            super.onCancelled();
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        
    }

}
