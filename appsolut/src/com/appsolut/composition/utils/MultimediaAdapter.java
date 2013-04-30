package com.appsolut.composition.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MultimediaAdapter extends BaseAdapter {
    
    Context mContext;
    DatabaseHandler db;
    ProjectModel project_model;
    long project_id;
    long[] image_ids;
    int THUMBNAIL_SIZE = 80;
    
    public MultimediaAdapter(Context context, long project_id) {
        mContext = context;
        db = new DatabaseHandler(mContext);
        this.project_id = project_id;
        project_model = new ProjectModel(mContext, project_id);
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
        File dir = project_model.getProjectDir();
        File image = new File(dir, (int) image_ids[position] + ".jpg");
        ImageView iv = new ImageView(mContext);
        iv.setImageBitmap(getPreview(image));

        return iv;
    }
    
    private Bitmap getPreview(File image) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
        return BitmapFactory.decodeFile(image.getPath(), opts);     
    }

}
