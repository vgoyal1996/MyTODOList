package com.dudeonfireandCO.vipul.mytodolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.vipul.mytodolist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends Activity {

    private Button button;
    private SQLiteDatabase db;
    //private Cursor cursor;
    private ListView taskListRunning;
    private ListView taskListCompleted;
    private ListView taskListUpcoming;
    private ArrayList<MyObject> taskarrayRunning;
    private ArrayList<MyObject> taskArrayCompleted;
    private ArrayList<MyObject> taskArrayUpcoming;
    private ArrayList<MyObject> taskArray;
    private TaskAdapter taskAdapterRunning;
    private TaskAdapter taskAdapterCompleted;
    private TaskAdapter taskAdapterUpcoming;
    private TaskAdapter taskAdapter;
    private TabHost tabHost;
    private GestureDetector gestureDetector;
    private ArrayList<MyObject> nameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Upcoming List");
        tabSpec.setContent(R.id.upcoming_list);
        tabSpec.setIndicator("Upcoming");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Running List");
        tabSpec.setContent(R.id.running_list);
        tabSpec.setIndicator("Running");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Completed List");
        tabSpec.setContent(R.id.completed_list);
        tabSpec.setIndicator("Completed");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(1);

        //final View mainView = findViewById(android.R.id.content);

        //this.gestureDetector = new GestureDetectorCompat(this,new SwipeGestureDetector());
        //gestureDetector.setOnDoubleTapListener(new SwipeGestureDetector(this));
        /*final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);*/
        gestureDetector = new GestureDetector(this,new MyGestureDetector());

        tabHost.setOnTabChangedListener(new AnimatedTabHostListener(getApplicationContext(),tabHost));

        taskListRunning = (ListView)findViewById(R.id.task_listview_running);
        taskListCompleted = (ListView)findViewById(R.id.task_listview_completed);
        taskListUpcoming = (ListView)findViewById(R.id.task_listview_upcoming);
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

        button = (Button)findViewById(R.id.task_button2);
        if(savedInstanceState!=null){
            taskarrayRunning = savedInstanceState.getParcelableArrayList("arraylistrunning");
            taskArrayCompleted = savedInstanceState.getParcelableArrayList("arraylistcompleted");
            taskArrayUpcoming = savedInstanceState.getParcelableArrayList("arraylistupcoming");
            taskArray = savedInstanceState.getParcelableArrayList("arraylist");
            taskAdapter = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArray);
            taskAdapterRunning = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskarrayRunning);
            taskAdapterCompleted = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArrayCompleted);
            taskAdapterUpcoming = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArrayUpcoming);
            taskListRunning.setAdapter(taskAdapterRunning);
            taskListCompleted.setAdapter(taskAdapterCompleted);
            taskListUpcoming.setAdapter(taskAdapterUpcoming);
            tabHost.setCurrentTab(savedInstanceState.getInt("selectedtab"));
        }
        else
            new UpdateTaskListTask().execute();

        taskListRunning.setOnItemClickListener(new TaskRunningClickListener());
        taskListUpcoming.setOnItemClickListener(new TaskUpcomingClickListener());
        taskListCompleted.setOnItemClickListener(new TaskCompletedClickListener());

        taskListRunning.setOnItemLongClickListener(new TaskRunningLongClickListener());
        taskListCompleted.setOnItemLongClickListener(new TaskCompletedLongClickListener());
        taskListUpcoming.setOnItemLongClickListener(new TaskUpcomingLongClickListener());

        SwipeRefreshLayout mySwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_running);
        mySwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionRefresh();
            }
        });

        SwipeRefreshLayout mySwipeLayout1 = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_upcoming);
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

        final EditText searchText = (EditText)findViewById(R.id.search_text);
        final ImageButton searchButton = (ImageButton)findViewById(R.id.search_button);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameList.size() == 0)
                    Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(MainActivity.this, SearchResultListviewActivity.class);
                    intent.putParcelableArrayListExtra("ArrayList", nameList);
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

    class TaskRunningClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            //int c = taskList.getAdapter().getCount();
            int c = taskarrayRunning.get(position).getId();
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            //intent.putExtra(TaskDetailActivity.EXTRA_ARRAYLIST,taskArray);
            //intent.putExtra(TaskDetailActivity.EXTRA_OBJECT,taskArray.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            //Toast.makeText(MainActivity.this,"tttt", Toast.LENGTH_SHORT).show();
        }
    }

    class TaskUpcomingClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            //int c = taskList.getAdapter().getCount();
            int c = taskArrayUpcoming.get(position).getId();
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            //intent.putExtra(TaskDetailActivity.EXTRA_ARRAYLIST,taskArray);
            //intent.putExtra(TaskDetailActivity.EXTRA_OBJECT,taskArray.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            //Toast.makeText(MainActivity.this,"tttt", Toast.LENGTH_SHORT).show();
        }
    }

    class TaskCompletedClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            //int c = taskList.getAdapter().getCount();
            int c = taskArrayCompleted.get(position).getId();
            //intent.putExtra(TaskDetailActivity.EXTRA_OBJECT,taskArray.get(position));
            intent.putExtra(TaskDetailActivity.EXTRA_ROW, c);
            ///intent.putExtra(TaskDetailActivity.EXTRA_ARRAYLIST,taskArray);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            //Toast.makeText(MainActivity.this,"tttt", Toast.LENGTH_SHORT).show();
        }
    }

    class TaskRunningLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
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
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(MainActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = database.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if (TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(MainActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
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
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            final int p = position;
            final int rowId = taskArrayUpcoming.get(position).getId();
            dialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (items[position].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (items[position].equals("Delete task")) {
                        taskArrayUpcoming.remove(p);
                        taskAdapterUpcoming.notifyDataSetChanged();
                        try {
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(MainActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = database.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if (TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(MainActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
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
                        i.putExtra(TimerService.EXTRA_SERVICE_REPEATING, o.isrepeating());
                        startService(i);
                    }
                }
            }
        }
    }

    class TaskCompletedLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setTitle("Select your choice");
            final String[] items = {"Edit task", "Delete task", "Cancel"};
            ListView lv = (ListView) dialog.findViewById(R.id.listview1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            final int p = position;
            final int rowId = taskArrayCompleted.get(position).getId();
            dialog.show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (items[position].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (items[position].equals("Delete task")) {
                        taskArrayCompleted.remove(p);
                        taskAdapterCompleted.notifyDataSetChanged();
                        try {
                            SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(MainActivity.this);
                            SQLiteDatabase database = taskHelper.getWritableDatabase();
                            Cursor c = database.query("NEWTASK6", new String[]{"CALENDAR_EVENT_ID"}, "_id=?", new String[]{"" + rowId}, null, null, null);
                            int eventId = 0;
                            if(c.moveToNext())
                                eventId = c.getInt(0);
                            new CalendarTask(getApplicationContext()).deleteFromCalendar(eventId);
                            database.delete("NEWTASK6", "_id = ?", new String[]{Integer.toString(rowId)});
                            if (TimerService.taskCounters.containsKey(rowId))
                                TimerService.taskCounters.remove(rowId);
                            c.close();
                            database.close();
                            Toast.makeText(MainActivity.this, "Task successfully deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "database unavailable", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (items[position].equals("Edit task")) {
                        Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
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

    private class UpdateTaskListTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SQLiteOpenHelper taskDatabaseHelper = new TODOListDatabaseHelper(MainActivity.this);
            db = taskDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("NEWTASK6", new String[]{"_id", "TASK_NAME", "PRIORITY","MODIFIED_DATE","START_DATE","END_DATE","START_TIME","END_TIME","IMAGE","REPEAT_TASK","REPEAT_AFTER","REMINDER","DESCRIPTION","REPEAT_INTERVAL","CALENDAR_EVENT_ID"}, null, null, null, null, "_id DESC");
            taskarrayRunning = new ArrayList<MyObject>();
            taskArrayUpcoming = new ArrayList<MyObject>();
            taskArrayCompleted = new ArrayList<MyObject>();
            taskArray = new ArrayList<MyObject>();
            Date cur = new Date();
            while(cursor.moveToNext()){
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");
                Date start=null,end=null;
                Log.v("MainActivity",cursor.getString(5)+" "+cursor.getString(7));
                try {
                    start = f.parse(cursor.getString(4)+" "+cursor.getString(6));
                    end = f.parse(cursor.getString(5)+" "+cursor.getString(7));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                taskArray.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(9),cursor.getString(10),cursor.getString(12),cursor.getBlob(8),cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                if(cur.compareTo(start)<0)
                    taskArrayUpcoming.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(9),cursor.getString(10),cursor.getString(12),cursor.getBlob(8),cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                else if(cur.compareTo(end)>0)
                    taskArrayCompleted.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(9),cursor.getString(10),cursor.getString(12),cursor.getBlob(8),cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
                else
                    taskarrayRunning.add(new MyObject(cursor.getInt(0),cursor.getInt(2),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(9),cursor.getString(10),cursor.getString(12),cursor.getBlob(8),cursor.getInt(11),cursor.getString(13),cursor.getInt(14)));
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            taskAdapter = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArray);
            taskAdapterRunning = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskarrayRunning);
            taskAdapterCompleted = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArrayCompleted);
            taskAdapterUpcoming = new TaskAdapter(MainActivity.this,R.layout.listview_layout,taskArrayUpcoming);
            taskListRunning.setAdapter(taskAdapterRunning);
            taskListCompleted.setAdapter(taskAdapterCompleted);
            taskListUpcoming.setAdapter(taskAdapterUpcoming);
            db.close();
            UpdateTimers();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void nextActivity(View v){
        Intent intent = new Intent(this,NewTaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }



    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Grid view")){
            startActivity(new Intent(this,GridViewActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void actionRefresh(){
        Intent intent = getIntent();
        onSaveInstanceState(new Bundle());
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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
                    Collections.sort(taskArrayCompleted, new PriorityCompare());
                    Collections.sort(taskArrayUpcoming, new PriorityCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (modifiedsort.isChecked()) {
                    Collections.sort(taskarrayRunning, new DateCompare());
                    Collections.sort(taskArrayCompleted, new DateCompare());
                    Collections.sort(taskArrayUpcoming, new DateCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (titlesort.isChecked()) {
                    Collections.sort(taskarrayRunning, new TitleCompare());
                    Collections.sort(taskArrayCompleted, new TitleCompare());
                    Collections.sort(taskArrayUpcoming, new TitleCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                } else if (createsort.isChecked()) {
                    Collections.sort(taskarrayRunning, new IdCompare());
                    Collections.sort(taskArrayCompleted, new IdCompare());
                    Collections.sort(taskArrayUpcoming, new IdCompare());
                    taskAdapterRunning.notifyDataSetChanged();
                    taskAdapterCompleted.notifyDataSetChanged();
                    taskAdapterUpcoming.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("arraylistrunning", taskarrayRunning);
        outState.putParcelableArrayList("arraylistcompleted",taskArrayCompleted);
        outState.putParcelableArrayList("arraylistupcoming",taskArrayUpcoming);
        outState.putParcelableArrayList("arraylist",taskArray);
        outState.putInt("selectedtab",tabHost.getCurrentTab());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                return true;
            case R.id.action_sort:
                actionSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
