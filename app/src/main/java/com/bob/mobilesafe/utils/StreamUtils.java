package com.bob.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/12/28.
 */
//功能：输入流转化为字符串
public class StreamUtils {
    public static String readStreamUtils(InputStream in){
        try{
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len=-1;
            while((len=in.read(buffer))!=-1){
                baos.write(buffer, 0, len);
            }
            in.close();
            return new String(baos.toByteArray());
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
