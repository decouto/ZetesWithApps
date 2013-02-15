package com.appsolut.composition;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appsolut.composition.utils.ProjectModel;

public class ProjectOverviewActivity extends SherlockActivity {
    
    private Context mContext;
    private int project_id;
    private ProjectModel projectModel;
    
    // layout elements
    private TextView tv_project_name;
    private TextView tv_project_description;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            finish();
        }
        project_id = bundle.getInt("project_id");
        projectModel = new ProjectModel(mContext, project_id);
        setContentView(R.layout.activity_project_overview);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(projectModel.getName());
        
        
        // import layout elements
        tv_project_name = (TextView) findViewById(R.id.tv_project_name);
        tv_project_description = (TextView) findViewById(R.id.tv_project_description);
        
        // set layout values
        tv_project_name.setText(projectModel.getName());
        tv_project_description.setText("Description lorem ipsum blah blah");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Skip landing page item
        menu.add("Share");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Share")) {
            // Launch Share/Export menu
            Toast.makeText(mContext, "Open share/export menu", Toast.LENGTH_LONG).show();
            // TODO
        }
        return true;
    }

}
