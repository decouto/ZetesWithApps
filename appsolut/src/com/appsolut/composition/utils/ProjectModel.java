package com.appsolut.composition.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;

public class ProjectModel {
    
    // Project resources
    private final Context mContext;
    private final DatabaseHandler db;
    private HashMap<String, String> projectDetails;
    
    // Project details
    private final long project_id;
    private int project_bpm;
    private String project_name;
    private String project_file_name;
    private String project_description;
    private String project_date_created;
    
    // List resources
    private boolean is_checked;
    
    public ProjectModel(Context context, long project_id) {
        // Project resources
        mContext = context;
        db = new DatabaseHandler(mContext);
        
        // Project details
        this.project_id = project_id;
        updateDetails();
        
        // List resources
        is_checked = false;
    }
    
    public void updateDetails() {
        projectDetails = db.getCompositionDetails(project_id);

        project_name = projectDetails.get(DatabaseHandler.KEY_COMPOSITION_NAME);
        project_bpm = Integer.parseInt(projectDetails.get(DatabaseHandler.KEY_COMPOSITION_BPM));
        project_file_name = projectDetails.get(DatabaseHandler.KEY_FILE_NAME);
        project_description = projectDetails.get(DatabaseHandler.KEY_DESCRIPTION);
        project_date_created = projectDetails.get(DatabaseHandler.KEY_DATE_CREATED);
    }
    
    public String getName() {
        return project_name;
    }
    
    public int getBpm() {
        return project_bpm;
    }
    
    public String getFileName() {
        return project_file_name;
    }
    
    public String getDescription() {
        return project_description;
    }
    
    public String getDateCreated() {
        return project_date_created;
    }
    
    public void setChecked(boolean checked) {
        is_checked = checked;
    }
    
    public boolean isChecked() {
        return is_checked;
    }
    
    public File getProjectDir() {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
        + File.separator
        + "SongScribe"
        + File.separator
        + "projects"
        + File.separator
        + project_id + ".ss"
        + File.separator);
        
        directory.mkdirs();
        
        return directory;
    }

}
