package com.suyong.kakaobot.engine;

import com.suyong.kakaobot.DebugChatAdapter;
import com.suyong.kakaobot.KakaoManager;
import com.suyong.kakaobot.KakaoTalkListener;
import com.suyong.kakaobot.MainActivity;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class ScriptBot extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Bot";
    }

    @JSStaticFunction
    public static void send(String room, String message) {
        try {
            KakaoTalkListener.send(room, message);
        } catch(IllegalArgumentException err) {
            if(KakaoManager.getInstance().isForeground) {
                MainActivity.adapter.addBotChat(message);
            }
        }
    }
}
