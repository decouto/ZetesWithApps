package com.appsolut.composition;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.appsolut.composition.utils.ProjectModel;

public class ProjectOverviewActivity extends SherlockActivity {
    
    private Context mContext;
    private long project_id;
    private ProjectModel projectModel;
    
    // layout elements
    private TextView tv_project_name;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_overview);
        getSupportActionBar().hide();
        mContext = this;
        
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            finish();
        }
        project_id = bundle.getLong("project_id");
        projectModel = new ProjectModel(mContext, project_id);
        
        
        // import layout elements
        tv_project_name = (TextView) findViewById(R.id.tv_project_name);
        
        // set layout values
        tv_project_name.setText(projectModel.getName());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Skip landing page item
        menu.add("Share");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        SubMenu sub = menu.addSubMenu("Edit");
        sub.add("Edit Title");
        sub.add("Edit Description");
        sub.add("Delete Project");
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
        else if (title.equals("Edit Title")) {
            // TODO
        }
        else if (title.equals("Edit Description")) {
            // TODO
        }
        else if (title.equals("Delete Project")) {
            // TODO
        }
        return true;
    }

}
