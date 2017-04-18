package ecnu.uleda.function_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.WeakHashMap;

import ecnu.uleda.exception.UServerAccessException;

/**
 * Created by Shensheng on 2017/4/16.
 * 获取图片
 */

public class PictureGetter{
    private static final int SET_TIME_OUT = 9999;
    private static final String PREFIX="pic";
    private static final String POSTFIX=".png";
    private Context mContext;
    private static WeakHashMap<Integer,Bitmap> sCache = new WeakHashMap<>();
    public PictureGetter setContext(Context context){
        mContext = context.getApplicationContext();
        return this;
    }
    public Bitmap getPicture(int pictureId) throws UServerAccessException {
        Bitmap bitmap = getPictureFromRAM(pictureId);
        if(bitmap == null){
            bitmap = getPictureFromLocalFile(pictureId);
        }
        if(bitmap == null){
            bitmap = getPictureFromServer(pictureId);
        }
        return bitmap;
    }
    private Bitmap getPictureFromServer(int pictureId)throws UServerAccessException {

        return null;
    }
    private Bitmap getPictureFromRAM(int pictureId){
        return sCache.get(pictureId);
    }
    private Bitmap getPictureFromLocalFile(int pictureId){
        Bitmap bitmap=null;
        FileInputStream fis=null;
        try{
            fis = mContext.openFileInput(PREFIX+pictureId+POSTFIX);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
            closeInputStream(fis);
        }
        return bitmap;
    }
    public boolean cleanPictureCache(){
        File[] files = mContext.getFilesDir().listFiles();
        boolean isOK=true;
        for(File f:files){
            String name=f.getName();
            if(name.startsWith(PREFIX) && name.endsWith(POSTFIX)){
                boolean successDelete = f.delete();
                isOK = isOK && successDelete;
                if(!successDelete){
                    Log.e(this.getClass().getSimpleName(),f.getName()+" cannot be cleaned.");
                }
            }
        }
        return isOK;
    }
    public long getCacheSize(){
        File[] files = mContext.getFilesDir().listFiles();
        long size=0;
        for(File f:files){
            String name=f.getName();
            if(name.startsWith(PREFIX) && name.endsWith(POSTFIX)) {
                size += f.length();
            }
        }
        return size;
    }
    private static void closeInputStream(FileInputStream fis){
        try{
            if(fis!=null) {
                fis.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
