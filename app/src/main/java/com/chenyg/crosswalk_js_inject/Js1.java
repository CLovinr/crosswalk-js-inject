package com.chenyg.crosswalk_js_inject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.chenyg.crossjs.Java2JsCallback;
import com.chenyg.crossjs.JsCallback;
import com.chenyg.crossjs.JsInterface;
import com.chenyg.crossjs.JsReturn;

/**
 * Created by 宇宙之灵 on 2016/7/1.
 */
public class Js1
{
    private Handler handler;
    private Context context;


    public Js1(Handler handler, Context context)
    {
        this.handler = handler;
        this.context = context;
    }

    @JsInterface
    public String hello()
    {
        return "Hello World!";
    }

    @JsInterface
    public void jsFun(JsCallback callback)
    {
        try
        {
            callback.apply(new Java2JsCallback(callback)
            {
                @Callback
                public String toast(String text)
                {
                    return "response:" + text;
                }
            }).onReturn(new JsReturn.Listener()
            {
                @Override
                public void onGetString(String returnValue)
                {
                    toast(returnValue);
                }
            });
        } catch (JsCallback.JsCallbackException e)
        {
            e.printStackTrace();
        }
    }

    @JsInterface
    public void toast(final String text)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JsInterface(needBuilder = true)
    public Java2JsCallback javaCall(Java2JsCallback.Builder builder)
    {
        Log.d(getClass().getName(),"javaCall invoked!");
        return new Java2JsCallback(builder)
        {
            @Callback
            public String getName()
            {
                return getClass().getName()+"\nHello!!!!";
            }
        };
    }
}
