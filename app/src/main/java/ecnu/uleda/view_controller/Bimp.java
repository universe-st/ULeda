package ecnu.uleda.view_controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 63516 on 2017/9/20.
 */

public class Bimp implements Parcelable{
    public static int max = 0;

    public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem> ();   //选择的图片的临时列表

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(tempSelectBitmap);
        parcel.writeInt(max);

    }

    private static final Parcelable.Creator<Bimp> CREATOR =new Parcelable.Creator<Bimp>(){

        @Override
        public Bimp createFromParcel(Parcel parcel) {
            Bimp bimp=new Bimp();
            bimp.tempSelectBitmap= parcel.readArrayList(ArrayList.class.getClassLoader());
            bimp.max=parcel.readInt();
            return  bimp;
        }

        @Override
        public Bimp[] newArray(int i) {
            return new Bimp[i];
        }
    };
}
