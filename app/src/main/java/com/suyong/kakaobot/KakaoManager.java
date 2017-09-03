package com.suyong.kakaobot;

import android.app.Notification;
import android.util.Log;

import com.suyong.kakaobot.engine.ScriptEngine;

import org.mozilla.javascript.EcmaError;

import java.util.ArrayList;

public class KakaoManager {
    private static final KakaoManager ourInstance = new KakaoManager();

    private static ArrayList<KakaoData> dataList = new ArrayList<>();
    private static ScriptEngine engine;
    private static boolean isOn = false;

    public static boolean isForeground = false;
    public static KakaoManager getInstance() {
        return ourInstance;
    }

    private KakaoManager() {}

    public void start() {
        if(engine == null) {
            reload();
        }
    }

    public void reload() {
        try {
            String script = FileManager.getInstance().readScript();

            engine = new ScriptEngine();
            engine.setScript(script);
            engine.start();
        } catch(EcmaError err) {

        }
    }

    public void stop() {
        engine.stop();

    }

    public void addKakaoData(KakaoData data) {
        dataList.add(data);

        engine.invokeFunction(ScriptEngine.NOTIFICATION_LISTENER, new Object[] { data.room, data.sender, data.message});
    }

    public ArrayList<KakaoData> getDataList() {
        return dataList;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public static class KakaoData {
        public String sender = "";
        public String room = "";
        public String message = "";

        public Notification.Action session;
    }
}
