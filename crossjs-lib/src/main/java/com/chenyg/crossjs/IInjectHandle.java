package com.chenyg.crossjs;

/**
 * Created by 宇宙之灵 on 2016/7/3.
 */
public interface IInjectHandle
{
    public void beforeInject(String url, IDeal iDeal);

    public interface IDeal
    {
        /**
         * 针对当前url，除此以外的接口都会被禁用
         *
         * @param interfaceNamespaces 允许的接口的命名空间
         */
        public void enableInterface(String... interfaceNamespaces);

        /**
         * 针对当前url，除此以外的接口都会被允许
         *
         * @param interfaceNamespaces 禁用的接口的命名空间
         */
        public void disableInterface(String... interfaceNamespaces);
    }

    static final IInjectHandle ENABLE_ALL_HANDLE = new IInjectHandle()
    {
        @Override
        public void beforeInject(String url, IDeal iDeal)
        {
            iDeal.disableInterface();
        }
    };
}
