package com.appsolut.composition.utils;

import java.util.HashMap;

import android.content.Context;

public class ProjectModel {
    
    private final Context mContext;
    private final DatabaseHandler db;
    private final long project_id;
    private HashMap<String, String> projectDetails;
    private String project_name;
    private String project_description;
    
    public ProjectModel(Context context, long project_id) {
        this.mContext = context;
        this.db = new DatabaseHandler(mContext);
        this.project_id = project_id;
        updateDetails();
    }
    
    private void updateDetails() {
        projectDetails = db.getCompositionDetails(project_id);
        project_name = projectDetails.get("name");
        project_description = projectDetails.get("description");
    }
    
    public String getName() {
        return project_name;
    }
    
    public String getDescription() {
        return project_description;
    }

}
