package com.appsolut.composition.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appsolut.composition.R;

public class ProjectsBaseAdapter extends BaseAdapter {
    
    private Activity activity;
    private LongSparseArray<String> data;
    private static LayoutInflater inflater = null;
    
    public ProjectsBaseAdapter(Activity a, LongSparseArray<String> d) {
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
        return data.keyAt(position);
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
        String project = data.valueAt(position);
        
        // Set values
        tv_title.setText(project);    // Title
        
        return vi;
    }
    
}