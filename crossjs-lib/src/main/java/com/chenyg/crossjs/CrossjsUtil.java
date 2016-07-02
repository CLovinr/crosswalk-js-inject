package com.chenyg.crossjs;

/**
 * Created by 宇宙之灵 on 2016/6/30.
 */
public class CrossjsUtil
{
    private static CrossjsUtil crossjsUtil;
    private MyXWalkExtension myXWalkExtension;

    private CrossjsUtil(JsCallJava jsCallJava)
    {
        this.myXWalkExtension = new MyXWalkExtension(jsCallJava.topName, jsCallJava);
    }

    /**
     * @param willPrintDebugInfo 是否打印调试信息。
     * @param topName            顶层对象的名称
     * @param injectObjs         用于注入
     */
    public synchronized static void init(boolean willPrintDebugInfo, String topName, InjectObj... injectObjs)
    {
        if (crossjsUtil == null)
        {
            crossjsUtil = new CrossjsUtil(
                    new JsCallJava(topName, willPrintDebugInfo, injectObjs));
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
     * @param isDisabled 是否禁用。
     */
    public void disable(boolean isDisabled)
    {
        myXWalkExtension.disable(isDisabled);
    }
}
