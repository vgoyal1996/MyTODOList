package com.example.vipul.mytodolist;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;


public class CompleteTaskService extends IntentService {
    public static final String SERVICE_EXTRA_ROW = "row";


    public CompleteTaskService() {
        super("CompleteTaskService");
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
        int row = (int) intent.getExtras().get(SERVICE_EXTRA_ROW);
        NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        m.cancel(TaskNotificationService.NOTIFICATION_ID);
        stopSelf();
    }


}
