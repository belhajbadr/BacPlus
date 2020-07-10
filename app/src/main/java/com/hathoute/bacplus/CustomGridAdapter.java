package com.hathoute.bacplus;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomGridAdapter extends BaseAdapter {

    private List<Subject> lSubjects;
    private Context context;
    private LayoutInflater thisInflater;

    public CustomGridAdapter(Context context, List<Subject> lSubjects) {
        this.context = context;
        this.thisInflater = LayoutInflater.from(context);
        this.lSubjects = lSubjects;
    }

    @Override
    public int getCount() {
        return lSubjects.size();
    }

    @Override
    public Object getItem(int position) {
        return lSubjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Subject)getItem(position)).getSubject();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = thisInflater.inflate( R.layout.grid_item_arabic, parent, false );
        }

        TextView tv = convertView.findViewById(R.id.text);
        AppCompatImageView iv = convertView.findViewById(R.id.picture);

        tv.setText( lSubjects.get(position).getTitle() );
        iv.setImageResource( lSubjects.get(position).getDrawableId() );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            convertView.setElevation(10);

        return convertView;
    }
}
