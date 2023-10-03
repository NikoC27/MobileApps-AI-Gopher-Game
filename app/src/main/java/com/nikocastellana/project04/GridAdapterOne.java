package com.nikocastellana.project04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class GridAdapterOne extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private int[] indexes;
    private String moveNum;

    public GridAdapterOne(Context c, int[] indexes, String move){
        this.mContext = c;
        this.indexes = indexes;
        this.moveNum = move;
    }

    @Override
    public int getCount() {
        return indexes.length;
    }

    @Override
    public Object getItem(int position) {
        return indexes[position];
    }

    @Override
    public long getItemId(int position) {
        return indexes[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Initialize inflater
        if(inflater == null){
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Initialize convert view
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item, null);
        }

        TextView textView = convertView.findViewById(R.id.index);
        textView.setText(String.valueOf(indexes[position]));
        return convertView;
    }
}
