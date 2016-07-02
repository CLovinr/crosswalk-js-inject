package com.chenyg.crossjs;

import java.lang.annotation.*;

/**
 * 用于标记注入的函数(public,非static)。
 * <pre>
 *     支持的返回类型有:
 *     {@linkplain Java2JsCallback},
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
    /**
     * 是否在第一个参数前加入{@linkplain com.chenyg.crossjs.Java2JsCallback.Builder}.默认为false。
     * @return
     */
    boolean needBuilder()default false;
}
