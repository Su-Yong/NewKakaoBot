package com.suyong.kakaobot;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.suyong.kakaobot.engine.ScriptEngine;

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.RhinoException;

import java.util.ArrayList;

public class KakaoManager {
    private static final KakaoManager ourInstance = new KakaoManager();

    private static ArrayList<KakaoData> dataList = new ArrayList<>();

    public static boolean isForeground = false;
    public static KakaoManager getInstance() {
        return ourInstance;
    }
    public static Context ctx;

    private KakaoManager() {}

    public void setContext(Context context) {
        this.ctx = context;
    }

    public void start() {
        Intent intent = new Intent(ctx, ScriptEngineService.class);

        ctx.startService(intent);
    }

    public void reload() {
        Intent intent = new Intent(ctx, ScriptEngineService.class);

        ctx.stopService(intent);
        ctx.startService(intent);
    }

    public void stop() {
        Intent intent = new Intent(ctx, ScriptEngineService.class);

        ctx.stopService(intent);
    }

    public boolean isRunning() {
        try {
            ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (ScriptEngineService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Throwable err) {}

        return false;
    }

    public void receiveError(final EcmaError err) {
        if(isForeground) {
            MainActivity.UIThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.settingPower.setChecked(false);
                    Toast.makeText(ctx, "Error: " + err.getName() + " (" + err.lineNumber() + ", " + err.columnNumber() + ")\n" + err.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.ERROR;
        log.title = err.getName();
        log.index = "at (" + err.lineNumber() + ", " + err.columnNumber() + ")\n" + err.getErrorMessage();

        Logger.getInstance().add(log);
    }

    public void receiveError(final EvaluatorException err) {
        if(isForeground) {
            MainActivity.UIThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.settingPower.setChecked(false);
                    Toast.makeText(ctx, "Error: EvaluatorException (" + err.lineNumber() + ", " + err.columnNumber() + ")\n" + err.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.ERROR;
        log.title = "Error: EvaluatorException";
        log.index = "at (" + err.lineNumber() + ", " + err.columnNumber() + ")\n" + err.toString();

        Logger.getInstance().add(log);
    }

    public void addKakaoData(KakaoData data) {
        dataList.add(data);

        if(isRunning())
            ScriptEngineService.getEngine().invokeFunction(ScriptEngine.NOTIFICATION_LISTENER, new Object[]{data.room, data.sender, data.message, data.session});

    }

    public ArrayList<KakaoData> getDataList() {
        return dataList;
    }

    public static class KakaoData {
        public String sender = "";
        public String room = "";
        public String message = "";

        public Notification.Action session;
    }
}
