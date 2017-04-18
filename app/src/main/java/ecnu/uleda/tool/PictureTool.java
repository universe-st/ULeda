package ecnu.uleda.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by Shensheng on 2017/4/16.
 * 图片处理
 */

public class PictureTool {
    public static String encodePic(Bitmap bitmap){
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bao);
        return new BASE64Encoder().encode(bao.toByteArray());
    }
    public static Bitmap decodePic(String str){
        try{
            byte[] bytes = new BASE64Decoder().decodeBuffer(str);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
