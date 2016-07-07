package com.chenyg.crossjs;


import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

class WEBUtil
{
    private static final Pattern TOP_NAME_PATTERN = Pattern.compile("^[a-zA-Z]([0-9a-zA-Z]*)$");
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("^([0-9a-zA-Z\\$_\\.]+)$");
    /**
     * 是否为顶层名称。
     * @param topName 只包含字母和数字，且以字母开头。
     * @return
     */
    public static boolean isTopName(String topName)
    {
        return TOP_NAME_PATTERN.matcher(topName).find();
    }

    /**
     * 是否是命名空间
     * @param namespace 包含字母、数字、下划线、美元符号，以点号'.'隔开.
     * @return
     */
    public static boolean isNamespace(String namespace){
        boolean is = NAMESPACE_PATTERN.matcher(namespace).find();
        if(is){
            is=namespace.indexOf("..")<0&&!namespace.startsWith(".")&&!namespace.endsWith(".");
        }
        return is;
    }

    public static String loadPackageJs(Class<?> c, String path, boolean isDebug) throws Exception
    {
        String content = read(c, path);
        content = content.replaceAll("/\\*[^/]*\\*/", "");
        if (!isDebug)
        {
            content = content.replace("\n", "");
        }
        return content;
    }

    private static String read(Class<?> c, String path) throws Exception
    {
        InputStream in = null;
        try
        {
            in = c.getResourceAsStream(path);
            byte[] bs = new byte[in.available()];
            in.read(bs);
            return new String(bs, "utf-8");
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
