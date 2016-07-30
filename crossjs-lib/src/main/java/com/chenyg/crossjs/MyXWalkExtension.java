package com.chenyg.crossjs;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkExtension;

import java.util.*;

/**
 * Created by 宇宙之灵 on 2016/6/30.
 */
class MyXWalkExtension extends XWalkExtension implements WebBridge
{
    private JsCallJava jsCallJava;
    private Map<Integer, Map<String, JsReturn>> jsReturnMap = new HashMap<>();
    private boolean isDisabled = false;
    private Set<String> injectsNames = new HashSet<String>();
    private IInjectHandle iInjectHandle;

    private Map<String, String> dynamics = new HashMap<>();
    private Set<String> disables = new HashSet<>();

    public MyXWalkExtension(String topName, JsCallJava jsCallJava, InjectObj[] injectObjs)
    {
        super(topName, jsCallJava.getPreloadInterfaceJS());
        this.jsCallJava = jsCallJava;
        for (int i = 0; i < injectObjs.length; i++)
        {
            injectsNames.add(injectObjs[i].namespace);
        }
    }

    public synchronized void addDynamicInjectObj(InjectObj injectObj)
    {
        try
        {
            dynamics.put(injectObj.namespace, jsCallJava.dynamicInjectOne(injectObj));
        } catch (Exception e)
        {
            if (jsCallJava.willPrintDebugInfo())
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void removeDynamicInjectObj(String namespace)
    {
        dynamics.remove(namespace);
    }

    /**
     * 清除所有禁用的接口。
     */
    public synchronized void clearAllDisables(){
        disables.clear();
    }

    /**
     * 清除所有动态注入的接口。
     */
    public synchronized void clearAllDynamics(){
        dynamics.clear();
    }

    /**
     * 禁用或启用指定的接口。
     *
     * @param namespace
     * @param isDisable
     */
    public synchronized void disableInjectObj(String namespace, boolean isDisable)
    {
        if (isDisable)
        {
            disables.add(namespace);
        } else
        {
            disables.remove(namespace);
        }
    }

    public synchronized void setIInjectHandle(IInjectHandle iInjectHandle)
    {
        this.iInjectHandle = iInjectHandle;
    }

    private synchronized IInjectHandle getIInjectHandle()
    {
        return iInjectHandle == null ? IInjectHandle.ENABLE_ALL_HANDLE : iInjectHandle;
    }

    /**
     * 整体启用或禁用注入的接口。
     *
     * @param isDisabled
     */
    public synchronized void disable(boolean isDisabled)
    {
        this.isDisabled = isDisabled;
    }

    @Override
    public synchronized void onMessage(int instanceId, String jsonStr)
    {
        if (isDisabled)
        {
            return;
        }
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
    public synchronized String onSyncMessage(int instanceId, String jsonStr)
    {
        if (isDisabled)
        {
            return null;
        }

        try
        {
            JSONObject jsonObject = new JSONObject(jsonStr);
            switch (jsonObject.getString("type"))
            {
                case "bridge":
                    return jsCallJava.call(this, instanceId, jsonObject.getString("value"));
                case "enables":
                    return getEnables(jsonObject.getString("value"));
                default:
                    return null;
            }
        } catch (JSONException e)
        {
            if (jsCallJava.willPrintDebugInfo())
            {
                e.printStackTrace();
            }
            return null;
        }


    }


    private synchronized String getEnables(String url)
    {


        final JSONObject enables = new JSONObject();

        IInjectHandle iInjectHandle = getIInjectHandle();
        iInjectHandle.beforeInject(url, new IInjectHandle.IDeal()
        {
            @Override
            public void enableInterface(String... interfaceNamespaces)
            {
                Arrays.sort(interfaceNamespaces);
                try
                {
                    JSONObject injects = new JSONObject();
                    enables.put("injects", injects);
                    Iterator<String> namespaces = injectsNames.iterator();
                    while (namespaces.hasNext())
                    {
                        String ns = namespaces.next();
                        if (Arrays.binarySearch(interfaceNamespaces, ns) >= 0&&!disables.contains(ns))
                        {
                            injects.put(ns, true);
                        }
                    }

                    JSONArray _dynamics = new JSONArray();
                    enables.put("dynamics", _dynamics);
                    namespaces = dynamics.keySet().iterator();
                    while (namespaces.hasNext())
                    {
                        String ns = namespaces.next();
                        if (Arrays.binarySearch(interfaceNamespaces, ns) >= 0&&!disables.contains(ns))
                        {
                            _dynamics.put(dynamics.get(ns));
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void disableInterface(String... interfaceNamespaces)
            {
                Arrays.sort(interfaceNamespaces);
                try
                {
                    JSONObject injects = new JSONObject();
                    enables.put("injects", injects);
                    Iterator<String> namespaces = injectsNames.iterator();
                    while (namespaces.hasNext())
                    {
                        String ns = namespaces.next();
                        if (Arrays.binarySearch(interfaceNamespaces, ns) < 0&&!disables.contains(ns))
                        {
                            injects.put(ns, true);
                        }
                    }

                    JSONArray _dynamics = new JSONArray();
                    enables.put("dynamics", _dynamics);
                    namespaces = dynamics.keySet().iterator();
                    while (namespaces.hasNext())
                    {
                        String ns = namespaces.next();
                        if (Arrays.binarySearch(interfaceNamespaces, ns) < 0&&!disables.contains(ns))
                        {
                            _dynamics.put(dynamics.get(ns));
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        return enables.toString();
    }

    @Override
    public synchronized JsReturn invoke(String jsCallbackId, int instanceId, String content)
    {

        if (isDisabled)
        {
            return null;
        }

        JsReturn jsReturn = null;


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
        postMessage(instanceId, content);
        return jsReturn;
    }

    @Override
    public synchronized void onInstanceCreated(int instanceID)
    {
        super.onInstanceCreated(instanceID);
        if (jsCallJava.willPrintDebugInfo())
        {
            Log.d(getClass().getName(), "onInstanceCreated:" + instanceID);
        }
    }

    @Override
    public synchronized void onInstanceDestroyed(int instanceID)
    {
        super.onInstanceDestroyed(instanceID);
        jsReturnMap.remove(instanceID);
        JavaFunction.removeAllByInstanceId(instanceID);
        if (jsCallJava.willPrintDebugInfo())
        {
            Log.d(getClass().getName(), "onInstanceDestroyed:" + instanceID);
        }
    }
}
