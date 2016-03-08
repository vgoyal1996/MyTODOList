package com.example.vipul.mytodolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SearchResultGridViewActivity extends Activity {
    private ArrayList<MyObject> names;
    private String[] ids;
    private int i=0;
    private SQLiteDatabase db;
    private GridView searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_grid_view);
        names =  getIntent().getExtras().getParcelableArrayList("ArrayList2");
        ids = new String[names.size()];
        for(MyObject o:names){
            ids[i] = Integer.toString(o.getId());
            i++;
        }
        searchList = (GridView)findViewById(R.id.task_grid_search);
        new UpdateTaskListTask().execute();

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultGridViewActivity.this, TaskDetailActivity.class);
                //int c = taskList.getAdapter().getCount();
                int c = names.get(position).getId();
                intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //Toast.makeText(MainActivity.this,"tttt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UpdateTaskListTask extends AsyncTask<Void,Void,Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            SQLiteOpenHelper taskDatabaseHelper = new TODOListDatabaseHelper(SearchResultGridViewActivity.this);
            db = taskDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("NEWTASK4", new String[]{"_id", "TASK_NAME", "PRIORITY","MODIFIED_DATE","START_DATE","END_DATE","IMAGE","START_TIME","END_TIME"}, null, null, null, null, "_id DESC");
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            ArrayList<MyObject> taskArray = new ArrayList<MyObject>();
            Date cur = new Date();
            i=0;
            while(cursor.moveToNext()){
                if(Integer.parseInt(ids[i])==cursor.getInt(0)) {
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");
                    Date start = null, end = null;
                    try {
                        start = f.parse(cursor.getString(4) + " " + cursor.getString(7));
                        end = f.parse(cursor.getString(5) + " " + cursor.getString(8));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    byte[] image = cursor.getBlob(6);
                    taskArray.add(new MyObject(cursor.getInt(0), cursor.getInt(2), cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5), image));
                    i++;
                    if(i==ids.length)
                        break;
                }
            }
            GridAdapter taskAdapter = new GridAdapter(SearchResultGridViewActivity.this,R.layout.listview_grid_layout,taskArray);
            searchList.setAdapter(taskAdapter);
            cursor.close();
            db.close();
        }
    }

}
