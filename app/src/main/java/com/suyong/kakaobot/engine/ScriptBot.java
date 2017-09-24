package com.suyong.kakaobot.engine;

import com.suyong.kakaobot.DebugChatAdapter;
import com.suyong.kakaobot.FileManager;
import com.suyong.kakaobot.KakaoManager;
import com.suyong.kakaobot.KakaoTalkListener;
import com.suyong.kakaobot.MainActivity;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class ScriptBot extends ScriptableObject {
    private static final String DATA = "data/script.data";

    public ScriptBot() {}

    @Override
    public String getClassName() {
        return "Bot";
    }

    @JSStaticFunction
    public static void send(String room, final String message) {
        try {
            KakaoTalkListener.send(room, message);
        } catch(IllegalArgumentException err) {
            if(KakaoManager.getInstance().isForeground) {
                MainActivity.UIThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.adapter.addBotChat(message);
                    }
                });
            }
        }
    }

    @JSStaticFunction
    public static void saveData(String key, String value) {
        FileManager.getInstance().saveData(DATA, key, value);
    }

    @JSStaticFunction
    public static Object readData(String key) {
        return FileManager.getInstance().readData(DATA, key);
    }
}
