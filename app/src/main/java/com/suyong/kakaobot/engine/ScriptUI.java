package com.suyong.kakaobot.engine;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.suyong.kakaobot.DebugChatAdapter;
import com.suyong.kakaobot.FileManager;
import com.suyong.kakaobot.KakaoManager;
import com.suyong.kakaobot.KakaoTalkListener;
import com.suyong.kakaobot.MainActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class ScriptUI extends ScriptableObject {
    private static ScriptableObject scope;
    private static Context context;

    public static void init(Context ctx, ScriptableObject obj) {
        scope = obj;
        context = ctx;
    }
    public ScriptUI() {}

    @Override
    public String getClassName() {
        return "UI";
    }

    @JSStaticFunction
    public static void showDialog(final String title, final String content, final Function acceptFunc, final Function denyFunc) {
        MainActivity.UIThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.context).create();
                dialog.setTitle(title);
                dialog.setMessage(content);

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceptFunc.call(context, scope, scope, new Object[] {});
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        denyFunc.call(context, scope, scope, new Object[] {});
                    }
                });
                dialog.show();
            }
        });
    }

    @JSStaticFunction
    public static void showToast(final String text) {
        MainActivity.UIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JSStaticFunction
    public static Object getContext() {
        return MainActivity.context;
    }

    @JSStaticFunction
    public static void Thread(final Function func) {
        MainActivity.UIThread(new Runnable() {
            @Override
            public void run() {
                func.call(context, scope, scope, new Object[] {});
            }
        });
    }
}
