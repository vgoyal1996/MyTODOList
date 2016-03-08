package com.example.vipul.mytodolist;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyObject implements Parcelable {
    private String task;
    private int prior;
    private int id;
    private Date modified;
    private String startDate;
    private String endDate;
    private Date startDateAndTime;
    private Date endDateAndTime;
    private boolean isrepeating;
    private boolean setReminder;
    private Date repeatDate;
    private byte[] image;
    private String description;
    private Bitmap bitmap=null;

    public MyObject(int id, int prior, String task, String modified) {
        this.id = id;
        this.prior = prior;
        this.task = task;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        try {
            this.modified = formatter.parse(modified);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public MyObject(int id, int prior, String task, String modified, String startDate, String endDate, String startTime, String endTime, int repeatTask, String repeatDate, String description, byte[] image, int setReminder){
        this.id = id;
        this.prior = prior;
        this.task = task;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        try {
            this.modified = formatter.parse(modified);
            startDateAndTime = formatter.parse(startDate+" "+startTime+":00");
            endDateAndTime = formatter.parse(endDate+" "+endTime+":00");
            this.repeatDate = formatter.parse(repeatDate+" "+startTime+":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isrepeating = repeatTask == 1;
        this.setReminder = setReminder == 1;
        this.startDate = startDate;
        this.endDate = endDate;
        if(image!=null) {
            this.image = image;
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        this.description = description;
    }

    public MyObject(int id, int prior, String task, String modified,String startDate,String endDate,byte[] image){
        this.id = id;
        this.prior = prior;
        this.task = task;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        try {
            this.modified = formatter.parse(modified);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.startDate = startDate;
        this.endDate = endDate;
        if(image!=null) {
            this.image = image;
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

    private MyObject(Parcel in) {
        task = in.readString();
        prior = in.readInt();
        id = in.readInt();
        description = in.readString();
        isrepeating = in.readInt() == 1;
        setReminder = in.readInt() == 1;
        startDate = in.readString();
        endDate = in.readString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        try {
            modified = formatter.parse(in.readString());
            startDateAndTime = formatter.parse(in.readString());
            endDateAndTime = formatter.parse(in.readString());
            repeatDate = formatter.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getStartDateAndTime() {
        return startDateAndTime;
    }

    public Date getEndDateAndTime() {
        return endDateAndTime;
    }

    public boolean isrepeating() {
        return isrepeating;
    }

    public boolean isSetReminder() {
        return setReminder;
    }

    public Date getRepeatDate() {
        return repeatDate;
    }

    public String getDescription() {
        return description;
    }

    public String getTask() {
            return task;
        }

    public int getPrior() {
            return prior;
        }

    public int getId() {
            return id;
        }

    public Date getModified() {
            return modified;
        }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int describeContents() {
            return 0;
        }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(task);
        dest.writeInt(prior);
        dest.writeInt(id);
        dest.writeString(modified.toString());
        dest.writeString(endDate);
        dest.writeString(startDate);
//        dest.writeString(endDateAndTime.toString());
//        dest.writeString(startDateAndTime.toString());
//        dest.writeString(description);
        /*if(isrepeating)
            dest.writeInt(1);
        else
            dest.writeInt(0);
        if(setReminder)
            dest.writeInt(1);
        else
            dest.writeInt(0);
        dest.writeByteArray(image);*/
    }

    public static final Parcelable.Creator<MyObject> CREATOR = new Parcelable.Creator<MyObject>() {
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }

        @Override
        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };
}

