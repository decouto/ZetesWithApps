package com.appsolut.composition;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appsolut.composition.utils.ProjectModel;

public class ProjectOverviewActivity extends SherlockActivity {
    
    private Context mContext;
    private long project_id;
    private ProjectModel projectModel;
    private boolean is_editable;
    
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
            toggleEdit();
        }
        return true;
    }
    
    private void toggleEdit() {
        if (is_editable) {
            et_project_description.setEnabled(false);
            et_project_name.setEnabled(false);
        } else {
            et_project_description.setEnabled(true);
            et_project_name.setEnabled(true);
        }
        is_editable = !is_editable;
    }

}
