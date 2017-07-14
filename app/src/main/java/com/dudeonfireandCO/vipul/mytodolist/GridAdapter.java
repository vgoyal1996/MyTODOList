package com.dudeonfireandCO.vipul.mytodolist;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vipul.mytodolist.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridAdapter extends ArrayAdapter {

    private int resource;
    private List<MyObject> tasks;
    private LayoutInflater inflater;
    private Context context;


    public GridAdapter(Context context, int resource, List<MyObject> objects) {
        super(context, resource, objects);
        this.resource = resource;
        tasks = objects;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(resource, null);
        }
        TextView taskname = (TextView)convertView.findViewById(R.id.grid_taskname);
        TextView createDate = (TextView)convertView.findViewById(R.id.grid_createdate);
        TextView endDate = (TextView)convertView.findViewById(R.id.grid_enddate);
        ImageView taskImage = (ImageView)convertView.findViewById(R.id.grid_taskimage);
        LinearLayout l = (LinearLayout)convertView.findViewById(R.id.grid_linear_layout);

        if(tasks.get(position).getPrior()==1)
            taskname.setBackgroundColor(Color.rgb(1, 223, 1));
        else if(tasks.get(position).getPrior()==2)
            taskname.setBackgroundColor(Color.rgb(255,255,255));
        else if(tasks.get(position).getPrior()==3)
            taskname.setBackgroundColor(Color.rgb(254,46,46));
        Typeface comfortaa = Typeface.createFromAsset(context.getAssets(),"fonts/Comfortaa_Bold.ttf");
        taskname.setTypeface(comfortaa);
        taskname.setText(tasks.get(position).getTask());
        createDate.setText("Creation Date:"+tasks.get(position).getStartDate());
        endDate.setText("End Date:"+tasks.get(position).getEndDate());
        if((new Date().getTime()-tasks.get(position).getEndDateAndTime().getTime())>=0)
            taskname.setPaintFlags(taskname.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        if(tasks.get(position).getBitmap()!=null)
            taskImage.setImageBitmap(tasks.get(position).getBitmap());
        else{
            taskImage.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public ArrayList<MyObject> filter(String text,ArrayList<MyObject> names){
        text = text.toLowerCase(Locale.getDefault());
        if(text.length()!=0) {
            for (MyObject o : tasks) {
                if(o.getTask().contains(text)){
                    names.add(o);
                }
            }
        }
        return names;
    }

}
