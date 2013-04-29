package com.appsolut.composition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appsolut.composition.utils.DatabaseHandler;

public class DashboardActivity extends SherlockActivity {
    
    private Context mContext;
    private DatabaseHandler db;
    private LongSparseArray<String> projects;
    
    // layout elements
    private FrameLayout fl_recent_projects;
    private ListView lv_recent_projects;
    private Button btn_start_project;
    
    // dialog elements
    private LayoutInflater dialogInflator;
    private View newProjectDialogView;
    private AlertDialog newProjectDialog;
    private AlertDialog.Builder alertDialogBuilder;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle("Dashboard");
        
        mContext = this;
        db = new DatabaseHandler(mContext);
        
        // import layout elements
        fl_recent_projects = (FrameLayout) findViewById(R.id.fl_recent_projects);
        btn_start_project = (Button) findViewById(R.id.btn_start_project);
        
        // set listeners
        btn_start_project.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                // get prompts.xml view
                dialogInflator = LayoutInflater.from(mContext);
                newProjectDialogView = dialogInflator.inflate(R.layout.dialog_new_project, null);
                alertDialogBuilder = new AlertDialog.Builder(mContext);
                // Set alertDialogBuilder view
                alertDialogBuilder.setView(newProjectDialogView);
                final EditText et_project_name = (EditText) newProjectDialogView.findViewById(R.id.et_new_project_name);
                String projectName = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(new Date());
                et_project_name.setHint(projectName);
                et_project_name.setText(projectName);
                // set dialog message
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create new project
                        String projectName = et_project_name.getText().toString();
                        long projectID = 0; // TODO db.addComposition(projectName, "", "");
                        newProjectDialog.dismiss();
                        if (projectID >= 0) {
                            // creation succeeded
                            Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
                            projectIntent.putExtra("project_id", projectID);
                            Log.d("project", "stored: " + projectID);
                            startActivity(projectIntent);
                        } else {
                            // creation failed
                            Toast.makeText(mContext, projectName+" could not be created", Toast.LENGTH_LONG).show();
                        }
                    }    
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newProjectDialog.dismiss();
                    }
                });
 
                // create alert dialog
                newProjectDialog = alertDialogBuilder.create();
                newProjectDialog.show();
            }
        });
    }
    
    public void onResume() {
        super.onResume();
        
        // populate Recent Projects
        setupListView();
    }
    
    private void setupListView() {
        int rows = db.getRowCount();
        final boolean rowOverflow = rows > 5;
        int rowsToShow = Math.min(5, rows);
        if (rows > 0) {
            projects = db.getCompositionNames();
            String[] names = new String[rowsToShow];
            for (int i = 0; i < rowsToShow; i++) {
                if (i == 4 && rowOverflow){
                    names[i] = "See more";
                } else {
                    names[i] = projects.valueAt(i);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.project_row, R.id.label, names);
            lv_recent_projects = new ListView(mContext);
            lv_recent_projects.setAdapter(adapter);
            lv_recent_projects.setScrollContainer(false);
            lv_recent_projects.setOnItemClickListener(new OnItemClickListener(){
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (arg2 == 4 && rowOverflow) {
                        startActivity(new Intent(mContext, ProjectListActivity.class));
                    }
                    else {
                        long project_id = projects.keyAt(arg2);
                        Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
                        projectIntent.putExtra("project_id", project_id);
                        startActivity(projectIntent);
                    }
                } 
            });
            lv_recent_projects.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (arg2 == 4 && rowOverflow) {
                        startActivity(new Intent(mContext, ProjectListActivity.class));
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Confirm delete project")
                            .setMessage("Are you sure you want to delete this project?")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_launcher)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(mContext, "Prject Deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                        builder.create().show(); 
                    }
                    return true;
                }
            });
            fl_recent_projects.removeAllViews();
            fl_recent_projects.addView(lv_recent_projects, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Skip landing page item
        menu.add("Settings");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Settings")) {
            // Launch Settings activity
            startActivity(new Intent(mContext, SettingsActivity.class));
        }
        return true;
    }
    
}
