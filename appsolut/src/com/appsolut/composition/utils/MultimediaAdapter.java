package com.appsolut.composition.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MultimediaAdapter extends BaseAdapter {
    
    Context mContext;
    DatabaseHandler db;
    long project_id;
    long[] image_ids;
    
    public MultimediaAdapter(Context context, long project_id) {
        mContext = context;
        db = new DatabaseHandler(mContext);
        this.project_id = project_id;
        image_ids = db.getMediaForProject(project_id);
    }

    @Override
    public int getCount() {
        return image_ids.length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = new ImageView(mContext);
        // TODO Auto-generated method stub
        return null;
    }

}
