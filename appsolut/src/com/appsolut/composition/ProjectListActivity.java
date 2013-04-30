package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.DatabaseHandler;
import com.appsolut.composition.utils.ProjectsBaseAdapter;

public class ProjectListActivity extends SherlockActivity {
    
    private Context mContext;
    private DatabaseHandler db;
    
    // layout elements
    private TextView tv_no_projects;
    private ListView lv_project_list;
    
    private ProjectsBaseAdapter adapter;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        getSupportActionBar().hide();
        
        mContext = this;
        db = new DatabaseHandler(mContext);
        
        LongSparseArray<String> project_list = db.getCompositionNames();
        
        // import layout elements
        tv_no_projects = (TextView) findViewById(R.id.tv_no_projects);
        lv_project_list = (ListView) findViewById(R.id.lv_project_list);
        
        adapter = new ProjectsBaseAdapter(this, project_list);
        lv_project_list.setAdapter(adapter);
        
        lv_project_list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent project_intent = new Intent(mContext, ProjectOverviewActivity.class);
                project_intent.putExtra("project_id", id);
                startActivity(project_intent);
            }
        });
        
        // Show projects if they exist
        if (project_list.size() > 0) {
            tv_no_projects.setVisibility(View.GONE);
            lv_project_list.setVisibility(View.VISIBLE);
        }
    }

}
