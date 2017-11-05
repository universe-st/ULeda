package ecnu.uleda.view_controller;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 63516 on 2017/9/20.
 */

public class ImageItem implements Parcelable{
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    private Bitmap bitmap;
    public boolean isSelected = false;
    public static final Parcelable.Creator<ImageItem>CREATOR=new Parcelable.Creator<ImageItem>(){

        @Override
        public ImageItem createFromParcel(Parcel source) {
            ImageItem it=new ImageItem();
            it.imageId=source.readString();
            it.thumbnailPath=source.readString();
            it.imagePath=source.readString();
            return it;
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public Bitmap getBitmap() {
        if(bitmap == null){
            try {
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeString(thumbnailPath);
        dest.writeString(imagePath);
    }
}
