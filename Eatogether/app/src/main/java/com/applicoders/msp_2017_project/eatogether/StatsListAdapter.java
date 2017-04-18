package com.applicoders.msp_2017_project.eatogether;

/**
 * Created by Koti on 17/04/2017.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatsListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final String[] loc;

    private final String[] date;


    public StatsListAdapter(Activity context, String[] itemname, String[] loc, String[] date) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.loc = loc;
        this.date = date;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_stats, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.upcomingfood);
        TextView locText = (TextView) rowView.findViewById(R.id.upcomingloc);
        TextView dateText = (TextView) rowView.findViewById(R.id.upcomingdate);
        txtTitle.setText(itemname[position]);
        locText.setText(loc[position]);
        dateText.setText(date[position]);
        return rowView;

    };

}
