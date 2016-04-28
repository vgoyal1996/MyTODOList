package com.example.vipul.mytodolist;


import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import java.io.Serializable;

public class TaskCountDown extends CountDownTimer implements Serializable {
    private String endHead;
    private String endMsg;
    private int flag;
    private boolean isReminderSet;
    private String taskName;
    private int priority;
    private int row;
    private Context context;
    private long millisInFuture;
    private boolean isRepeating;

    public TaskCountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public TaskCountDown(long millisInFuture, long countDownInterval, int flag) {
        super(millisInFuture, countDownInterval);
        this.flag = flag;
    }

    public TaskCountDown(long millisInFuture, long countDownInterval,Context context, int flag, boolean isReminderSet, String taskName, int priority, int row, boolean isRepeating) {
        super(millisInFuture, countDownInterval);
        this.context = context;
        this.millisInFuture = millisInFuture;
        this.flag = flag;
        this.isReminderSet = isReminderSet;
        this.taskName = taskName;
        this.priority = priority;
        this.row = row;
        this.isRepeating = isRepeating;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setIsReminderSet(boolean isReminderSet) {
        this.isReminderSet = isReminderSet;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setMillisInFuture(long millisInFuture) {
        this.millisInFuture = millisInFuture;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        UpdateMessage(millisUntilFinished);
        this.millisInFuture = millisUntilFinished;
    }

    @Override
    public void onFinish() {
        if(flag==1){
            endHead = "Task started";
            endMsg = "";
        }
        else{
            endHead = "Task ended";
            endMsg = "";
        }
        if(isReminderSet){
            Intent intent = new Intent(context, TaskNotificationService.class);
            intent.putExtra(TaskNotificationService.EXTRA_NAME, taskName);
            intent.putExtra(TaskNotificationService.EXTRA_ROW, row);
            intent.putExtra(TaskNotificationService.EXTRA_FLAG, flag);
            intent.putExtra(TaskNotificationService.EXTRA_PRIORITY, priority);
            intent.putExtra(TaskNotificationService.EXTRA_REPEATING, isRepeating);
            context.startService(intent);
        }
    }

    public long getMillisInFuture() {
        return millisInFuture;
    }

    public String getEndHead() {
        return endHead;
    }

    public String getEndMsg() {
        return endMsg;
    }

    public Context getContext() {
        return context;
    }

    public int getRow() {
        return row;
    }

    public int getPriority() {
        return priority;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isReminderSet() {
        return isReminderSet;
    }

    public void UpdateMessage(long millisUntilFinished){
        long seconds1 = (millisUntilFinished / 1000);
        long minutes1 = (seconds1 / 60);
        long hours1 = (minutes1 / 60);
        long days1 = (hours1 / 24);
        long seconds = (seconds1 % 60);
        long minutes = (minutes1 % 60);
        long hours = (hours1 % 24);
        long days = (days1);
        if (flag == 1) {
            if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
                endHead = "Task Started";
                endMsg = "";
            }
            else{
                endHead = "Task starts in:";
                if (days == 0 && hours == 0 && minutes == 0 && seconds != 0) {
                    endMsg = ""+seconds + "sec";

                }
                else if (days == 0 && hours == 0) {
                    endMsg = ""+minutes + "min " + seconds + "sec ";
                }
                else if (days == 0) {
                    endMsg = ""+hours + "hrs " + minutes + "min " + seconds + "sec ";
                }
                else {
                    endMsg = ""+days + "days " + hours + "hrs " + minutes + "min " + seconds + "sec ";
                }
            }
        } else {
            if (days <= 0 && hours <= 0 && minutes <= 0 && seconds <= 0) {
                endHead = "Task ended";
                endMsg = "";
            }
            else {
                endHead = "Task ends in:";
                if (days == 0 && hours == 0 && minutes == 0 && seconds != 0) {
                    endMsg = ""+seconds + "sec";
                }
                else if (days == 0 && hours == 0) {
                    endMsg = ""+minutes + "min " + seconds + "sec ";

                }
                else if (days == 0) {
                    endMsg = "" + hours + "hrs " + minutes + "min " + seconds + "sec ";
                }
                else {
                    endMsg = ""+days + "days " + hours + "hrs " + minutes + "min " + seconds + "sec ";
                }
            }
        }
    }


}
