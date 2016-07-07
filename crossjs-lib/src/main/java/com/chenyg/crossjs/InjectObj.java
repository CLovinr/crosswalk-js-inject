package com.chenyg.crossjs;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * json对象中的字符串不能以{@linkplain #JS_FUNCTION_STARTS}或{@linkplain JavaFunction#JAVA_CALLBACK}开头。
 * <br>
 * 命名空间合法字符：包含字母、数字、下划线、美元符号，以点号'.'隔开
 */
public class InjectObj
{
    public final static String JS_FUNCTION_STARTS = "[-/js-function/-]=";

    String namespace;
    List<Class<?>> interfaceClasses = new ArrayList<>();
    List<Object> interfaceObjects = new ArrayList<>();

    /**
     * 对应的类必须含有无参构造函数。
     *
     * @param namespace        命名空间,如:"base.ui","lib.util","demo"
     * @param interfaceClasses 要注入的类，必须含有无参构造函数.
     */
    public InjectObj(String namespace, Class<?>... interfaceClasses)
    {
        setNamespace(namespace);
        this.add(interfaceClasses);
    }

    /**
     * @param namespace        命名空间
     * @param interfaceObjects 要注入的对象
     */
    public InjectObj(String namespace, Object... interfaceObjects)
    {
        setNamespace(namespace);
        this.add(interfaceObjects);
    }

    private void setNamespace(String namespace)
    {
        if (TextUtils.isEmpty(namespace))
        {
            throw new RuntimeException("namespace can not be null!");
        } else if (!WEBUtil.isNamespace(namespace))
        {
            throw new RuntimeException("the namespace is illegal!");
        }
        this.namespace = namespace;
    }

    /**
     * 添加注入对象。
     *
     * @param interfaceObjects 要注入的对象
     */
    public InjectObj add(Object... interfaceObjects)
    {
        for (Object object : interfaceObjects)
        {
            this.interfaceObjects.add(object);
        }

        return this;
    }

    /**
     * 添加要注入的类。
     *
     * @param interfaceClasses 必须含有无参构造函数。
     */
    public InjectObj add(Class<?>... interfaceClasses)
    {
        for (Class<?> c : interfaceClasses)
        {
            this.interfaceClasses.add(c);
        }
        return this;
    }
}

