package com.chenyg.crossjs;

import android.text.TextUtils;

/**
 * Created by 宇宙之灵 on 2016/6/30.
 */
public class CrossjsUtil
{
    private static CrossjsUtil crossjsUtil;
    private MyXWalkExtension myXWalkExtension;

    private CrossjsUtil(JsCallJava jsCallJava, InjectObj[] injectObjs)
    {
        this.myXWalkExtension = new MyXWalkExtension(jsCallJava.topName, jsCallJava, injectObjs);
    }

    /**
     * @param willPrintDebugInfo 是否打印调试信息。
     * @param topName            顶层对象的名称,只能是字母或数字,且不能以数字开头
     * @param injectObjs         用于注入,此处注入的无法被移除，已经被写死。在动态接口之前注入。
     */
    public synchronized static void init(boolean willPrintDebugInfo, String topName, InjectObj... injectObjs)
    {
        if (TextUtils.isEmpty(topName))
        {
            throw new RuntimeException("The topName can not be null!");
        } else if (!WEBUtil.isTopName(topName))
        {
            throw new RuntimeException("The topName is illegal!");
        }
        if (crossjsUtil == null)
        {
            crossjsUtil = new CrossjsUtil(
                    new JsCallJava(topName, willPrintDebugInfo, injectObjs), injectObjs);
        }
    }

    public static CrossjsUtil getCrossjsUtil()
    {
        if (crossjsUtil == null)
        {
            throw new RuntimeException(
                    "You have to invoke " + CrossjsUtil.class.getSimpleName() + ".init method first!");
        }
        return crossjsUtil;
    }

    /**
     * 禁用或者启用注入的接口。
     *
     * @param isDisabled 是否禁用。
     */
    public void disable(boolean isDisabled)
    {
        myXWalkExtension.disable(isDisabled);
    }

    public void setIInjectHandle(IInjectHandle iInjectHandle)
    {
        myXWalkExtension.setIInjectHandle(iInjectHandle);
    }

    /**
     * 动态添加接口。
     *
     * @param injectObj
     */
    public synchronized void addDynamicInjectObj(InjectObj injectObj)
    {
        myXWalkExtension.addDynamicInjectObj(injectObj);
    }

    /**
     * 移除动态添加的接口。
     *
     * @param namespace
     */
    public synchronized void removeDynamicInjectObj(String namespace)
    {
        myXWalkExtension.removeDynamicInjectObj(namespace);
    }
}
