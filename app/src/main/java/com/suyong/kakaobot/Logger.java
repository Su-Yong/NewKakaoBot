package com.suyong.kakaobot;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Logger {
    private static final boolean isDebug = true;
    private static final Logger ourInstance = new Logger();
    private final String logPath;

    private ArrayList<Log> list = new ArrayList<>();
    private OnLogChangedListener listener = new OnLogChangedListener() {
        @Override
        public void onChanged(Log log) {
            // Nothing
        }
    };

    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        logPath = "/log/" + format.format(new Date()) + ".log";

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
                        long time = Long.parseLong(key.split("-")[0]);
                        Type type = Type.toType(key.split("-")[1]);

                        String[] text = json.get(key).toString().split("-");
                        String title = text[0];
                        String index = "";

                        for(int i = 0; i < text.length; i++) {
                            if(i > 0) {
                                index += text[i];
                                if(i < text.length - 1) {
                                    index += "-";
                                }
                            }
                        }

                        list.add(new Log(type, title, index, time));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void add(Log log) {
        long time = System.currentTimeMillis();

        list.add(log);

        FileManager.getInstance().saveData(logPath, log.time + "-" + log.type.toString(), log.title + "-" + log.index);

        listener.onChanged(log);
    }

    public void setLogChangedListener(OnLogChangedListener listener) {
        this.listener = listener;
    }

    public void deleteLog() {
        list.clear();
    }
    public ArrayList<Log> getLog() {
        return list;
    }

    public interface OnLogChangedListener {
        void onChanged(Log log);
    }

    public static class Log {
        public Type type;
        public String title;
        public String index;
        public long time;

        public Log(Type type, String title, String index, long time) {
            time = System.currentTimeMillis();

            this.type = type;
            this.title = title;
            this.index = index;
            this.time = time;
        }

        public Log() {
            time = System.currentTimeMillis();
        }
    }

    public enum Type {
        APP("[A]"),
        ERROR("[E]"),
        SCRIPT("[S]");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Type toType(String str) {
            switch(str) {
                case "[A]":
                    return Type.APP;
                case "[E]":
                    return Type.ERROR;
                case "[S]":
                    return Type.SCRIPT;
                default:
                    return Type.APP;
            }
        }
    }
}
