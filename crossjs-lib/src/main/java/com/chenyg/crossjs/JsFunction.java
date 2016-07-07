/**
 * Summary: 异步回调页面JS函数管理对象
 * Version 1.0
 * Date: 13-11-26
 * Time: 下午7:55
 * Copyright: Copyright (c) 2013
 */

package com.chenyg.crossjs;


import android.util.Log;
import org.json.JSONArray;

/**
 * 用于java端调用js。
 */
public class JsFunction
{

    private static final String CALLBACK_JS_FORMAT = "javascript:%s.__callback__('%s',%d,%s);";
    private static final String DESTROY_JS_FORMAT = "javascript:%s.__destroy__('%s');";


    private boolean couldGoOn;

    private boolean isPermanent = false;
    private String namespace;
    WebBridge webBridge;
    int instanceId;
    String id;
    boolean isDebug;


    JsFunction(WebBridge webBridge, String topName, String namespace, String id,int instanceId,boolean isDebug)
    {
        couldGoOn = true;
        this.webBridge = webBridge;
        this.instanceId=instanceId;
        this.isDebug=isDebug;
        this.namespace = topName + "." + namespace;
        this.id = id.startsWith(InjectObj.JS_FUNCTION_STARTS) ? id
                .substring(InjectObj.JS_FUNCTION_STARTS.length()) : id;
    }

    public boolean isPermanent()
    {
        return isPermanent;
    }

    public void setPermanent(boolean permanent)
    {
        isPermanent = permanent;
    }

    public void destroy() throws JsCallbackException
    {
        String execJs = String.format(DESTROY_JS_FORMAT, namespace, id);
        webBridge.invoke(null,instanceId,execJs);
    }

    public JsReturn apply(Object... args) throws JsCallbackException
    {
        if (!couldGoOn)
        {
            throw new JsCallbackException("the JsFunction isn't permanent,cannot be called more than once");
        }
        JSONArray params = new JSONArray();
        for (Object arg : args)
        {
            params.put(arg);
        }

        String execJs = String.format(CALLBACK_JS_FORMAT, namespace, id, isPermanent() ? 1 : 0, params.toString());
        couldGoOn = isPermanent();
        if(!couldGoOn){
            if(isDebug){
                Log.d(getClass().getName(),"will destroy this instance(id="+id+")");
            }
        }
      return  webBridge.invoke(id,instanceId,execJs);
    }


    public static class JsCallbackException extends Exception
    {
        public JsCallbackException(String msg)
        {
            super(msg);
        }
    }

    public static void tryApply(JsFunction jsFunction, Object... args)
    {
        if (jsFunction != null)
        {
            try
            {
                jsFunction.apply(args);
            } catch (JsCallbackException e)
            {
                e.printStackTrace();
            }
        }
    }
}
