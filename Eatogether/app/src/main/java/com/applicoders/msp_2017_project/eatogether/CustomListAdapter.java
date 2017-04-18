package com.applicoders.msp_2017_project.eatogether;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] loc;
    private final String[] guest;
    private final String[] date;


    public CustomListAdapter(Activity context, String[] itemname, String[] loc, String[] guest, String[] date) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.loc = loc;
        this.guest = guest;
        this.date = date;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView locText = (TextView) rowView.findViewById(R.id.textView1);
        TextView numOfGuestText = (TextView) rowView.findViewById(R.id.noofguest);
        TextView dateText = (TextView) rowView.findViewById(R.id.date);
        TextView timeText = (TextView) rowView.findViewById(R.id.time);
        txtTitle.setText(itemname[position]);
        imageView.setImageResource(R.drawable.pic1);
        locText.setText(loc[position]);
        numOfGuestText.setText(guest[position]+" slots available");
        dateText.setText(date[position]);
        timeText.setText("10:20:00");
        return rowView;

    };
}