package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.appsolut.composition.utils.DatabaseHandler;

public class ProjectListActivity extends SherlockActivity {
    
    private Context mContext;
    private DatabaseHandler db;
    private LongSparseArray<String> projects;
    
    // layout elements
    private TextView tv_no_projects;
    private ListView lv_project_list;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        getSupportActionBar().hide();
        
        mContext = this;
        db = new DatabaseHandler(mContext);
        
        // import layout elements
        tv_no_projects = (TextView) findViewById(R.id.tv_no_projects);
        lv_project_list = (ListView) findViewById(R.id.lv_project_list);
        
        // populate project list
        if (db.getRowCount() > 0) {            
            projects = db.getCompositionNames();
            String[] names = new String[projects.size()];
            for (int i = 0; i < projects.size(); i++) {
                names[i] = projects.valueAt(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.project_row, R.id.label, names);
            lv_project_list.setAdapter(adapter);
            lv_project_list.setOnItemClickListener(new OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("ListView", "item clicked");
                    String item = String.valueOf(projects.keyAt(position));
                    Intent projectIntent = new Intent(mContext, ProjectOverviewActivity.class);
                    projectIntent.putExtra("project_id", projects.keyAt(position));
                    startActivity(projectIntent);
                } 
            });
            tv_no_projects.setVisibility(View.GONE);
            lv_project_list.setVisibility(View.VISIBLE);
        }
    }

}
