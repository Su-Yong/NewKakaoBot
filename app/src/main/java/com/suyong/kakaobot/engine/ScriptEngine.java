package com.suyong.kakaobot.engine;

import android.content.Intent;
import android.os.Process;

import com.suyong.kakaobot.KakaoManager;
import com.suyong.kakaobot.Logger;
import com.suyong.kakaobot.ScriptEngineService;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
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
    private String name = "KakaoBot";

    public ScriptEngine() {
        context = Context.enter();
        context.setOptimizationLevel(-1);
    }

    public void start() {
        try {
            script = context.compileString(source, name, 0, null);
            globalScope = context.initStandardObjects();

            ScriptUtil.init(context, globalScope);
            ScriptUI.init(context, globalScope);
            ScriptableObject.defineClass(globalScope, ScriptBot.class);
            ScriptableObject.defineClass(globalScope, ScriptUtil.class);
            ScriptableObject.defineClass(globalScope, ScriptUI.class);

            script.exec(context, globalScope);
        } catch (JavaScriptException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (EvaluatorException e) {
            KakaoManager.getInstance().receiveError(e);
        } catch (EcmaError e) {
            KakaoManager.getInstance().receiveError(e);
        }
    }

    public ScriptableObject getGlobalScope() {
        return globalScope;
    }

    public Context getContext() {
        return context;
    }

    public void stop() {
        try {
            Context.exit();
        } catch (IllegalStateException err) {}
    }

    public void setScript(String src) {
        if(src != null)
            this.source = src;
    }

    public void setName(String name) {
        if(name != null)
            this.name = name;
    }

    public void invokeFunction(String name, Object... parameters) {
        Function func = (Function) globalScope.get(name, globalScope);

        if(func != null) {
            Context.enter();
            try {
                func.call(context, globalScope, globalScope, parameters);
            } catch (EcmaError err) {
                KakaoManager.getInstance().receiveError(err);
            }
            String params = "";
            int i = 0;
            for(Object p : parameters) {
                i++;
                params += " -> " + String.valueOf(p);
                if(i != parameters.length) {
                    params += "\n";
                }
            }

            Logger.Log log = new Logger.Log();
            log.type = Logger.Type.APP;
            log.title = "call \"" + name + "\"";
            log.index = "parameters\n" + params;

            Logger.getInstance().add(log);
        }
    }
}
