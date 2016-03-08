package com.example.vipul.mytodolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class SearchResultListviewActivity extends Activity {
    private ArrayList<MyObject> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        names = getIntent().getExtras().getParcelableArrayList("ArrayList");
        ListView searchList = (ListView)findViewById(R.id.search_list);
        TaskAdapter adapter = new TaskAdapter(this,R.layout.listview_layout,names);
        searchList.setAdapter(adapter);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultListviewActivity.this, TaskDetailActivity.class);
                //int c = taskList.getAdapter().getCount();
                int c = names.get(position).getId();
                intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //Toast.makeText(MainActivity.this,"tttt", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
