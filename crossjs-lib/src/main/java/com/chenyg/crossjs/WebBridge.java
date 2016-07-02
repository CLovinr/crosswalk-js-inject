package com.chenyg.crossjs;


interface WebBridge
{
    JsReturn invoke(String jsCallbackId,int instanceId,String content);
}
