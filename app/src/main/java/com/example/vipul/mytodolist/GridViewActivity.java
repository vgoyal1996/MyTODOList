package com.example.vipul.mytodolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class GridViewActivity extends Activity {
    private Button button;
    private SQLiteDatabase db;
    //private Cursor cursor;
    private GridView taskListRunning;
    private GridView taskListCompleted;
    private GridView taskListUpcoming;
    private List<MyObject> taskarrayRunning;
    private List<MyObject> taskarrayCompleted;
    private List<MyObject> taskarrayUpcoming;
    private List<MyObject> taskArray;
    private GridAdapter taskAdapter;
    private GridAdapter taskAdapterRunning;
    private GridAdapter taskAdapterCompleted;
    private GridAdapter taskAdapterUpcoming;
    private TabHost tabHost;
    private GestureDetector gestureDetector;
    private ArrayList<MyObject> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        getActionBar().setDisplayShowHomeEnabled(true);

        tabHost = (TabHost)findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Upcoming List");
        tabSpec.setContent(R.id.upcoming_grid);
        tabSpec.setIndicator("Upcoming");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Running List");
        tabSpec.setContent(R.id.running_grid);
        tabSpec.setIndicator("Running");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Completed List");
        tabSpec.setContent(R.id.completed_grid);
        tabSpec.setIndicator("Completed");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(1);

        tabHost.setOnTabChangedListener(new AnimatedTabHostListener(getApplicationContext(), tabHost));

        taskListRunning = (GridView)findViewById(R.id.task_grid_running);
        taskListCompleted = (GridView)findViewById(R.id.task_grid_completed);
        taskListUpcoming = (GridView)findViewById(R.id.task_grid_upcoming);

        gestureDetector = new GestureDetector(this,new MyGestureDetector());


        taskListRunning.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        taskListUpcoming.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        taskListCompleted.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        if(savedInstanceState!=null){
            taskarrayRunning = savedInstanceState.getParcelableArrayList("arraylistrunning");
            taskarrayCompleted = savedInstanceState.getParcelableArrayList("arraylistcompleted");
            taskarrayUpcoming = savedInstanceState.getParcelableArrayList("arraylistupcoming");
            taskArray = savedInstanceState.getParcelableArrayList("arraylist");
            taskAdapter = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskArray);
            taskAdapterRunning = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayRunning);
            taskAdapterCompleted = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayCompleted);
            taskAdapterUpcoming = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayUpcoming);
            taskListRunning.setAdapter(taskAdapterRunning);
            taskListCompleted.setAdapter(taskAdapterCompleted);
            taskListUpcoming.setAdapter(taskAdapterUpcoming);
            tabHost.setCurrentTab(savedInstanceState.getInt("selectedtab"));
        }
        else
            new UpdateTaskListTask().execute();

        //new UpdateTaskListTask().execute();

        SwipeRefreshLayout mySwipeLayout1 = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_running);
        mySwipeLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionRefresh();
            }
        });

        SwipeRefreshLayout mySwipeLayout2 = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_completed);
        mySwipeLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionRefresh();
            }
        });

        SwipeRefreshLayout mySwipeLayout3 = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_upcoming);
        mySwipeLayout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionRefresh();
            }
        });

        taskListUpcoming.setOnItemClickListener(new TaskUpcomingClickListener());
        taskListCompleted.setOnItemClickListener(new TaskCompletedClickListener());
        taskListRunning.setOnItemClickListener(new TaskRunningClickListener());

        taskListUpcoming.setOnItemLongClickListener(new TaskUpcomingLongClickListener());
        taskListCompleted.setOnItemLongClickListener(new TaskCompletedLongClickListener());
        taskListRunning.setOnItemLongClickListener(new TaskRunningLongClickListener());

        final EditText searchText = (EditText)findViewById(R.id.search_text_grid);
        final ImageButton searchButton = (ImageButton)findViewById(R.id.search_button_grid);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameList.size() == 0)
                    Toast.makeText(GridViewActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(GridViewActivity.this, SearchResultGridViewActivity.class);
                    intent.putParcelableArrayListExtra("ArrayList2", nameList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });



        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchButton.setEnabled(true);
                nameList = new ArrayList<MyObject>();
                String text = searchText.getText().toString().toLowerCase(Locale.getDefault());
                if(text.equals(""))
                    searchButton.setEnabled(false);
                else
                    nameList = taskAdapter.filter(text, nameList);
            }
        });
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener{
        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_OFF_PATH = 80;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;
        private int maxTabs;

        /**
         * An empty constructor that uses the tabhosts content view to decide how many tabs there are.
         */
        public MyGestureDetector(){
            maxTabs = tabHost.getTabContentView().getChildCount();
        }

        /**
         * Listens for the onFling event and performs some calculations between the touch down point and the touch up
         * point. It then uses that information to calculate if the swipe was long enough. It also uses the swiping
         * velocity to decide if it was a "true" swipe or just some random touching.
         */
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            int newTab = 0;
            if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                // Swipe right to left
                newTab = tabHost.getCurrentTab() + 1;
            }
            else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                // Swipe left to right
                newTab = tabHost.getCurrentTab() - 1;
            }
            if (newTab < 0 || newTab > (maxTabs - 1)){
                return false;
            }
            tabHost.setCurrentTab(newTab);
            return super.onFling(event1, event2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class TaskRunningClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(GridViewActivity.this, TaskDetailActivity.class);
            int c = taskarrayRunning.get(position).getId();
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    class TaskCompletedClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(GridViewActivity.this, TaskDetailActivity.class);
            int c = taskarrayCompleted.get(position).getId();
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    class TaskUpcomingClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(GridViewActivity.this, TaskDetailActivity.class);
            int c = taskarrayUpcoming.get(position).getId();
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    class TaskRunningLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(GridViewActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GridViewActivity.this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            final int p = position;
            final int rowId = taskarrayRunning.get(position).getId();
            dialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (items[position].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (items[position].equals("Delete task")) {
                        taskarrayRunning.remove(p);
                        taskAdapterRunning.notifyDataSetChanged();
                        try {
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(GridViewActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = db.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if(TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(GridViewActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                            //actionRefresh();
                        } catch (Exception e) {
                            Toast.makeText(GridViewActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(GridViewActivity.this, TaskEditActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(TaskEditActivity.EXTRA_ROW, rowId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            });
            return true;
        }
    }

    class TaskCompletedLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(GridViewActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GridViewActivity.this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            final int p = position;
            final int rowId = taskarrayCompleted.get(position).getId();
            dialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (items[position].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (items[position].equals("Delete task")) {
                        taskarrayCompleted.remove(p);
                        taskAdapterCompleted.notifyDataSetChanged();
                        try {
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(GridViewActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = db.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if(TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(GridViewActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                            //actionRefresh();
                        } catch (Exception e) {
                            Toast.makeText(GridViewActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(GridViewActivity.this, TaskEditActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(TaskEditActivity.EXTRA_ROW, rowId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            });
            return true;
        }
    }

    class TaskUpcomingLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(GridViewActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(GridViewActivity.this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            final int p = position;
            final int rowId = taskarrayUpcoming.get(position).getId();
            dialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (items[position].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (items[position].equals("Delete task")) {
                        taskarrayUpcoming.remove(p);
                        taskAdapterUpcoming.notifyDataSetChanged();
                        try {
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(GridViewActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = db.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if(TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(GridViewActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                            //actionRefresh();
                        } catch (Exception e) {
                            Toast.makeText(GridViewActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(GridViewActivity.this, TaskEditActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(TaskEditActivity.EXTRA_ROW, rowId);
                        startActivity(intent);
                    }
                }
            });
            return true;
        }
    }

    private class UpdateTaskListTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SQLiteOpenHelper taskDatabaseHelper = new TODOListDatabaseHelper(GridViewActivity.this);
            db = taskDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("NEWTASK6", new String[]{"_id", "TASK_NAME", "PRIORITY","MODIFIED_DATE","START_DATE","END_DATE","IMAGE","START_TIME","END_TIME","REPEAT_TASK","REPEAT_AFTER","REMINDER","DESCRIPTION","REPEAT_INTERVAL","CALENDAR_EVENT_ID"}, null, null, null, null, "_id DESC");
            taskarrayRunning = new ArrayList<MyObject>();
            taskarrayCompleted = new ArrayList<MyObject>();
            taskarrayUpcoming = new ArrayList<MyObject>();
            taskArray = new ArrayList<MyObject>();
            Date cur = new Date();
            while(cursor.moveToNext()){
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date start=null,end=null;
                try {
                    start = f.parse(cursor.getString(4)+" "+cursor.getString(7)+":00");
                    end = f.parse(cursor.getString(5)+" "+cursor.getString(8)+":00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                byte[] image = cursor.getBlob(6);
                taskArray.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getString(10),cursor.getString(12),image,cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                if(cur.compareTo(start)<0)
                    taskarrayUpcoming.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getString(10),cursor.getString(12),image,cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                else if(cur.compareTo(end)>0)
                    taskarrayCompleted.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getString(10),cursor.getString(12),image,cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                else
                    taskarrayRunning.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getString(10),cursor.getString(12),image,cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            taskAdapter = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskArray);
            taskAdapterRunning = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayRunning);
            taskAdapterCompleted = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayCompleted);
            taskAdapterUpcoming = new GridAdapter(GridViewActivity.this,R.layout.listview_grid_layout,taskarrayUpcoming);
            taskListRunning.setAdapter(taskAdapterRunning);
            taskListCompleted.setAdapter(taskAdapterCompleted);
            taskListUpcoming.setAdapter(taskAdapterUpcoming);
            db.close();
            UpdateTimers();
        }
    }

    public void UpdateTimers(){
        if(TimerService.taskCounters==null)
            TimerService.taskCounters = new HashMap<>();
        if (TimerService.taskCounters.size() == 0) {
            if (taskArray.size() != 0) {
                for (MyObject o : taskArray) {
                    int row = o.getId();
                    boolean isReminderSet = o.isSetReminder();
                    int priority = o.getPrior();
                    String taskName = o.getTask();
                    long interval = 1000;
                    Date startDateAndTime = o.getStartDateAndTime();
                    Date endDateAndTime = o.getEndDateAndTime();
                    if((endDateAndTime.getTime() - (new Date().getTime()))>0) {
                        Intent i = new Intent(this, TimerService.class);
                        //TaskCountDown tcd = null;
                        if ((startDateAndTime.getTime() - (new Date().getTime())) > 0) {
                            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS, (startDateAndTime.getTime() - (new Date().getTime())));
                            i.putExtra(TimerService.EXTRA_SERVICE_FLAG, 1);
                            //tcd = new TaskCountDown((startDateAndTime.getTime()-(new Date().getTime())),1000,1,isReminderSet,taskName,priority,r);
                        } else if ((startDateAndTime.getTime() - (new Date().getTime()) <= 0)) {
                            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS, endDateAndTime.getTime() - (new Date().getTime()));
                            i.putExtra(TimerService.EXTRA_SERVICE_FLAG, 2);
                            //tcd = new TaskCountDown(finishTimeAndDate.getTime()-(new Date().getTime()),1000,2,isReminderSet,taskName,priority,r);
                        }
                        //tcd.start();
                        i.putExtra(TimerService.EXTRA_SERVICE_ROW, row);
                        //i.putExtra(TimerService.EXTRA_SERVICE_COUNTDOWN,tcd);
                        i.putExtra(TimerService.EXTRA_SERVICE_COUNTDOWN_INTERVAL, interval);
                        i.putExtra(TimerService.EXTRA_SERVICE_PRIORITY, priority);
                        i.putExtra(TimerService.EXTRA_SERVICE_REMINDER, isReminderSet);
                        i.putExtra(TimerService.EXTRA_SERVICE_TASKNAME, taskName);
                        startService(i);
                    }
                }
            }
        }
    }

    public void actionRefresh(){
        Intent intent = getIntent();
        onSaveInstanceState(new Bundle());
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void nextActivity(View v){
        Intent intent = new Intent(this,NewTaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.pref_key_view),"Condensed Listview").equals("Condensed Listview")){
            startActivity(new Intent(GridViewActivity.this,MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("arraylistrunning", (ArrayList<? extends Parcelable>) taskarrayRunning);
        outState.putParcelableArrayList("arraylistcompleted", (ArrayList<? extends Parcelable>) taskarrayCompleted);
        outState.putParcelableArrayList("arraylistupcoming", (ArrayList<? extends Parcelable>) taskarrayUpcoming);
        outState.putParcelableArrayList("arraylist", (ArrayList<? extends Parcelable>) taskArray);
        outState.putInt("selectedtab", tabHost.getCurrentTab());
    }

    public void actionSort(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sort_layout);
        dialog.setTitle("Select your choice");
        RadioGroup sortGroup = (RadioGroup)dialog.findViewById(R.id.sort_group);
        final RadioButton prioritysort = (RadioButton)dialog.findViewById(R.id.priority_sort);
        final RadioButton modifiedsort = (RadioButton)dialog.findViewById(R.id.modified_sort);
        final RadioButton titlesort = (RadioButton)dialog.findViewById(R.id.title_sort);
        final RadioButton createsort = (RadioButton)dialog.findViewById(R.id.created_sort);
        dialog.show();
        Button cancel = (Button)dialog.findViewById(R.id.cancel_button);
        Button sort = (Button)dialog.findViewById(R.id.sort_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prioritysort.isChecked()) {
                    Collections.sort(taskarrayRunning, new PriorityCompare());
                    Collections.sort(taskarrayCompleted, new PriorityCompare());
                    Collections.sort(taskarrayUpcoming, new PriorityCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (modifiedsort.isChecked()) {
                    Collections.sort(taskarrayRunning, new DateCompare());
                    Collections.sort(taskarrayCompleted, new DateCompare());
                    Collections.sort(taskarrayUpcoming, new DateCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (titlesort.isChecked()) {
                    Collections.sort(taskarrayRunning, new TitleCompare());
                    Collections.sort(taskarrayCompleted, new TitleCompare());
                    Collections.sort(taskarrayUpcoming, new TitleCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (createsort.isChecked()) {
                    Collections.sort(taskarrayRunning, new IdCompare());
                    Collections.sort(taskarrayCompleted, new IdCompare());
                    Collections.sort(taskarrayUpcoming, new IdCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(GridViewActivity.this,SettingsActivity.class));
                return true;
            case R.id.action_sort:
                actionSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
