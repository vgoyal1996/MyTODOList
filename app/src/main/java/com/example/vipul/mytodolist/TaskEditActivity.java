package com.example.vipul.mytodolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TaskEditActivity extends Activity {
    public static String EXTRA_ROW = "Extra";
    private EditText editname;
    private EditText editFromDate;
    private EditText editToDate;
    private EditText editStartTime;
    private CheckBox editRepeatText;
    private RadioButton editDaily;
    private RadioButton editWeekly;
    private RadioButton editMonthly;
    private RadioButton editYearly;
    private RadioButton editOther;
    private EditText editDescription;
    private TextView editPriority1;
    private TextView editPriority2;
    private TextView editPriority3;
    private ImageView editImage;
    private EditText editFinishTime;
    private CheckBox editReminder;
    private Calendar myCalendar;
    private RadioGroup r;
    private byte[] finalImage = null;
    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    private int priority;
    private final String[] daysList = {"second(s)","minute(s)","hour(s)","day(s)","week(s)","month(s)","year(s)"};
    private int countday;
    private String rep=daysList[0];
    int row;
    private int eventId;
    private String repeatInterval;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            UpdateLabel();
        }
    };

    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            UpdateLabel2();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        editname = (EditText)findViewById(R.id.edit_task_name_edittext);
        editFromDate = (EditText)findViewById(R.id.edit_from_date_text);
        editToDate = (EditText)findViewById(R.id.edit_to_date_text);
        editStartTime = (EditText)findViewById(R.id.edit_start_time_text);
        editFinishTime = (EditText)findViewById(R.id.edit_finish_time_text);
        editDescription = (EditText)findViewById(R.id.edit_description_text);
        editRepeatText = (CheckBox)findViewById(R.id.edit_repeat_checkbox);
        editReminder = (CheckBox)findViewById(R.id.edit_reminder_checkbox);
        editDaily = (RadioButton)findViewById(R.id.edit_daily_button);
        editWeekly = (RadioButton)findViewById(R.id.edit_weekly_button);
        editMonthly = (RadioButton)findViewById(R.id.edit_monthly_button);
        editYearly = (RadioButton)findViewById(R.id.edit_yearly_button);
        editOther = (RadioButton)findViewById(R.id.edit_other_button);
        editPriority1 = (TextView)findViewById(R.id.edit_priority1);
        editPriority2 = (TextView)findViewById(R.id.edit_priority2);
        editPriority3 = (TextView)findViewById(R.id.edit_priority3);
        editImage = (ImageView)findViewById(R.id.edit_attach_image);
        r = (RadioGroup)findViewById(R.id.repeat_radiobox);
        myCalendar = Calendar.getInstance();
        makeView();
        row = (int)getIntent().getExtras().get(EXTRA_ROW);
        SQLiteOpenHelper taskHelper = new TODOListDatabaseHelper(this);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        Cursor cursor = db.query("NEWTASK6",new String[]{"TASK_NAME","START_DATE","END_DATE","START_TIME","END_TIME","REPEAT_TASK","REPEAT_AFTER","DESCRIPTION","PRIORITY","IMAGE","REMINDER","REPEAT_INTERVAL","CALENDAR_EVENT_ID"},
                "_id=?",new String[]{Integer.toString(row)},null,null,null);
        if(cursor.moveToFirst()) {
            editname.setText(cursor.getString(0));
            editFromDate.setText(cursor.getString(1));
            editToDate.setText(cursor.getString(2));
            editStartTime.setText(cursor.getString(3));
            editFinishTime.setText(cursor.getString(4));
            boolean val = (cursor.getInt(5) != 0);
            editRepeatText.setChecked(val);
            //String repeatDate = cursor.getString(6);
            repeatInterval = cursor.getString(11);
            eventId = cursor.getInt(12);
            if(val) {
                String arr[] = repeatInterval.split(" ");
                if (arr[1].equals("day")&&arr[0].equals("1")) {
                    editDaily.setChecked(true);
                }
                else if (arr[1].equals("week")&&arr[0].equals("1")) {
                    editWeekly.setChecked(true);
                }
                else if (arr[1].equals("month")&&arr[0].equals("1")) {
                    editMonthly.setChecked(true);
                }
                else if (arr[1].equals("year")&&arr[0].equals("1")) {
                    editYearly.setChecked(true);
                }
                else {
                    countday = Integer.parseInt(arr[0]);
                    switch (arr[1]) {
                        case "second":
                            rep = daysList[0];
                            break;
                        case "minute":
                            rep = daysList[1];
                            break;
                        case "hour":
                            rep = daysList[2];
                            break;
                        case "day":
                            rep = daysList[3];
                            break;
                        case "week":
                            rep = daysList[4];
                            break;
                        case "month":
                            rep = daysList[5];
                            break;
                        case "year":
                            rep = daysList[6];
                            break;
                    }
                    editOther.setChecked(true);
                }
            }
            if (cursor.getString(6) != null)
                editDescription.setText(cursor.getString(7));
            boolean v = (cursor.getInt(10) != 0);
            editReminder.setChecked(v);
            int p = cursor.getInt(8);
            if (p == 1) {
                editPriority1.setBackgroundResource(R.drawable.rounded_background);
                editPriority2.setBackgroundResource(Color.TRANSPARENT);
                editPriority3.setBackgroundResource(Color.TRANSPARENT);
                priority = 1;
            } else if (p == 2) {
                editPriority2.setBackgroundResource(R.drawable.rounded_background);
                editPriority1.setBackgroundResource(Color.TRANSPARENT);
                editPriority3.setBackgroundResource(Color.TRANSPARENT);
                priority = 2;
            } else {
                editPriority3.setBackgroundResource(R.drawable.rounded_background);
                editPriority2.setBackgroundResource(Color.TRANSPARENT);
                editPriority1.setBackgroundResource(Color.TRANSPARENT);
                priority = 3;
            }
            if (cursor.getBlob(9) != null) {
                byte[] bytes = cursor.getBlob(9);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                editImage.setImageBitmap(bm);
            }
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_leave, R.anim.anim_enter);
    }

    public void updateTask(View v){
        String name = editname.getText().toString();
        String from = editFromDate.getText().toString();
        String to = editToDate.getText().toString();
        String time = editStartTime.getText().toString();
        String timefinish = editFinishTime.getText().toString();
        boolean isrepeating = editRepeatText.isChecked();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String s = to + " " + timefinish + ":00";
        try{
            c.setTime(formatter.parse(s));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(isrepeating){
            if(editDaily.isChecked()){
                c.add(Calendar.DAY_OF_MONTH,1);
                repeatInterval = ""+1+" "+"day";
            }
            else if(editWeekly.isChecked()){
                c.add(Calendar.DAY_OF_MONTH,7);
                repeatInterval = ""+1+" "+"week";
            }
            else if(editMonthly.isChecked()){
                c.add(Calendar.MONTH,1);
                repeatInterval = ""+1+" "+"month";
            }
            else if(editYearly.isChecked()){
                c.add(Calendar.YEAR,1);
                repeatInterval = ""+1+" "+"year";
            }
            else if(editOther.isChecked()){
                if(rep.equals("second(s)")){
                    c.add(Calendar.SECOND,countday);
                    repeatInterval = ""+countday+" "+"second";
                }
                if(rep.equals("minute(s)")){
                    c.add(Calendar.MINUTE,countday);
                    repeatInterval = ""+countday+" "+"minute";
                }
                if(rep.equals("hour(s)")){
                    c.add(Calendar.HOUR_OF_DAY,countday);
                    repeatInterval = ""+countday+" "+"hour";
                }
                if(rep.equals("day(s)")){
                    c.add(Calendar.DAY_OF_MONTH,countday);
                    repeatInterval = ""+countday+" "+"day";
                }
                if(rep.equals("week(s)")){
                    c.add(Calendar.DAY_OF_MONTH,7*countday);
                    repeatInterval = ""+countday+" "+"week";
                }
                if(rep.equals("month(s)")){
                    c.add(Calendar.MONTH,countday);
                    repeatInterval = ""+countday+" "+"month";
                }
                if(rep.equals("year(s)")){
                    c.add(Calendar.YEAR,countday);
                    repeatInterval = ""+countday+" "+"year";
                }
            }
        }
        String description = editDescription.getText().toString();
        boolean isReminderSet = editReminder.isChecked();
        SQLiteOpenHelper todoDatabaseHelper = new TODOListDatabaseHelper(this);
        SQLiteDatabase db = todoDatabaseHelper.getWritableDatabase();
        updateMyTask(db, name, from, to, time, timefinish, isrepeating, c, description, priority, finalImage, isReminderSet, repeatInterval);
        //Toast.makeText(this,"inserted",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void updateMyTask(SQLiteDatabase db,String taskName, String startDate, String endDate, String startTime, String finishTime, boolean repeatTask,Calendar repeatAfterDate, String description, int priority, byte[] fi, boolean isReminderSet, String repeatInterval){
        ContentValues taskValues = new ContentValues();
        taskValues.put("TASK_NAME",taskName);
        taskValues.put("START_DATE",startDate);
        taskValues.put("END_DATE",endDate);
        taskValues.put("START_TIME",startTime);
        taskValues.put("END_TIME",finishTime);
        taskValues.put("REPEAT_TASK",repeatTask);
        int temp = repeatAfterDate.get(Calendar.MONTH)+1;
        String date = repeatAfterDate.get(Calendar.DAY_OF_MONTH)+"/"+temp+"/"+repeatAfterDate.get(Calendar.YEAR)+" "+repeatAfterDate.get(Calendar.HOUR_OF_DAY)+":"+repeatAfterDate.get(Calendar.MINUTE)+":"+repeatAfterDate.get(Calendar.SECOND);
        taskValues.put("REPEAT_AFTER",date);
        taskValues.put("DESCRIPTION", description);
        taskValues.put("PRIORITY", priority);
        taskValues.put("IMAGE", fi);
        taskValues.put("REMINDER", isReminderSet);
        taskValues.put("REPEAT_INTERVAL",repeatInterval);
        new CalendarTask(getApplicationContext()).UpdateEvent(eventId,taskName,endDate+" "+finishTime+":00",description);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        taskValues.put("MODIFIED_DATE", formatter.format(c.getTime()));
        Date startDateAndTime = null,finishTimeAndDate = null;
        try {
            startDateAndTime = formatter.parse(startDate +" " + startTime + ":00");
            finishTimeAndDate = formatter.parse(endDate +" "+ finishTime + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.update("NEWTASK6", taskValues, "_id=?", new String[]{Integer.toString(row)});
        Log.v("updateTask",""+row);

        if(TimerService.taskCounters.containsKey(row))
            TimerService.taskCounters.remove(row);

        Intent i = new Intent(this,TimerService.class);
        if((startDateAndTime.getTime()-(new Date().getTime()))>0){
            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS,(startDateAndTime.getTime()-(new Date().getTime())));
            i.putExtra(TimerService.EXTRA_SERVICE_FLAG,1);
        }
        else if(startDateAndTime.getTime()-(new Date().getTime())<=0){
            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS,finishTimeAndDate.getTime()-(new Date().getTime()));
            i.putExtra(TimerService.EXTRA_SERVICE_FLAG,2);
        }

        i.putExtra(TimerService.EXTRA_SERVICE_ROW,row);
        i.putExtra(TimerService.EXTRA_SERVICE_COUNTDOWN_INTERVAL,(long)1000);
        i.putExtra(TimerService.EXTRA_SERVICE_PRIORITY,priority);
        i.putExtra(TimerService.EXTRA_SERVICE_REMINDER,isReminderSet);
        i.putExtra(TimerService.EXTRA_SERVICE_TASKNAME,taskName);
        i.putExtra(TimerService.EXTRA_SERVICE_REPEATING,repeatTask);
        startService(i);
        db.close();
        Toast.makeText(this,"Update Successful", Toast.LENGTH_SHORT).show();
    }


    public void makeView(){
        editFromDate.setFocusableInTouchMode(false);
        editFromDate.setFocusable(false);
        editToDate.setFocusableInTouchMode(false);
        editToDate.setFocusable(false);
        editFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskEditActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TaskEditActivity.this, date2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        editRepeatText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < r.getChildCount(); i++) {
                    r.getChildAt(i).setEnabled(isChecked);
                }
            }
        });

        for(int i=0;i<r.getChildCount();i++){
            r.getChildAt(i).setEnabled(false);
        }

        editPriority1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPriority1.setBackgroundResource(R.drawable.rounded_background);
                editPriority2.setBackgroundResource(Color.TRANSPARENT);
                editPriority3.setBackgroundResource(Color.TRANSPARENT);
                priority = 1;
            }
        });

        editPriority2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPriority2.setBackgroundResource(R.drawable.rounded_background);
                editPriority1.setBackgroundResource(Color.TRANSPARENT);
                editPriority3.setBackgroundResource(Color.TRANSPARENT);
                priority = 2;
            }
        });

        editPriority3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPriority3.setBackgroundResource(R.drawable.rounded_background);
                editPriority2.setBackgroundResource(Color.TRANSPARENT);
                editPriority1.setBackgroundResource(Color.TRANSPARENT);
                priority = 3;
            }
        });


        editOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final Dialog dialog = new Dialog(TaskEditActivity.this);
                    dialog.setContentView(R.layout.dialog_repeat);
                    final Spinner repeatList = (Spinner) dialog.findViewById(R.id.daylist);
                    final ArrayAdapter<String> repeatAdapter = new ArrayAdapter<String>(TaskEditActivity.this, android.R.layout.select_dialog_singlechoice, daysList);
                    repeatList.setAdapter(repeatAdapter);
                    dialog.show();
                    final EditText daycountText = (EditText) dialog.findViewById(R.id.count_text);
                    daycountText.setFocusable(false);
                    daycountText.setFocusableInTouchMode(false);
                    Button countupButton = (Button) dialog.findViewById(R.id.countup_button);
                    Button countdownButton = (Button) dialog.findViewById(R.id.countdown_button);
                    Button dontrepeatButton = (Button) dialog.findViewById(R.id.repeat_button);
                    Button okButton = (Button) dialog.findViewById(R.id.ok_button);
                    daycountText.setText(""+countday);
                    if(rep.equals(daysList[0]))
                        repeatList.setSelection(0);
                    if(rep.equals(daysList[1]))
                        repeatList.setSelection(1);
                    if(rep.equals(daysList[2]))
                        repeatList.setSelection(2);
                    if(rep.equals(daysList[3]))
                        repeatList.setSelection(3);
                    if(rep.equals(daysList[4]))
                        repeatList.setSelection(4);
                    if(rep.equals(daysList[5]))
                        repeatList.setSelection(5);
                    if(rep.equals(daysList[6]))
                        repeatList.setSelection(6);
                    countupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            countday++;
                            daycountText.setText("" + countday);
                        }
                    });
                    countdownButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (countday > 1) {
                                countday--;
                                daycountText.setText("" + countday);
                            }
                        }
                    });
                    dontrepeatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            rep = null;
                            countday = 1;
                            editRepeatText.setChecked(false);
                        }
                    });
                    repeatList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            rep = daysList[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        editStartTime.setFocusableInTouchMode(false);
        editStartTime.setFocusable(false);
        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TaskEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editStartTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        editFinishTime.setFocusableInTouchMode(false);
        editFinishTime.setFocusable(false);
        editFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TaskEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editFinishTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskEditActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editImage.setImageBitmap(thumbnail);
                finalImage = bytes.toByteArray();

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                finalImage = bos.toByteArray();
                editImage.setImageBitmap(bm);
            }
        }
    }

    private void UpdateLabel(){
        String myformat = "dd/MM/yy";
        SimpleDateFormat formatter = new SimpleDateFormat(myformat, Locale.UK);
        editFromDate.setText(formatter.format(myCalendar.getTime()));
    }

    private void UpdateLabel2(){
        String myformat = "dd/MM/yy";
        SimpleDateFormat formatter = new SimpleDateFormat(myformat, Locale.UK);
        editToDate.setText(formatter.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_edit, menu);
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
