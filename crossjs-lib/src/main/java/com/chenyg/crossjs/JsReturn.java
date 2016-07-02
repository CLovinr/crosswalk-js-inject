package com.chenyg.crossjs;

/**
 * Created by 宇宙之灵 on 2016/7/2.
 */
public interface JsReturn
{
    public void onReturn(Listener listener);

    public interface Listener{
        public void onGetString(String returnValue);
    }

    class JsReturnImpl implements JsReturn{
        Listener listener;
        @Override
        public void onReturn(Listener listener)
        {
            this.listener=listener;
        }
    }
}
