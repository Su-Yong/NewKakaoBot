package com.suyong.kakaobot;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LogAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return Logger.getInstance().getLog().size();
    }

    @Override
    public Object getItem(int i) {
        return Logger.getInstance().getLog().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context ctx = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.layout_log_line, null);
        }

        TextView title = view.findViewById(R.id.log_title);
        TextView time = view.findViewById(R.id.log_date);
        TextView index = view.findViewById(R.id.log_index);
        ImageView icon = view.findViewById(R.id.log_icon);
        View color = view.findViewById(R.id.log_color);

        Logger.Log[] log = Logger.getInstance().getLog().toArray(new Logger.Log[0]);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");

        title.setText(log[i].title);
        time.setText(format.format(log[i].time));
        index.setText(log[i].index);

        switch(log[i].type) {
            case APP:
                icon.setImageResource(R.drawable.ic_android_black_24dp);
                color.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case ERROR:
                icon.setImageResource(R.drawable.ic_error_black_24dp);
                color.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case SCRIPT:
                icon.setImageResource(R.drawable.ic_assignment_black_24dp);
                color.setBackgroundColor(Color.parseColor("#607D8B"));
                break;
        }

        return view;
    }
}
