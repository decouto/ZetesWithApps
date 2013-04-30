package com.appsolut.composition;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appsolut.composition.utils.DatabaseHandler;
import com.appsolut.composition.utils.ProjectModel;

public class ProjectOverviewActivity extends SherlockActivity {
    
    private Context mContext;
    private long project_id;
    private ProjectModel projectModel;
    private DatabaseHandler db;
    private boolean is_editable;
    
    // media codes
    private static final int CODE_CAPTURE_IMAGE = 0;
    private static final int CODE_PICK_IMAGE = 1;
    
    // layout elements
    private EditText et_project_name;
    private TextView tv_project_bpm;
    private TextView tv_project_date_created;
    private EditText et_project_description;
    private Button btn_add_media;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_overview);
        getSupportActionBar().hide();
        mContext = this;
        db = new DatabaseHandler(mContext);
        
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            finish();
        }
        project_id = bundle.getLong("project_id");
        projectModel = new ProjectModel(mContext, project_id);
        is_editable = false;
        
        
        // import layout elements
        et_project_name = (EditText) findViewById(R.id.et_project_name);
        tv_project_bpm = (TextView) findViewById(R.id.tv_project_bpm);
        tv_project_date_created = (TextView) findViewById(R.id.tv_project_date_created);
        et_project_description = (EditText) findViewById(R.id.et_project_description);
        btn_add_media = (Button) findViewById(R.id.btn_add_media);
        
        // set layout values
        et_project_name.setText(projectModel.getName());
        tv_project_bpm.setText(String.valueOf(projectModel.getBpm()));
        tv_project_date_created.setText(projectModel.getDateCreated());
        et_project_description.setText(projectModel.getDescription());
        
        // set listeners
        btn_add_media.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // Prepare for new photo
                if (isIntentAvailable(mContext, MediaStore.ACTION_IMAGE_CAPTURE)) {
                    // Ready file resource
                    String date_created = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date());
                    long media_id = db.addMedia(project_id, date_created);
                    File f = new File(projectModel.getProjectDir(), (int) media_id + ".jpg");
                    Log.d("MediaCapture", "URI: " + f.getAbsolutePath());
                    // Launch camera intent
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(takePictureIntent, CODE_CAPTURE_IMAGE);
                } else {
                    Toast.makeText(mContext, "Camera not available", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        if (is_editable) {
            menu.add("Save");
        } else {
            menu.add("Edit");
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Edit")) {
            toggleEdit();
        }
        else if (title.equals("Save")) {
            String name = et_project_name.getText().toString();
            name = (name == "") ? projectModel.getName() : name;
            String description = et_project_description.getText().toString();
            description = (description == "") ? projectModel.getDescription() : description;
            ContentValues args = new ContentValues();
            args.put(DatabaseHandler.KEY_COMPOSITION_NAME, name);
            args.put(DatabaseHandler.KEY_DESCRIPTION, description);
            db.updateComposition(project_id, args);
            Toast.makeText(mContext, "Project details updated", Toast.LENGTH_LONG).show();
            toggleEdit();
        }
        return true;
    }
    
    private void toggleEdit() {
        if (is_editable) {
            et_project_description.setEnabled(false);
            et_project_description.setFocusable(false);
            et_project_name.setEnabled(false);
            et_project_name.setFocusable(false);
        } else {
            et_project_description.setEnabled(true);
            et_project_description.setFocusable(true);
            et_project_name.setEnabled(true);
            et_project_name.setFocusable(true);
        }
        is_editable = !is_editable;
    }
    
    private boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        // Picked image from gallery
        case CODE_PICK_IMAGE:
            if (resultCode == Activity.RESULT_OK) {
                // TODO
                Log.d("GalleryImage", "Result was ok");
            }
            break;
            
        // Took new picture
        case CODE_CAPTURE_IMAGE:
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext, "Canceled, no photo captured.", Toast.LENGTH_LONG).show();
                return;
            }

            if (resultCode == RESULT_OK) {
                // TODO deal with image
                Toast.makeText(mContext, "Image saved successfully", Toast.LENGTH_LONG).show();
                return;
            }
            break;
            
        default:
            // TODO define default behavior
            break;
        }
    }

}
