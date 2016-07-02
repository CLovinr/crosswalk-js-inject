package com.chenyg.crossjs;


import android.util.Log;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 用于向js端动态传递java函数。使用注解{@linkplain Callback}标记要注入的函数.
 */
public abstract class Java2JsCallback extends JsCallback
{

    public static class Builder
    {
        int instanceId;
        boolean isDebug;

        Builder(int instanceId, boolean isDebug)
        {
            this.instanceId = instanceId;
            this.isDebug = isDebug;
        }
    }

    /**
     * 用于标记回调函数(public、非static),函数的名称没有被使用。
     * 标记的函数可以有返回值，返回值类型同{@linkplain JsInterface}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    public @interface Callback
    {
        /**
         * 是否在第一个参数前加入{@linkplain com.chenyg.crossjs.Java2JsCallback.Builder}.默认为false.
         * @return
         */
        boolean needBuilder()default false;
    }

    public static final String JAVA_CALLBACK = "[/JavaCallback/]=";

    private static Map<Integer, Map<String, Java2JsCallback>> instanceIdMap = Collections
            .synchronizedMap(new HashMap<Integer, Map<String, Java2JsCallback>>());
    private HashMap<String, JsCallJava.MethodClass> methodsMap;

    static void removeAllByInstanceId(int instanceId)
    {
        instanceIdMap.remove(instanceId);
    }

    /**
     * 根据id移除
     *
     * @param callbackId
     * @param instanceId
     */
    static void remove(String callbackId, int instanceId)
    {
        synchronized (instanceIdMap)
        {
            Map<String, Java2JsCallback> javaCallbackHashMap = instanceIdMap.get(instanceId);

            if (javaCallbackHashMap == null)
            {
                Iterator<Map<String, Java2JsCallback>> iterator = instanceIdMap.values().iterator();
                while (iterator.hasNext())
                {
                    javaCallbackHashMap = iterator.next();
                    if (javaCallbackHashMap.containsKey(callbackId))
                    {
                        break;
                    }
                }
            }

            if (javaCallbackHashMap != null)
            {
                javaCallbackHashMap.remove(callbackId);
                if (javaCallbackHashMap.size() == 0)
                {
                    instanceIdMap.remove(instanceId);
                }
            }
        }

    }

    /**
     * 根据id获取。
     *
     * @param callbackId
     * @param instanceId
     * @return
     */
    static Java2JsCallback get(String callbackId, int instanceId)
    {
        synchronized (instanceIdMap)
        {
            Map<String, Java2JsCallback> map = instanceIdMap.get(instanceId);
            if (map != null && map.containsKey(callbackId))
            {
                return map.get(callbackId);
            }
            Iterator<Map<String, Java2JsCallback>> iterator = instanceIdMap.values().iterator();
            while (iterator.hasNext())
            {
                map = iterator.next();
                if (map.containsKey(callbackId))
                {
                    return map.get(callbackId);
                }
            }
            return null;
        }
    }

    private static int instanceIdCheck(JsCallback jsCallback)
    {
        if (jsCallback == null || jsCallback instanceof Java2JsCallback)
        {
            throw new RuntimeException(JsCallback.class.getSimpleName() + " for " + Java2JsCallback.class
                    .getSimpleName() + " is illegal!");
        }
        return jsCallback.instanceId;
    }

    public Java2JsCallback(Builder builder)
    {
        this(builder.instanceId, builder.isDebug);
    }

    public Java2JsCallback(JsCallback jsCallback)
    {
        this(instanceIdCheck(jsCallback), jsCallback.isDebug);
    }

    private Java2JsCallback(int istanceId, boolean isDebug)
    {
        super(null, null, "", UUID.randomUUID().toString(), istanceId, isDebug);
        synchronized (instanceIdMap)
        {
            Map<String, Java2JsCallback> map = instanceIdMap.get(instanceId);
            if (map == null)
            {
                map = new HashMap<>();
                instanceIdMap.put(instanceId, map);
            }
            map.put(id, this);
        }
        methodsMap = new HashMap<>();

        try
        {
            Method[] methods = getClass().getMethods();
            for (Method method : methods)
            {
                String sign;
                if ((sign = JsCallJava.genJavaMethodSign(
                        method, false, Callback.class)) == null)
                {
                    continue;
                }
                methodsMap.put(sign, new JsCallJava.MethodClass(method, this));
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws JsCallbackException
    {
        remove(id, instanceId);
    }

    @Override
    public String toString()
    {
        return JAVA_CALLBACK + id;
    }

    @Override
    public JsReturn apply(Object... args) throws JsCallbackException
    {
        throw new JsCallbackException("it is a " + JAVA_CALLBACK);
    }

    JsCallJava.MethodClass getMethodClass(String sign)
    {
        if (!isPermanent())
        {
            try
            {
                if (isDebug)
                {
                    Log.d(getClass().getName(), "will destroy this instance(id=" + id + ")");
                }
                destroy();
            } catch (JsCallbackException e)
            {
                e.printStackTrace();
            }
        }
        return methodsMap.get(sign);
    }


}
