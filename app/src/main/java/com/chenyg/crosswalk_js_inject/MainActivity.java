package com.chenyg.crosswalk_js_inject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import com.chenyg.crossjs.CrossjsUtil;
import com.chenyg.crossjs.InjectObj;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

public class MainActivity extends XWalkActivity
{

    private XWalkView xWalkView;
    @Override
    protected void onXWalkReady()
    {
        xWalkView = new XWalkView(this,this);
        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView){
            @Override
            public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedLoadError(view, errorCode, description, failingUrl);
                Log.e("err",errorCode+":"+failingUrl);
            }
        });
        CrossjsUtil.init(true,"test",new InjectObj("js1",new Js1(new Handler(),this)));
        xWalkView.load("file:///android_asset/demo1.html",null);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.cross_parent);
        viewGroup.removeAllViewsInLayout();
        viewGroup.addView(xWalkView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(xWalkView!=null){
            xWalkView.onDestroy();
        }
    }
}
