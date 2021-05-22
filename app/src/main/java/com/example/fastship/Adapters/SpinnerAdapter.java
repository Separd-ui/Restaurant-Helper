package com.example.fastship.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fastship.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] payList;
    private int[] images;
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects,int[] images) {
        super(context, resource, objects);
        this.context=context;
        payList=objects;
        this.images=images;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        View row=LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false);
        TextView title=row.findViewById(R.id.row_title);
        ImageView imageView=row.findViewById(R.id.row_image);
        title.setText(payList[position]);
        imageView.setImageResource(images[position]);
        return row;
    }
}
