package com.suyong.kakaobot;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Logger {
    private static final boolean isDebug = true;
    private static final Logger ourInstance = new Logger();
    private final String logPath;

    private ArrayList<String> log = new ArrayList<>();
    private OnLogChangedListener listener = new OnLogChangedListener() {
        @Override
        public void onChanged(Type type, String log) {
            // Nothing
        }
    };


    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
        logPath = "/log/" + System.currentTimeMillis() + ".log";

        File file = new File(FileManager.KAKAOBOT_HOME, logPath);
        file.getParentFile().mkdir();
        FileManager.getInstance().createFile(logPath);
    }

    public void init() {
        File file = new File(FileManager.KAKAOBOT_HOME, logPath).getParentFile();
        try {
            for (File f : file.listFiles()) {
                try {
                    JSONObject json = new JSONObject((String) FileManager.getInstance().read(f.toString()));

                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        log.add(key + json.get(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void add(Type type, String str) {
        long time = System.currentTimeMillis();

        log.add(time + " " + type.toString() + str);

        FileManager.getInstance().saveData(logPath, time + " " + type.toString() , str);

        listener.onChanged(type, str);

        if(isDebug) {
            Log.d("KakaoBot: LOG", type.toString() + "\n" + str);
        }
    }

    public void setLogChangedListener(OnLogChangedListener listener) {
        this.listener = listener;
    }

    public ArrayList<String> getLog() {
        return log;
    }

    public interface OnLogChangedListener {
        void onChanged(Type type, String log);
    }

    public enum Type {
        COMMON("[C]"),
        DEBUG("[D]"),
        APP("[A]"),
        ERROR("[E]"),
        WARNING("[W]");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
