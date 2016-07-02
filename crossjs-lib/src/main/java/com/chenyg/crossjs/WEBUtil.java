package com.chenyg.crossjs;


import java.io.IOException;
import java.io.InputStream;

class WEBUtil
{
    static String loadPackageJs(Class<?> c, String path, boolean isDebug) throws Exception
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
