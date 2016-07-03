package com.chenyg.crosswalk_js_inject;

import com.chenyg.crossjs.JsInterface;

/**
 * Created by 宇宙之灵 on 2016/7/3.
 */
public class DynamicJs
{
    @JsInterface
    public String hello(){
        return "DynamicHello!";
    }
}
