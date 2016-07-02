package com.chenyg.crossjs;

import java.lang.annotation.*;

/**
 * 用于标记注入的函数(public,非static)。
 * 函数的返回值可以是{@linkplain Java2JsCallback}类型的。
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
