package com.dudeonfireandCO.vipul.mytodolist;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vipul.mytodolist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter {

    private int resource;
    private List<MyObject> tasks;
    private LayoutInflater inflater;
    private Context context;
    //private ArrayList<String> taskName;
    //private int colorValues[] = {Color.rgb(32,47,223),Color.rgb(164,164,164)};

    public TaskAdapter(Context context, int resource, List<MyObject> objects) {
        super(context, resource, objects);
        this.resource = resource;
        tasks = objects;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(resource,null);
        }
        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout);
        if(position%2==0)
            layout.setBackgroundResource(R.drawable.listview_style1);
        else
            layout.setBackgroundResource(R.drawable.listview_style2);
        TextView nameText = (TextView)convertView.findViewById(R.id.text1);
        //TextView priortext = (TextView)convertView.findViewById(R.id.text2);
        final TextView finishText = (TextView)convertView.findViewById(R.id.finish_text);
        CheckBox finishbutton = (CheckBox)convertView.findViewById(R.id.finish_checkbox);
        Typeface comfortaa = Typeface.createFromAsset(context.getAssets(),"fonts/Comfortaa_Bold.ttf");
        nameText.setTypeface(comfortaa);
        nameText.setText(tasks.get(position).getTask());
        if((new Date().getTime()-tasks.get(position).getEndDateAndTime().getTime())>=0) {
            nameText.setPaintFlags(nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            finishbutton.setChecked(true);
            finishbutton.setEnabled(false);
            finishText.setText("Task finished on " + tasks.get(position).getEndDateAndTime().toString());
        }
        else
            finishText.setText("");
        finishbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Calendar c = Calendar.getInstance();
                    finishText.setText("Task finished on " + formatter.format(c.getTime()));
                }
                else{
                    finishText.setText("");
                }
            }
        });
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
