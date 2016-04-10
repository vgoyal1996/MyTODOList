package com.example.vipul.mytodolist;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CompleteAndRestartTaskService extends IntentService {
    public static final String SERVICE_EXTRA_ROW = "row";
    private int row;


    public CompleteAndRestartTaskService() {
        super("CompleteAndRestartTaskService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        row = (int)intent.getExtras().get(SERVICE_EXTRA_ROW);
        NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        m.cancel(TaskNotificationService.NOTIFICATION_ID);
        SQLiteOpenHelper taskDatabaseHelper = new TODOListDatabaseHelper(this);
        SQLiteDatabase db = taskDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query("NEWTASK6",new String[]{"REPEAT_TASK","REPEAT_AFTER","START_DATE","START_TIME","END_DATE","END_TIME","MODIFIED_DATE","TASK_NAME","PRIORITY","CALENDAR_EVENT_ID","DESCRIPTION"},"_id=?",new String[]{String.valueOf(row)},null,null,null);
        if(cursor.moveToNext()){
            if(cursor.getInt(0)==1){
                Log.v("CompleteService",cursor.getString(7));
                String startDate = cursor.getString(2)+" "+cursor.getString(3)+":00";
                String endDate = cursor.getString(4)+" "+cursor.getString(5)+":00";
                String repDate = cursor.getString(1);
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Calendar start = Calendar.getInstance(),end=Calendar.getInstance(),repeatDate=Calendar.getInstance();
                try{
                    start.setTime(f.parse(startDate));
                    repeatDate.setTime(f.parse(repDate));
                    end.setTime(f.parse(endDate));
                }catch (Exception e){
                    e.printStackTrace();
                }
                ContentValues taskContents = new ContentValues();

                int year = end.get(Calendar.YEAR)-start.get(Calendar.YEAR);
                int month = end.get(Calendar.MONTH)-start.get(Calendar.MONTH);
                if(month<0)
                    month=12+month;
                int seconds = (end.get(Calendar.SECOND)-start.get(Calendar.SECOND));
                if(seconds<0)
                    seconds=60+seconds;
                int minutes = end.get(Calendar.MINUTE)-start.get(Calendar.MINUTE);
                int hour = end.get(Calendar.HOUR_OF_DAY)-start.get(Calendar.HOUR_OF_DAY);
                if(hour<0)
                    hour=24+hour;

                int day = end.get(Calendar.DAY_OF_MONTH)-start.get(Calendar.DAY_OF_MONTH);
                if(day<0){
                    day = start.getActualMaximum(Calendar.DAY_OF_MONTH)-start.get(Calendar.DAY_OF_MONTH);
                    day+=end.get(Calendar.DAY_OF_MONTH);
                }

                start.set(repeatDate.get(Calendar.YEAR),repeatDate.get(Calendar.MONTH),repeatDate.get(Calendar.DAY_OF_MONTH),repeatDate.get(Calendar.HOUR_OF_DAY),repeatDate.get(Calendar.MINUTE),repeatDate.get(Calendar.SECOND));
                int temp = start.get(Calendar.MONTH)+1;
                String date = start.get(Calendar.DAY_OF_MONTH)+"/"+temp+"/"+start.get(Calendar.YEAR);
                String time = start.get(Calendar.HOUR_OF_DAY)+":"+start.get(Calendar.MINUTE)+":"+start.get(Calendar.SECOND);
                taskContents.put("START_DATE", date);
                taskContents.put("START_TIME", time);
                if(TimerService.taskCounters.containsKey(row))
                    TimerService.taskCounters.remove(row);

                start.add(Calendar.YEAR, year);
                start.add(Calendar.MONTH, month);
                start.add(Calendar.DAY_OF_MONTH, day);
                start.add(Calendar.HOUR_OF_DAY, hour);
                start.add(Calendar.MINUTE, minutes);
                start.add(Calendar.SECOND, seconds);
                temp = start.get(Calendar.MONTH)+1;
                date = start.get(Calendar.DAY_OF_MONTH)+"/"+temp+"/"+start.get(Calendar.YEAR);
                time = start.get(Calendar.HOUR_OF_DAY)+":"+start.get(Calendar.MINUTE)+":"+start.get(Calendar.SECOND);
                taskContents.put("END_DATE", date);
                taskContents.put("END_TIME", time);
                String endDateAndTime = date+" "+time;

                Intent i = new Intent(this,TimerService.class);
                i.putExtra(TimerService.EXTRA_SERVICE_SECONDS, start.getTimeInMillis()-(Calendar.getInstance().getTimeInMillis()));
                i.putExtra(TimerService.EXTRA_SERVICE_FLAG, 1);
                i.putExtra(TimerService.EXTRA_SERVICE_ROW, row);
                i.putExtra(TimerService.EXTRA_SERVICE_COUNTDOWN_INTERVAL,(long)1000);
                i.putExtra(TimerService.EXTRA_SERVICE_PRIORITY,cursor.getInt(8));
                i.putExtra(TimerService.EXTRA_SERVICE_REMINDER,true);
                i.putExtra(TimerService.EXTRA_SERVICE_TASKNAME, cursor.getString(7));
                startService(i);

                int yearrep = repeatDate.get(Calendar.YEAR)-end.get(Calendar.YEAR);
                int monthrep = repeatDate.get(Calendar.MONTH)-end.get(Calendar.MONTH);
                if(monthrep<0)
                    monthrep=12+monthrep;
                int secondsrep = (repeatDate.get(Calendar.SECOND)-end.get(Calendar.SECOND));
                if(seconds<0)
                    secondsrep=60+secondsrep;
                int minutesrep = repeatDate.get(Calendar.MINUTE)-end.get(Calendar.MINUTE);
                int hourrep = repeatDate.get(Calendar.HOUR_OF_DAY)-end.get(Calendar.HOUR_OF_DAY);
                if(hourrep<0)
                    hourrep=24+hourrep;
                int dayrep = repeatDate.get(Calendar.DAY_OF_MONTH)-end.get(Calendar.DAY_OF_MONTH);
                if(dayrep<0){
                    dayrep = end.getActualMaximum(Calendar.DAY_OF_MONTH)-end.get(Calendar.DAY_OF_MONTH);
                    dayrep+=repeatDate.get(Calendar.DAY_OF_MONTH);
                }


                start.add(Calendar.YEAR, yearrep);
                start.add(Calendar.MONTH, monthrep);
                start.add(Calendar.DAY_OF_MONTH, dayrep);
                start.add(Calendar.HOUR_OF_DAY, hourrep);
                start.add(Calendar.MINUTE, minutesrep);
                start.add(Calendar.SECOND, secondsrep);
                temp = start.get(Calendar.MONTH)+1;
                date = start.get(Calendar.DAY_OF_MONTH)+"/"+temp+"/"+start.get(Calendar.YEAR)+" "+start.get(Calendar.HOUR_OF_DAY)+":"+start.get(Calendar.MINUTE)+":"+start.get(Calendar.SECOND);;
                taskContents.put("REPEAT_AFTER", date);
                Calendar c = Calendar.getInstance();
                date = c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
                taskContents.put("MODIFIED_DATE", date);
                new CalendarTask(getApplicationContext()).UpdateEvent(cursor.getInt(9),cursor.getString(7),endDateAndTime,cursor.getString(10));
                db.update("NEWTASK6", taskContents, "_id=?", new String[]{String.valueOf(row)});
            }
        }
        cursor.close();
        db.close();
        stopSelf();
    }


}
