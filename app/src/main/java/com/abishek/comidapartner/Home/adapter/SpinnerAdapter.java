package com.abishek.comidapartner.Home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.abishek.comidapartner.R;

import java.util.List;


public class SpinnerAdapter extends ArrayAdapter<String> {

    Context context;
    int resource;
    List<String> objects;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(R.layout.spinner_layout_custom_dropdown,parent,false);
        textView.setText(objects.get(position));

        return textView;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) inflater.inflate(R.layout.spinner_layout_custom, parent, false);
        textView.setText(objects.get(position));

        if(position == 0) textView.setTextColor(context.getColor(R.color.black4));
        else textView.setTextColor(context.getColor(R.color.black));

        return textView;
    }
}
