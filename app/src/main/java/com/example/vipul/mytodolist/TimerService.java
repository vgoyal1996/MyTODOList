package com.example.vipul.mytodolist;

import android.app.IntentService;
import android.content.Intent;

import java.util.HashMap;


public class TimerService extends IntentService {
    public static HashMap<Integer,TaskCountDown> taskCounters = new HashMap<>();
    //public static final String EXTRA_SERVICE_COUNTDOWN = "countdown";
    public static final String EXTRA_SERVICE_FLAG = "flag";
    public static final String EXTRA_SERVICE_REMINDER = "reminder";
    public static final String EXTRA_SERVICE_COUNTDOWN_INTERVAL = "interval";
    public static final String EXTRA_SERVICE_SECONDS = "seconds";
    public static final String EXTRA_SERVICE_TASKNAME = "taskname";
    public static final String EXTRA_SERVICE_PRIORITY = "priority";
    public static final String EXTRA_SERVICE_ROW = "row";
    public static final String EXTRA_SERVICE_REPEATING = "repeating";

    public TimerService() {
        super("TimerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra(EXTRA_SERVICE_ROW)) {
            int row = (int) intent.getExtras().get(EXTRA_SERVICE_ROW);
            //TaskCountDown tcd = intent.getParcelableExtra(EXTRA_SERVICE_COUNTDOWN);
            int flag = (int) intent.getExtras().get(EXTRA_SERVICE_FLAG);
            boolean isReminderset = (boolean) intent.getExtras().get(EXTRA_SERVICE_REMINDER);
            long interval = (long) intent.getExtras().get(EXTRA_SERVICE_COUNTDOWN_INTERVAL);
            long seconds = (long) intent.getExtras().get(EXTRA_SERVICE_SECONDS);
            String taskName = (String) intent.getExtras().get(EXTRA_SERVICE_TASKNAME);
            int priority = (int) intent.getExtras().get(EXTRA_SERVICE_PRIORITY);
            boolean isRepeating = (boolean)intent.getExtras().get(EXTRA_SERVICE_REPEATING);
            TaskCountDown tcd = new TaskCountDown(seconds, interval, getApplicationContext(), flag, isReminderset, taskName, priority, row, isRepeating);
            tcd.start();
            if (!taskCounters.containsKey(row)) {
                taskCounters.put(row, tcd);
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
