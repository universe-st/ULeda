package ecnu.uleda;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 胡楠 on 2017/1/24.
 */

public class UCircle {

    private   int mPhotoId;
    private   String mName ;
    private   String mTitle;
    private   String mArticle ;
    private   String mTime ;
    private   String mGet ;
    private   int mDynamic_Photo;

    public String getmName() {
        return mName;
    }

    public UCircle setmName(String mName) {
        this.mName = mName;
        return this;
    }



    public int getmDynamic_Photo() {
        return mDynamic_Photo;
    }

    public UCircle setmDynamic_Photo(int mDynamic_Photo) {
        this.mDynamic_Photo = mDynamic_Photo;
        return this;
    }

    public String getmGet() {
        return mGet;
    }

    public UCircle setmGet(String mGet) {
        this.mGet = mGet;
        return this;
    }

    public String getmTime() {
        return mTime;
    }

    public UCircle setmTime(String mTime) {
        this.mTime = mTime;
        return this;
    }

    public String getmArticle() {
        return mArticle;
    }

    public UCircle setmArticle(String mArticle) {
        this.mArticle = mArticle;
        return this;
    }

    public String getmTitle() {
        return mTitle;
    }

    public UCircle setmTitle(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public int getmPhotoId() {
        return mPhotoId;
    }

    public UCircle setmPhotoId(int mPhotoId) {
        this.mPhotoId = mPhotoId;
        return this;
    }
}
