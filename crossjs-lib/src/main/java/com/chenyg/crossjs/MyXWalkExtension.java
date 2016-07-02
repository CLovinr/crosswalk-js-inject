package com.chenyg.crossjs;

import android.util.Log;
import org.json.JSONObject;
import org.xwalk.core.XWalkExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 宇宙之灵 on 2016/6/30.
 */
class MyXWalkExtension extends XWalkExtension implements WebBridge
{
    private JsCallJava jsCallJava;
    private Map<Integer, Map<String, JsReturn>> jsReturnMap = Collections
            .synchronizedMap(new HashMap<Integer, Map<String, JsReturn>>());

    public MyXWalkExtension(String name, JsCallJava jsCallJava)
    {
        super(name, jsCallJava.getPreloadInterfaceJS());
        this.jsCallJava = jsCallJava;
    }

    @Override
    public void onMessage(int instanceId, String jsonStr)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String jsCallbackId = jsonObject.getString("id");
            String returnValue = jsonObject.optString("return", null);
            if (jsCallJava.willPrintDebugInfo())
            {
                Log.d(getClass().getName(), jsonStr);
            }
            synchronized (jsReturnMap)
            {
                Map<String, JsReturn> map = jsReturnMap.get(instanceId);
                if (map != null && map.containsKey(jsCallbackId))
                {
                    JsReturn.JsReturnImpl jsReturn = (JsReturn.JsReturnImpl) map.get(jsCallbackId);
                    if (jsReturn.listener != null)
                    {
                        jsReturn.listener.onGetString(returnValue);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String onSyncMessage(int instanceId, String jsonStr)
    {
        return jsCallJava.call(this, instanceId, jsonStr);
    }

    @Override
    public JsReturn invoke(String jsCallbackId, int instanceId, String content)
    {
        JsReturn jsReturn = null;
        postMessage(instanceId, content);

        if (jsCallbackId != null)
        {
            synchronized (jsReturnMap)
            {
                Map<String, JsReturn> map = jsReturnMap.get(instanceId);
                if (map == null)
                {
                    map = new HashMap<>();
                    jsReturnMap.put(instanceId, map);
                }
                jsReturn = new JsReturn.JsReturnImpl();
                map.put(jsCallbackId, jsReturn);
            }
        }

        return jsReturn;
    }

    @Override
    public void onInstanceCreated(int instanceID)
    {
        super.onInstanceCreated(instanceID);
        if (jsCallJava.willPrintDebugInfo())
        {
            Log.d(getClass().getName(), "onInstanceCreated:" + instanceID);
        }
    }

    @Override
    public void onInstanceDestroyed(int instanceID)
    {
        super.onInstanceDestroyed(instanceID);
        jsReturnMap.remove(instanceID);
        Java2JsCallback.removeAllByInstanceId(instanceID);
        if (jsCallJava.willPrintDebugInfo())
        {
            Log.d(getClass().getName(), "onInstanceDestroyed:" + instanceID);
        }
    }
}
