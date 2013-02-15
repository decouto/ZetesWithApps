package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private SparseArray<String> projects;
    
    // layout elements
    private FrameLayout fl_recent_projects;
    private ListView lv_recent_projects;
    private Button btn_start_project;
    
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
        
        // populate Recent Projects
        setUpListView();
        
        // set listeners
        btn_start_project.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(mContext, "blank project added. Refresh dashboard", Toast.LENGTH_LONG).show();
                int num = db.getRowCount() + 1;
                db.addComposition("project" + num, "05/16/1993", "uri", "uri", "uri", "uri");
                // TODO
            }
        });
    }
    
    private void setUpListView() {
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
                        int project_id = projects.keyAt(arg2);
                        Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
                        projectIntent.putExtra("project_id", project_id);
                        startActivity(projectIntent);
                    }
                } 
            });
            fl_recent_projects.removeAllViews();
            fl_recent_projects.addView(lv_recent_projects, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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
            Toast.makeText(mContext, "Open settings activity", Toast.LENGTH_LONG).show();
            // TODO
        }
        return true;
    }
    
}
