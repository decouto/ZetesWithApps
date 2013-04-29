package com.appsolut.composition.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appsolut.composition.R;

public class ProjectsBaseAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    
    public ProjectsBaseAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater =  (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.project_row, null);
        }
        
        // Import layout elements
        TextView tv_title = (TextView) vi.findViewById(R.id.tv_project_list_title);
        
        // Create project resource
        HashMap<String, String> project = new HashMap<String, String>();
        project = data.get(position);
        
        // Set values
        tv_title.setText(project.get(DatabaseHandler.KEY_COMPOSITION_NAME));    // Title
        
        return vi;
    }
    
}