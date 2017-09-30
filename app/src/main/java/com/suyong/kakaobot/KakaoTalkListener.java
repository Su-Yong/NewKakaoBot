package com.suyong.kakaobot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;

import com.suyong.kakaobot.engine.ScriptEngine;
import com.suyong.kakaobot.KakaoManager.KakaoData;

import java.util.ArrayList;

public class KakaoTalkListener extends NotificationListenerService {
    private final static String KAKAO_TALK = "com.kakao.talk";

    private static Context context;

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        super.onNotificationPosted(statusBarNotification);

        if(statusBarNotification.getPackageName().equals(KAKAO_TALK)) {
            Notification.WearableExtender extender = new Notification.WearableExtender(statusBarNotification.getNotification());

            for(Notification.Action act : extender.getActions()) {
                if(act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
                    context = getApplicationContext();

                    Object title = null;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title = statusBarNotification.getNotification().extras.getString("android.summaryText");
                    } else {
                        title = statusBarNotification.getNotification().extras.getString("android.title");
                    }
                    Object text = statusBarNotification.getNotification().extras.get("android.text");

                    KakaoData data = getKakaoData(title.toString(), text);
                    data.session = act;

                    KakaoManager.getInstance().addKakaoData(data);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    public static void send(String room, String message) throws IllegalArgumentException { // @author ManDongI
        Notification.Action session = null;

        for(KakaoData data : KakaoManager.getInstance().getDataList().toArray(new KakaoData[0])) {
            if(data.room.equals(room)) {
                session = data.session;

                break;
            }
        }

        if(session == null) {
            throw new IllegalArgumentException("Can't find the room");
        }

        Intent sendIntent = new Intent();
        Bundle msg = new Bundle();
        for (RemoteInput inputable : session.getRemoteInputs()) msg.putCharSequence(inputable.getResultKey(), message);
        RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);

        try {
            session.actionIntent.send(context, 0, sendIntent);

            Logger.Log log = new Logger.Log();
            log.type = Logger.Type.APP;
            log.title = "send message";
            log.index = "room: " + room +"\nmessage: " + message;

            Logger.getInstance().add(log);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private KakaoData getKakaoData(String room, Object text) {
        KakaoData result = new KakaoData();

        result.room = room;

        if(text instanceof String) {
            result.sender = room;
            result.message = text.toString();
        } else {
            String html = Html.toHtml((SpannableString) text);
            result.sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
            result.message = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
        }

        return result;
    }
}
