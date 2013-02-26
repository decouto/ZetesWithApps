package com.appsolut.composition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    private TextView tv_project_description;
    private Button btn_record_audio;
    private Button btn_midi_view;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            finish();
        }
        project_id = bundle.getLong("project_id");
        Log.d("project", "loaded: " + project_id);
        projectModel = new ProjectModel(mContext, project_id);
        setContentView(R.layout.activity_project_overview);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(projectModel.getName());
        
        
        // import layout elements
        tv_project_name = (TextView) findViewById(R.id.tv_project_name);
        tv_project_description = (TextView) findViewById(R.id.tv_project_description);
        btn_midi_view = (Button) findViewById(R.id.btn_midi_view);
        btn_record_audio = (Button) findViewById(R.id.btn_record_audio);
        
        // set layout values
        tv_project_name.setText(projectModel.getName());
        tv_project_description.setText(projectModel.getDescription());
        
        // set listeners
        btn_midi_view.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(mContext, MIDIEditorActivity.class));
            }
        });
        
        btn_record_audio.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent projectRecordIntent = new Intent(mContext, ProjectRecordAudioActivity.class);
                projectRecordIntent.putExtra("project_id", project_id);
                startActivity(projectRecordIntent);
            }
        });
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
