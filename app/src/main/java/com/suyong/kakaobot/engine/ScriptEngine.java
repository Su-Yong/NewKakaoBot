package com.suyong.kakaobot.engine;

import android.os.Process;

import com.suyong.kakaobot.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ScriptEngine {
    public static final String NOTIFICATION_LISTENER = "catchMessage";

    private Context context;
    private ScriptableObject globalScope;
    private Script script;
    private String source = "";
    private String name = "KakaoBot:Dev";

    public ScriptEngine() {
        context = Context.enter();
        context.setOptimizationLevel(-1);
    }

    public void start() {
        script = context.compileString(source, name, 0, null);
        globalScope = context.initStandardObjects();

        try {
            ScriptableObject.defineClass(globalScope, ScriptBot.class);
            script.exec(context, globalScope);
        } catch (JavaScriptException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Context.exit();
        Process.killProcess(Process.myPid());
    }

    public void setScript(String src) {
        if(src != null)
            this.source = src;
    }

    public void invokeFunction(String name, Object... parameters) {
        Function func = (Function) globalScope.get(name, globalScope);

        if(func != null) {
            Context.enter();
            func.call(context, globalScope, globalScope, parameters);

            String params = "";
            int i = 0;
            for(Object p : parameters) {
                i++;
                params += String.valueOf(p);
                if(i != parameters.length) {
                    params += ", ";
                }
            }
            Logger.getInstance().add(Logger.Type.COMMON, "call function: " + name + ", parameters: " + params);
        }
    }
}
