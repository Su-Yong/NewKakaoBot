package com.suyong.kakaobot.engine;

import com.suyong.kakaobot.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.IOException;

public class ScriptUtil extends ScriptableObject {
    private static ScriptableObject scope;
    private static Context context;

    public ScriptUtil() {}

    public static void init(Context ctx, ScriptableObject obj) {
        scope = obj;
        context = ctx;
    }

    @Override
    public String getClassName() {
        return "Util";
    }

    @JSStaticFunction
    public static void parseToHtml(final String url, final String option, final Function func) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect(url).get();
                    Elements element = document.select(option);

                    func.call(context, scope, scope, new Object[] { element.html(), null });
                } catch (IOException e) {
                    try {
                        func.call(context, scope, scope, new Object[] { null, e});
                    } catch (Exception err) {}
                }
            }
        }).start();
    }

    @JSStaticFunction
    public static void parseToText(final String url, final String option, final Function func) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect(url).get();
                    Elements element = document.select(option);

                    func.call(context, scope, scope, new Object[] { element.text(), null });
                } catch (IOException e) {
                    try {
                        func.call(context, scope, scope, new Object[] { null, e });
                    } catch (Exception err) {}
                }
            }
        }).start();
    }

    @JSStaticFunction
    public static void delay(final Function func, final int ms) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ms);
                    func.call(context, scope, scope, new Object[] { true });
                } catch (InterruptedException e) {
                    func.call(context, scope, scope, new Object[] { false });
                }
            }
        }).start();
    }

    @JSStaticFunction
    public static void log(String title, String message) {
        Logger.Log log = new Logger.Log();
        log.type = Logger.Type.SCRIPT;
        log.title = title;
        log.index = message;

        Logger.getInstance().add(log);
    }
}
