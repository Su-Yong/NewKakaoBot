package com.suyong.kakaobot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DebugChatAdapter extends BaseAdapter {
    private ArrayList<ChatData> list = new ArrayList<>();

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context ctx = viewGroup.getContext();
        int type = getItemViewType(i);

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch(type) {
                case 0:
                    view = inflater.inflate(R.layout.chat_person, null);
                    break;
                case 1:
                    view = inflater.inflate(R.layout.chat_bot, null);
                    break;
            }
        }

        TextView text = view.findViewById(R.id.chat_text);
        text.setText(list.get(i).text);

        return view;
    }

    @Override
    public int getItemViewType(int p) {
        return list.get(p).isPerson ? 0 : 1;
    }

    public void addBotChat(String message) {
        ChatData data = new ChatData();
        data.isPerson = false;
        data.text = message;

        list.add(data);

        this.notifyDataSetChanged();

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.APP;
        log.title = "Debuging Chat: BOT";
        log.index = message;

        Logger.getInstance().add(log);
    }

    public void addPersonChat(String message) {
        ChatData data = new ChatData();
        data.isPerson = true;
        data.text = message;

        list.add(data);

        this.notifyDataSetChanged();

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.APP;
        log.title = "Debuging Chat: PERSON";
        log.index = message;

        Logger.getInstance().add(log);
    }

    private class ChatData {
        public boolean isPerson = false;
        public String text = "";
    }
}
