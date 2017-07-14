package com.dudeonfireandCO.vipul.mytodolist;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.vipul.mytodolist.R;

import java.util.Calendar;


public class SnoozeService extends IntentService {
    public static final String SNOOZE_EXTRA_NAME = "NAME";
    public static final String SNOOZE_EXTRA_ROW = "ROW";
    public static final String SNOOZE_EXTRA_PRIORITY = "PRIORITY";
    public static final String SNOOZE_EXTRA_FLAG = "FLAG";
    public static final String SNOOZE_EXTRA_NOTIFICATION_ID="ID";
    private int row;
    private int p;
    private int f;
    private String name;

    public SnoozeService() {
        super("SnoozeService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        name = (String)intent.getExtras().get(SNOOZE_EXTRA_NAME);
        row = (int)intent.getExtras().get(SNOOZE_EXTRA_ROW);
        p = (int)intent.getExtras().get(SNOOZE_EXTRA_PRIORITY);
        f = (int)intent.getExtras().get(SNOOZE_EXTRA_FLAG);
        NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        m.cancel(TaskNotificationService.NOTIFICATION_ID);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String snoozePref = pref.getString(getString(R.string.pref_key_snooze), "" + 10 + " " + "minute(s)");
        String[] arr = snoozePref.split(" ");
        long time=0;
        long countDay = Long.parseLong(arr[0]);
        String rep = arr[1];
        if(rep.equals("second(s)"))
            time = countDay;
        else if(rep.equals("minute(s)"))
            time = countDay * 60;
        else if(rep.equals("hour(s)"))
            time = countDay*3600;
        else if(rep.equals("week(s)"))
            time = countDay*7*24;
        else if(rep.equals("day(s)"))
            time = countDay*24;
        else if(rep.equals("month(s)")){
            Calendar c = Calendar.getInstance();
            for(int i=1;i<=countDay;i++) {
                time += c.getActualMaximum(Calendar.DAY_OF_MONTH)*24*60*60;
                c.add(Calendar.MONTH,1);
            }
        }
        else if(rep.equals("year(s)"))
            time = countDay*365*24*60*60;
        //long snoozeTime = pref.getLong(getString(R.string.pref_key_snooze),10000);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,(int)time);
        SnoozeCountDown snoozeCountDown = new SnoozeCountDown((c.getTimeInMillis()-System.currentTimeMillis()),1000);
        snoozeCountDown.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //final int id = (int)intent.getExtras().get(SNOOZE_EXTRA_NOTIFICATION_ID);
        /*final long[] currentTimeMillis = {System.currentTimeMillis()};
        final long nextUpdateTimeMillis = currentTimeMillis[0] + 1 * DateUtils.MINUTE_IN_MILLIS;
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(Calendar.getInstance().getTimeInMillis()!=nextUpdateTimeMillis)
                    handler.postDelayed(this,1000);
                else{
                    /*Intent i = new Intent(SnoozeService.this,TaskNotificationService.class);
                    i.putExtra(TaskNotificationService.EXTRA_NAME,name);
                    i.putExtra(TaskNotificationService.EXTRA_PRIORITY,p);
                    i.putExtra(TaskNotificationService.EXTRA_ROW,row);
                    i.putExtra(TaskNotificationService.EXTRA_FLAG,f);
                    startService(i);
                    Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    class SnoozeCountDown extends CountDownTimer{


        public SnoozeCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.v("SnoozeService",""+millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            Intent i = new Intent(SnoozeService.this,TaskNotificationService.class);
            i.putExtra(TaskNotificationService.EXTRA_NAME,name);
            i.putExtra(TaskNotificationService.EXTRA_PRIORITY,p);
            i.putExtra(TaskNotificationService.EXTRA_ROW,row);
            i.putExtra(TaskNotificationService.EXTRA_FLAG,f);
            startService(i);
        }
    }

}
