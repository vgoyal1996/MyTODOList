package com.example.vipul.mytodolist;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
    private int isRepeating;
    private String repeatDate;
    private String startTimeAndDate;
    private String endTimeAndDate;
    Date endDate;
    private Bitmap bm=null;
    private String startDateText;
    private String description = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        row = (int) getIntent().getExtras().get(EXTRA_ROW);
        new getRowInfoTask().execute(row);
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
                    TaskCountDown t = new TaskCountDown(endDate.getTime()-(new Date().getTime()),1000,getApplicationContext(),2,rem,name,priority,row);
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
            cursor = db.query("NEWTASK4",new String[]{"TASK_NAME","PRIORITY","IMAGE","DESCRIPTION","START_DATE","END_DATE","START_TIME","END_TIME","REMINDER","REPEAT_TASK","REPEAT_AFTER"},"_id = ?",new String[]{Integer.toString(params[0])},null,null,null);
            try{
                if(cursor.moveToFirst()) {
                    taskname = cursor.getString(0);
                    setReminder = cursor.getInt(8) == 1;
                    p = cursor.getInt(1);
                    if (cursor.getBlob(2) != null) {
                        byte[] bytes = cursor.getBlob(2);
                        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                    isRepeating = cursor.getInt(9);
                    startDateText = cursor.getString(4);
                    if (isRepeating == 1)
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
