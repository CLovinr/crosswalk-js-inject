package com.chenyg.crossjs;


import android.util.Log;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 用于向js端动态传递java函数。使用注解{@linkplain JsInterface}标记要注入的函数.
 */
public abstract class JavaFunction extends JsFunction
{



//    /**
//     * 用于标记回调函数(public、非static),函数的名称没有被使用。
//     * 标记的函数可以有返回值，返回值类型同{@linkplain JsInterface}
//     */
//    @Retention(RetentionPolicy.RUNTIME)
//    @Target(ElementType.METHOD)
//    @Documented
//    public @interface Callback
//    {
//        /**
//         * 是否在第一个参数前加入{@linkplain com.chenyg.crossjs.JavaFunction.Builder}.默认为false.
//         * @return
//         */
//        boolean needBuilder()default false;
//    }

    public static final String JAVA_CALLBACK = "[/JavaFunction/]=";

    private static Map<Integer, Map<String, JavaFunction>> instanceIdMap = Collections
            .synchronizedMap(new HashMap<Integer, Map<String, JavaFunction>>());
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
            Map<String, JavaFunction> javaCallbackHashMap = instanceIdMap.get(instanceId);

            if (javaCallbackHashMap == null)
            {
                Iterator<Map<String, JavaFunction>> iterator = instanceIdMap.values().iterator();
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
    static JavaFunction get(String callbackId, int instanceId)
    {
        synchronized (instanceIdMap)
        {
            Map<String, JavaFunction> map = instanceIdMap.get(instanceId);
            if (map != null && map.containsKey(callbackId))
            {
                return map.get(callbackId);
            }
            Iterator<Map<String, JavaFunction>> iterator = instanceIdMap.values().iterator();
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

    private static int instanceIdCheck(JsFunction jsFunction)
    {
        if (jsFunction == null || jsFunction instanceof JavaFunction)
        {
            throw new RuntimeException(JsFunction.class.getSimpleName() + " for " + JavaFunction.class
                    .getSimpleName() + " is illegal!");
        }
        return jsFunction.instanceId;
    }

    public JavaFunction(JsInterface.Builder builder)
    {
        this(builder.instanceId, builder.isDebug);
    }

    public JavaFunction(JsFunction jsFunction)
    {
        this(instanceIdCheck(jsFunction), jsFunction.isDebug);
    }

    private JavaFunction(int instanceId, boolean isDebug)
    {
        super(null, null, "", UUID.randomUUID().toString(), instanceId, isDebug);
        synchronized (instanceIdMap)
        {
            Map<String, JavaFunction> map = instanceIdMap.get(instanceId);
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
                        method, false, JsInterface.class)) == null)
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
