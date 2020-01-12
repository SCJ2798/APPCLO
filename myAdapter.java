package com.project.appclo.dataentryapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class myAdapter extends BaseAdapter {

    List<String> list;
   Activity activity;
    LayoutInflater inflater;

    public myAdapter(List<String> list, Activity activity) {
        this.list = list;
        this.activity = activity;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.spinner_colour_layout,parent,false);
        }

        TextView textItem = (TextView) convertView.findViewById(R.id.text_items_color_sp);
        textItem.setText(list.get(position));

        return  convertView;
    }
}
