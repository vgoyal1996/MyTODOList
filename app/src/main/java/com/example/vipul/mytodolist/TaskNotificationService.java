package com.example.vipul.mytodolist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TaskNotificationService extends IntentService {
    public static final int NOTIFICATION_ID=9874;
    public static final String EXTRA_ROW = "message";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_FLAG = "flag";
    public static final String EXTRA_PRIORITY = "priority";
    private int row;
    private String name;
    private int flag;
    private int prior;

    public TaskNotificationService() {
        super("TaskNotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        flag = (int) intent.getExtras().get(EXTRA_FLAG);
        row = (int)intent.getExtras().get(EXTRA_ROW);
        name = (String)intent.getExtras().get(EXTRA_NAME);
        prior = (int)intent.getExtras().get(EXTRA_PRIORITY);
        String message;
        if(flag==1) {
            message = "Task " + name + " started";
        }
        else{
            message = "Task " + name + " completed";
        }
        showNotification(message);
    }

    public void showNotification(final String message){
        Intent intent = new Intent(this,TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_ROW,row);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isQuietHours = preferences.getBoolean(getString(R.string.pref_key_quiet_hours_vibrate_and_sound), true);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        if(isQuietHours){
            String startTime = preferences.getString(getString(R.string.pref_key_quiet_hours_start),"12:00");
            String endTime = preferences.getString(getString(R.string.pref_key_quiet_hours_end), "15:00");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
            try {
                int x = start.get(Calendar.MONTH)+1;
                String temp = ""+start.get(Calendar.DAY_OF_MONTH)+"/"+x+"/"+start.get(Calendar.YEAR);
                start.setTime(dateFormat.parse(temp+" "+startTime));
                end.setTime(dateFormat.parse(temp+" "+endTime));
                if(end.get(Calendar.HOUR_OF_DAY)<start.get(Calendar.HOUR_OF_DAY)){
                    end.add(Calendar.DAY_OF_MONTH,1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        NotificationCompat.Builder notification;
        int notPrior;
        if(prior==1)
            notPrior=Notification.PRIORITY_LOW;
        else if(prior==2)
            notPrior=Notification.PRIORITY_DEFAULT;
        else
            notPrior=Notification.PRIORITY_MAX;

        notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentTitle(getString(R.string.app_name));
        if(flag==1)
            notification.setAutoCancel(true);
        else
            notification.setAutoCancel(false);
        notification.setPriority(notPrior);
        Calendar curr = Calendar.getInstance();
        if(isQuietHours) {
            Log.v("TaskNotification", "" + curr.compareTo(start));
            Log.v("TaskNotification",""+curr.compareTo(end));
            if (!(curr.compareTo(start)>=0&&curr.compareTo(end)<=0)) {
                if (preferences.getBoolean(getString(R.string.pref_key_vibrate), true))
                    notification.setVibrate(new long[]{500});
                if (preferences.getString(getString(R.string.pref_key_ringtone), null) != null)
                    notification.setSound(Uri.parse(preferences.getString(getString(R.string.pref_key_ringtone), null)));
            }
        }
        else{
            if (preferences.getBoolean(getString(R.string.pref_key_vibrate), true))
                notification.setVibrate(new long[]{500});
            if (preferences.getString(getString(R.string.pref_key_ringtone), null) != null)
                notification.setSound(Uri.parse(preferences.getString(getString(R.string.pref_key_ringtone), null)));
        }
        notification.setContentText(message);
        notification.setContentIntent(pendingIntent);
        Intent i = new Intent(TaskNotificationService.this,SnoozeService.class);
        Intent completeIntent = new Intent(TaskNotificationService.this,CompleteAndRestartTaskService.class);
        completeIntent.putExtra(CompleteAndRestartTaskService.SERVICE_EXTRA_ROW,row);
        i.putExtra(SnoozeService.SNOOZE_EXTRA_NAME, name);
        i.putExtra(SnoozeService.SNOOZE_EXTRA_ROW, row);
        i.putExtra(SnoozeService.SNOOZE_EXTRA_FLAG, flag);
        i.putExtra(SnoozeService.SNOOZE_EXTRA_PRIORITY, prior);
        i.putExtra(SnoozeService.SNOOZE_EXTRA_NOTIFICATION_ID,NOTIFICATION_ID);
        notification.addAction(R.drawable.complete, "Complete", PendingIntent.getService(getApplicationContext(),0,completeIntent,PendingIntent.FLAG_UPDATE_CURRENT));
        notification.addAction(R.drawable.snooze, "Snooze", PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notif = notification.build();
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID,notif);
    }
}