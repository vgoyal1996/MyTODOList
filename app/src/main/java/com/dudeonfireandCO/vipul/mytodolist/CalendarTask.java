package com.dudeonfireandCO.vipul.mytodolist;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarTask {
    private Context context;
    private ArrayList<CalendarObject> calendarList;

    public CalendarTask(Context context) {
        this.context = context;
    }

    public ArrayList<CalendarObject> isCalendarAvailable(){
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        Cursor calCursor = context.getContentResolver().
                query(CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        CalendarContract.Calendars.VISIBLE + " = 1",
                        null,
                        CalendarContract.Calendars._ID + " ASC");
        calendarList = new ArrayList<>();
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String name = calCursor.getString(2);
                calendarList.add(new CalendarObject(id,name));
            } while (calCursor.moveToNext());
        }
        calCursor.close();
        return calendarList;
    }

    public class CalendarObject{
        private long id;
        private String name;

        public CalendarObject(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public long addToCalendar(long calId, int position, String title, String endDateAndTime, String description, boolean setReminder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar end = Calendar.getInstance();
        try {
            end.setTime(sdf.parse(endDateAndTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, end.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        if(description!=null)
            values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.IS_ORGANIZER, 1);
        Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        long eventId = Long.parseLong(uri.getLastPathSegment());
        values.clear();
        if(setReminder){
            values.put(CalendarContract.Reminders.EVENT_ID, eventId);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.MINUTES, 1);
            context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
        }
        Toast.makeText(context,"Task "+title+" added to "+calendarList.get(position).getName(),Toast.LENGTH_LONG).show();
        return eventId;
    }

    public int deleteFromCalendar(long eventId){
        String[] selArgs = new String[]{Long.toString(eventId)};
        int deleted = context.getContentResolver().delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID + " =? ", selArgs);
        return -1;
    }

    public void UpdateEvent(long eventId, String title, String endDateAndTime, String description){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar end = Calendar.getInstance();
        try {
            end.setTime(sdf.parse(endDateAndTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, end.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        if(description!=null)
            values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.IS_ORGANIZER, 1);
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int updated =
                context.getContentResolver().
                        update(
                                eventUri,
                                values,
                                null,null);
    }

}