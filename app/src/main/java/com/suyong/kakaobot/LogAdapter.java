package com.suyong.kakaobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

        TextView text = view.findViewById(R.id.log_text);
        text.setText(Logger.getInstance().getLog().get(i));

        return view;
    }
}
