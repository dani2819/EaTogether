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
    private final String[] desc;

    public CustomListAdapter(Activity context, String[] itemname, String[] loc, String[] desc) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.loc = loc;
        this.desc = desc;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        TextView description = (TextView) rowView.findViewById(R.id.textView2);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(R.drawable.pic1);
        extratxt.setText(loc[position]);
        description.setText(desc[position]);
        return rowView;

    };
}