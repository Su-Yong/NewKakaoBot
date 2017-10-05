package com.suyong.kakaobot;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.suyong.kakaobot.engine.ScriptEngine;

public class ScriptEngineService extends Service {
    private static final int RUNNING_FLAG = 2000;
    private static ScriptEngine engine;

    public static boolean isStart = false;

    public ScriptEngineService() {
        super();
    }

    public static ScriptEngine getEngine() {
        return engine;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);

        engine = new ScriptEngine();
        engine.setName("KakaoBot:Dev");
        engine.setScript(FileManager.getInstance().readScript());
        engine.start();

        isStart = true;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_debug_white_24dp);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.running));
        builder.setOngoing(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(RUNNING_FLAG, builder.build());

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.APP;
        log.title = "Turn on the KakaoBot";
        log.index = "turned on";

        Logger.getInstance().add(log);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isStart = false;

        engine.stop();
        engine = null;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(RUNNING_FLAG);

        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.APP;
        log.title = "Turn off the KakaoBot";
        log.index = "turned off";

        Logger.getInstance().add(log);

        super.onDestroy();
    }
}
