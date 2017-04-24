package com.applicoders.msp_2017_project.eatogether;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Koti on 24/04/2017.
 */

public class FragListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final String[] guest;
    private final String[] date;

    public FragListAdapter(Activity context, String[] itemname, String[] guest, String[] date) {
        super(context, R.layout.list_item_stats, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.guest = guest;
        this.date = date;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_stats, null,true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.upcomingfood);
        TextView dateText = (TextView) rowView.findViewById(R.id.upcomingdate);
        TextView locText = (TextView) rowView.findViewById(R.id.upcomingloc);
        txtTitle.setText(itemname[position]);
        dateText.setText(date[position]);
        locText.setText(guest[position]);
        return rowView;

    };
}
