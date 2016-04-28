package com.example.vipul.mytodolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TaskDetailActivity extends Activity  {
    public static final String EXTRA_ROW = "Extra";
    TextView taskName;
    TextView priority;
    ImageView image;
    TextView startsOn;
    TextView endsOn;
    TextView descriptionHeading;
    TextView startDate;
    TextView enddate;
    TextView desc;
    Cursor cursor;
    SQLiteDatabase db;
    private String taskname;
    private int row;
    private boolean setReminder;
    private int p;
    private boolean isRepeating;
    private String repeatDate;
    private String startTimeAndDate;
    private String endTimeAndDate;
    Date endDate;
    private String repeatInterval;
    private Bitmap bm=null;
    private String startDateText;
    private String description = null;
    private CalendarTask ctask;
    private int eventId;
    private ArrayList<CalendarTask.CalendarObject> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Button calendarAdd = (Button) findViewById(R.id.calendar_add);
        Button calendarDelete = (Button) findViewById(R.id.calendar_delete);
        row = (int) getIntent().getExtras().get(EXTRA_ROW);
        new getRowInfoTask().execute(row);


        ctask = new CalendarTask(getApplicationContext());
        list = new ArrayList<>();
        calendarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventId!=-1)
                    Toast.makeText(TaskDetailActivity.this,"Task already present in calendar",Toast.LENGTH_SHORT).show();
                else {
                    list = ctask.isCalendarAvailable();
                    if (list.size() == 0)
                        Toast.makeText(TaskDetailActivity.this, "Calendar not available", Toast.LENGTH_LONG).show();
                    else if (list.size() == 1) {
                        eventId = (int) ctask.addToCalendar(list.get(0).getId(), 0, taskname, endTimeAndDate, description, setReminder);
                        SQLiteOpenHelper helper = new TODOListDatabaseHelper(TaskDetailActivity.this);
                        db = helper.getWritableDatabase();
                        ContentValues val = new ContentValues();
                        val.put("CALENDAR_EVENT_ID", eventId);
                        db.update("NEWTASK6", val, "_id=?", new String[]{"" + row});
                        db.close();
                    } else {
                        final Dialog dialog = new Dialog(TaskDetailActivity.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.setTitle("Choose your Calendar");
                        final String items[] = new String[list.size() + 1];
                        int i = 0;
                        for (i = 1; i <= list.size(); i++) {
                            items[i - 1] = list.get(i - 1).getName();
                        }
                        items[i - 1] = "cancel";
                        ListView lv = (ListView) dialog.findViewById(R.id.listview1);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TaskDetailActivity.this, android.R.layout.simple_list_item_1, items);
                        lv.setAdapter(adapter);
                        dialog.show();
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (items[position].equals("cancel"))
                                    dialog.dismiss();
                                else {
                                    dialog.dismiss();
                                    eventId = (int) ctask.addToCalendar(list.get(position).getId(), position, taskname, endTimeAndDate, description, setReminder);
                                    SQLiteOpenHelper helper = new TODOListDatabaseHelper(TaskDetailActivity.this);
                                    db = helper.getWritableDatabase();
                                    ContentValues val = new ContentValues();
                                    val.put("CALENDAR_EVENT_ID", eventId);
                                    db.update("NEWTASK6", val, "_id=?", new String[]{"" + row});
                                    db.close();
                                }
                            }
                        });
                    }
                }
            }
        });


        calendarDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteOpenHelper helper = new TODOListDatabaseHelper(TaskDetailActivity.this);
                db = helper.getWritableDatabase();
                Cursor c = db.query("NEWTASK6",new String[]{"CALENDAR_EVENT_ID"},"_id=?",new String[]{""+row},null,null,null);
                if(c.moveToNext())
                    eventId = c.getInt(0);
                if(eventId!=-1) {
                    eventId = ctask.deleteFromCalendar(eventId);
                    ContentValues val = new ContentValues();
                    val.put("CALENDAR_EVENT_ID", eventId);
                    db.update("NEWTASK6", val, "_id=?", new String[]{"" + row});
                    Toast.makeText(TaskDetailActivity.this,"Task removed successfully",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(TaskDetailActivity.this,"Task not present in calendar",Toast.LENGTH_SHORT).show();
                c.close();
                db.close();
            }
        });
    }


    public void UpdateTextView(final TaskCountDown tcd){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                endsOn.setText(tcd.getEndHead());
                enddate.setText(tcd.getEndMsg());
                if(tcd.getEndHead().equals("Task started")){
                    tcd.cancel();
                    String name = tcd.getTaskName();
                    int priority = tcd.getPriority();
                    boolean rem = tcd.isReminderSet();
                    TimerService.taskCounters.remove(row);
                    TaskCountDown t = new TaskCountDown(endDate.getTime()-(new Date().getTime()),1000,getApplicationContext(),2,rem,name,priority,row,isRepeating);
                    TimerService.taskCounters.put(row,t);
                    t.start();
                    UpdateTextView(t);
                }
                else if(tcd.getEndHead().equals("Task ended")){
                    tcd.cancel();
                    TimerService.taskCounters.remove(row);
                }
                else
                    handler.postDelayed(this, 1000);
            }
        });
    }



    private class getRowInfoTask extends AsyncTask<Integer,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskName = (TextView)findViewById(R.id.task_name);
            //priority = (TextView)findViewById(R.id.text_priority);
            image = (ImageView)findViewById(R.id.image);
            startsOn = (TextView)findViewById(R.id.starts_on);
            endsOn = (TextView)findViewById(R.id.ends_on);
            descriptionHeading = (TextView)findViewById(R.id.description_heading);
            startDate = (TextView)findViewById(R.id.start_date);
            enddate = (TextView)findViewById(R.id.end_date);
            desc = (TextView)findViewById(R.id.description);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            SQLiteOpenHelper taskDatabaseHelper = new TODOListDatabaseHelper(TaskDetailActivity.this);
            db = taskDatabaseHelper.getReadableDatabase();
            cursor = db.query("NEWTASK6",new String[]{"TASK_NAME","PRIORITY","IMAGE","DESCRIPTION","START_DATE","END_DATE","START_TIME","END_TIME","REMINDER","REPEAT_TASK","REPEAT_AFTER","REPEAT_INTERVAL","CALENDAR_EVENT_ID"},"_id = ?",new String[]{Integer.toString(params[0])},null,null,null);
            try{
                if(cursor.moveToFirst()) {
                    taskname = cursor.getString(0);
                    setReminder = cursor.getInt(8) == 1;
                    p = cursor.getInt(1);
                    if (cursor.getBlob(2) != null) {
                        byte[] bytes = cursor.getBlob(2);
                        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                    isRepeating = cursor.getInt(9) == 1;
                    startDateText = cursor.getString(4);
                    if (isRepeating)
                        repeatDate = cursor.getString(10);
                    startTimeAndDate = cursor.getString(4) + " " + cursor.getString(6) + ":00";
                    endTimeAndDate = cursor.getString(5) + " " + cursor.getString(7) + ":00";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    Date startDate = new Date();
                    endDate = new Date();
                    try {
                        startDate = dateFormat.parse(startTimeAndDate);
                        endDate = dateFormat.parse(endTimeAndDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    description = cursor.getString(3);
                    repeatInterval = cursor.getString(11);
                    eventId = cursor.getInt(12);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            Typeface niconne = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Niconne-Regular.ttf");
            Typeface whitrabt = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/whitrabt.ttf");
            taskName.setTypeface(whitrabt);
            taskName.setText(taskname);
            LinearLayout textLayout = (LinearLayout)findViewById(R.id.text_layout);
            int color;
            if(p==1) {
                textLayout.setBackgroundColor(Color.rgb(1, 223, 1));
                color=Color.rgb(1, 223, 1);
            }
            else if(p==2) {
                textLayout.setBackgroundColor(Color.rgb(255, 255, 255));
                color=Color.rgb(255, 255, 255);
            }
            else {
                textLayout.setBackgroundColor(Color.rgb(254, 46, 46));
                color=Color.rgb(254, 46, 46);
            }
            startsOn.setTypeface(niconne);
            endsOn.setTypeface(niconne);
            startsOn.setTextColor(color);
            endsOn.setTextColor(color);
            if(bm!=null)
                image.setImageBitmap(bm);
            else
                image.setVisibility(View.INVISIBLE);
            startDate.setText(startDateText);
            if(TimerService.taskCounters.containsKey(row)) {
                TaskCountDown tcd = TimerService.taskCounters.get(row);
                UpdateTextView(tcd);
            }
            else{
                endsOn.setText("Task ended");
                enddate.setText("");
            }
            if (description == null) {
                descriptionHeading.setVisibility(View.INVISIBLE);
                desc.setVisibility(View.INVISIBLE);
            } else {
                descriptionHeading.setTypeface(niconne);
                descriptionHeading.setTextColor(color);
                descriptionHeading.setText("Description");
                desc.setText(description);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_leave, R.anim.anim_enter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
