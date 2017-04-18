package ecnu.uleda.function_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.WeakHashMap;

import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.tool.PictureTool;

/**
 * Created by Shensheng on 2017/4/16.
 * 获取图片
 */

public class PictureGetter{
    private static final String PREFIX="pic";
    private static final String POSTFIX=".png";
    private Context mContext;
    private UserOperatorController mUoc=UserOperatorController.getInstance();
    private static WeakHashMap<Integer,Bitmap> sCache = new WeakHashMap<>();
    public PictureGetter setContext(Context context){
        mContext = context.getApplicationContext();
        return this;
    }
    public Bitmap getPicture(int pictureId){
        Bitmap bitmap = getPictureFromRAM(pictureId);
        if(bitmap == null){
            bitmap = getPictureFromLocalFile(pictureId);
        }
        if(bitmap == null){
            bitmap = getPictureFromServer(pictureId);
        }
        return bitmap;
    }
    private boolean putPictureToLocal(int pictureId,Bitmap bitmap){
        FileOutputStream fos=null;
        try {
            fos = mContext.openFileOutput(PREFIX + pictureId + POSTFIX, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            closeOutputStream(fos);
            return false;
        }
    }

    private Bitmap getPictureFromServer(int pictureId){
        Bitmap bitmap = null;
        try {
            String str = ServerAccessApi.getPicture(mUoc.getId(), mUoc.getPassport(), pictureId);
            bitmap = PictureTool.decodePic(str.trim());
        }catch (UServerAccessException e){
            e.printStackTrace();
        }
        if(bitmap != null){
            putPictureToLocal(pictureId,bitmap);
            sCache.put(pictureId,bitmap);
        }
        return bitmap;
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
            sCache.put(pictureId,bitmap);
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
    private static void closeOutputStream(FileOutputStream fos){
        try{
            if(fos!=null) {
                fos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
