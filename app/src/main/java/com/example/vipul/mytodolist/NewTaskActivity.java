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


public class NewTaskActivity extends Activity {

    private Calendar myCalendar;
    private EditText fromtext;
    private EditText totext;
    private CheckBox checkBox;
    private RadioGroup radioGroup;
    private TextView prior1;
    private TextView prior2;
    private TextView prior3;
    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    private ImageView ivImage;
    private final String[] daysList = {"second(s)","minute(s)","hour(s)","day(s)","week(s)","month(s)","year(s)"};
    private int countday=1;
    private EditText startTimeText;
    private EditText finishTimeText;
    private CheckBox reminderCheckBox;
    private EditText nameText;
    private String name;
    private String from;
    private String to;
    private boolean isrepeating;
    private String time;
    private String timefinish;
    private RadioButton other;
    private RadioButton daily;
    private RadioButton weekly;
    private RadioButton monthly;
    private RadioButton yearly;
    private String rep;
    private EditText descriptiontext;
    private String description;
    private int priority=1;
    private byte[] finalImage = null;
    private boolean isReminderSet;


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
        setContentView(R.layout.activity_new_task);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        myCalendar = Calendar.getInstance();
        fromtext = (EditText)findViewById(R.id.from_date_text);
        totext = (EditText)findViewById(R.id.to_date_text);
        fromtext.setFocusableInTouchMode(false);
        fromtext.setFocusable(false);
        totext.setFocusableInTouchMode(false);
        totext.setFocusable(false);
        fromtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewTaskActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        totext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewTaskActivity.this, date2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        checkBox = (CheckBox)findViewById(R.id.repeat_checkbox);
        radioGroup = (RadioGroup)findViewById(R.id.repeat_radiobox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(isChecked);
                }
            }
        });

        for(int i=0;i<radioGroup.getChildCount();i++){
            radioGroup.getChildAt(i).setEnabled(false);
        }

        prior1 = (TextView)findViewById(R.id.priority1);
        prior2 = (TextView)findViewById(R.id.priority2);
        prior3 = (TextView)findViewById(R.id.priority3);
        prior1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prior1.setBackgroundResource(R.drawable.rounded_background);
                prior2.setBackgroundColor(Color.TRANSPARENT);
                prior3.setBackgroundColor(Color.TRANSPARENT);
                priority = 1;
            }
        });

        prior2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prior2.setBackgroundResource(R.drawable.rounded_background);
                prior1.setBackgroundColor(Color.TRANSPARENT);
                prior3.setBackgroundColor(Color.TRANSPARENT);
                priority=2;
            }
        });

        prior3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prior3.setBackgroundResource(R.drawable.rounded_background);
                prior2.setBackgroundColor(Color.TRANSPARENT);
                prior1.setBackgroundColor(Color.TRANSPARENT);
                priority=3;
            }
        });

        descriptiontext = (EditText)findViewById(R.id.description_text);
        ivImage = (ImageView)findViewById(R.id.attach_image);
        other = (RadioButton)findViewById(R.id.other_button);
        daily = (RadioButton)findViewById(R.id.daily_button);
        weekly = (RadioButton)findViewById(R.id.weekly_button);
        monthly = (RadioButton)findViewById(R.id.monthly_button);
        yearly = (RadioButton)findViewById(R.id.yearly_button);
        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final Dialog dialog = new Dialog(NewTaskActivity.this);
                    dialog.setContentView(R.layout.dialog_repeat);
                    final Spinner repeatList = (Spinner) dialog.findViewById(R.id.daylist);
                    final ArrayAdapter<String> repeatAdapter = new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.select_dialog_singlechoice, daysList);
                    repeatList.setAdapter(repeatAdapter);
                    dialog.show();
                    final EditText daycountText = (EditText) dialog.findViewById(R.id.count_text);
                    daycountText.setFocusable(false);
                    daycountText.setFocusableInTouchMode(false);
                    Button countupButton = (Button) dialog.findViewById(R.id.countup_button);
                    Button countdownButton = (Button) dialog.findViewById(R.id.countdown_button);
                    Button dontrepeatButton = (Button) dialog.findViewById(R.id.repeat_button);
                    Button okButton = (Button) dialog.findViewById(R.id.ok_button);
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
                            rep=null;
                            countday=1;
                            checkBox.setChecked(false);
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


        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        startTimeText = (EditText)findViewById(R.id.start_time_text);
        startTimeText.setFocusableInTouchMode(false);
        startTimeText.setFocusable(false);
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTimeText.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        finishTimeText = (EditText)findViewById(R.id.finish_time_text);
        finishTimeText.setFocusableInTouchMode(false);
        finishTimeText.setFocusable(false);
        finishTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        finishTimeText.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        reminderCheckBox = (CheckBox)findViewById(R.id.reminder_checkbox);

        nameText = (EditText)findViewById(R.id.task_name_edittext);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_leave,R.anim.anim_enter);
    }

    public void createNewTask(View v){
        name = nameText.getText().toString();
        from = fromtext.getText().toString();
        to = totext.getText().toString();
        time = startTimeText.getText().toString();
        timefinish = finishTimeText.getText().toString();
        isrepeating = checkBox.isChecked();
        Calendar c = Calendar.getInstance();
        String repeatInterval = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String s = to + " " + timefinish + ":00";
        try{
            c.setTime(formatter.parse(s));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(isrepeating){
            if(daily.isChecked()){
                c.add(Calendar.DAY_OF_MONTH,1);
                repeatInterval = ""+1+" "+"day";
            }
            else if(weekly.isChecked()){
                c.add(Calendar.DAY_OF_MONTH,7);
                repeatInterval = ""+1+" "+"week";
            }
            else if(monthly.isChecked()){
                c.add(Calendar.MONTH,1);
                repeatInterval = ""+1+" "+"month";
            }
            else if(yearly.isChecked()){
                c.add(Calendar.YEAR,1);
                repeatInterval = ""+1+" "+"year";
            }
            else if(other.isChecked()){
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
        description = descriptiontext.getText().toString();
        isReminderSet = reminderCheckBox.isChecked();
        int calendarEventId = -1;
        SQLiteOpenHelper todoDatabaseHelper = new TODOListDatabaseHelper(this);
        SQLiteDatabase db = todoDatabaseHelper.getWritableDatabase();
        insertTask(db, name, from, to, time,timefinish, isrepeating, c, description, priority, finalImage, isReminderSet, repeatInterval, calendarEventId);
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void insertTask(SQLiteDatabase db,String taskName, String startDate, String endDate, String startTime,String finishTime, boolean repeatTask,Calendar repeatAfterDate, String description, int priority, byte[] fi, boolean isReminderSet, String repeatInterval, int calendarEventId){
        ContentValues taskValues = new ContentValues();
        taskValues.put("TASK_NAME",taskName);
        taskValues.put("START_DATE",startDate);
        taskValues.put("END_DATE",endDate);
        taskValues.put("START_TIME",startTime);
        taskValues.put("END_TIME",finishTime);
        taskValues.put("REPEAT_TASK",repeatTask);
        int temp = repeatAfterDate.get(Calendar.MONTH)+1;
        String date = repeatAfterDate.get(Calendar.DAY_OF_MONTH)+"/"+temp+"/"+repeatAfterDate.get(Calendar.YEAR)+" "+repeatAfterDate.get(Calendar.HOUR_OF_DAY)+":"+repeatAfterDate.get(Calendar.MINUTE)+":"+repeatAfterDate.get(Calendar.SECOND);
        taskValues.put("REPEAT_AFTER", date);
        taskValues.put("DESCRIPTION", description);
        taskValues.put("PRIORITY",priority);
        taskValues.put("IMAGE", fi);
        taskValues.put("REMINDER", isReminderSet);
        taskValues.put("REPEAT_INTERVAL",repeatInterval);
        taskValues.put("CALENDAR_EVENT_ID",calendarEventId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        taskValues.put("MODIFIED_DATE", dateFormat.format(Calendar.getInstance().getTime()));
        int r = (int) db.insert("NEWTASK6", null, taskValues);
        int rp,sr=0;
        if(repeatTask)
            rp=1;
        else
            rp=0;
        if(isReminderSet)
            sr=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date startDateAndTime = null,finishTimeAndDate = null;
        try {
            startDateAndTime = formatter.parse(startDate +" " + startTime + ":00");
            finishTimeAndDate = formatter.parse(endDate +" "+ finishTime + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(this,TimerService.class);
        if((startDateAndTime.getTime()-(new Date().getTime()))>0){
            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS,(startDateAndTime.getTime()-(new Date().getTime())));
            i.putExtra(TimerService.EXTRA_SERVICE_FLAG,1);
        }
        else if(startDateAndTime.getTime()-(new Date().getTime())<=0){
            i.putExtra(TimerService.EXTRA_SERVICE_SECONDS,finishTimeAndDate.getTime()-(new Date().getTime()));
            i.putExtra(TimerService.EXTRA_SERVICE_FLAG,2);
        }

        i.putExtra(TimerService.EXTRA_SERVICE_ROW,r);
        i.putExtra(TimerService.EXTRA_SERVICE_COUNTDOWN_INTERVAL,(long)1000);
        i.putExtra(TimerService.EXTRA_SERVICE_PRIORITY,priority);
        i.putExtra(TimerService.EXTRA_SERVICE_REMINDER,isReminderSet);
        i.putExtra(TimerService.EXTRA_SERVICE_TASKNAME, taskName);
        i.putExtra(TimerService.EXTRA_SERVICE_REPEATING,repeatTask);
        startService(i);
        db.close();
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
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

                ivImage.setImageBitmap(thumbnail);
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
                ivImage.setImageBitmap(bm);
            }
        }
    }


    private void UpdateLabel(){
        String myformat = "dd/MM/yy";
        SimpleDateFormat formatter = new SimpleDateFormat(myformat, Locale.UK);
        fromtext.setText(formatter.format(myCalendar.getTime()));
    }

    private void UpdateLabel2(){
        String myformat = "dd/MM/yy";
        SimpleDateFormat formatter = new SimpleDateFormat(myformat, Locale.UK);
        totext.setText(formatter.format(myCalendar.getTime()));
    }


}
