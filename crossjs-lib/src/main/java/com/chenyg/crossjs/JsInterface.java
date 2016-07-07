package com.chenyg.crossjs;

import java.lang.annotation.*;

/**
 * 用于标记注入的函数(public,非static)。
 * <pre>
 *     支持的返回类型有:
 *     {@linkplain JavaFunction},
 *     String,基本类型以及对应的封装类型，
 *     org.json.JSONObject，org.json.JSONArray,
 *     Object(会使用Gson.toJson()来转换)
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JsInterface
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

    public enum Param{
        builder
    }

    /**
     * 在形参列表开头要加入的参数。
     *
     * @return
     */
    Param[] paramsFirst() default {};
}
