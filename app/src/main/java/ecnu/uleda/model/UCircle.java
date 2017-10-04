package ecnu.uleda.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ecnu.uleda.tool.UPublicTool;

/**
 * Created by 胡楠 on 2017/1/24.
 */

public class UCircle {

    private   String mPhotoId;
    private   String mName ;
    private   String mTitle;
    private   String mArticle ;
    private   String mTime ;
    private   String mGet ;
    private   String mDynamic_Photo1;
    private   String mDynamic_Photo2;
    private   String mDynamic_Photo3;
    private   String id;
    private   String Commentcontent;
    private   String CommentTime;
    private   String CommentName;
    private   String CommentImage;
    public String getCommentImage() {
        return CommentImage;
    }

    public UCircle setCommentImage(String commentImage) {
        CommentImage = commentImage;
        return this;
    }


    public String getCommentName() {
        return CommentName;
    }

    public UCircle setCommentName(String commentName) {
        CommentName = commentName;
        return this;
    }


    public String getCommentTime() {
        return CommentTime;
    }

    public UCircle setCommentTime(String commentTime) {
        CommentTime = commentTime;
        return this;
    }



    public String getCommentcontent() {
        return Commentcontent;
    }

    public UCircle setCommentcontent(String commentcontent) {
        Commentcontent = commentcontent;
        return this;
    }



    public String getId() {
        return id;
    }

    public UCircle setId(String id) {
        this.id = id;
        return this;
    }

    public String getmName() {
        return mName;
    }

    public UCircle setmName(String mName) {
        this.mName = mName;
        return this;
    }



    public String  getmDynamic_Photo1() {
        return mDynamic_Photo1;
    }
    public String  getmDynamic_Photo2() {
        return mDynamic_Photo2;
    }
    public String  getmDynamic_Photo3() {
        return mDynamic_Photo3;
    }
    public UCircle setmDynamic_Photo1(String mDynamic_Photo) {

            this.mDynamic_Photo1 = mDynamic_Photo;

        return this;
    }
    public UCircle setmDynamic_Photo2(String mDynamic_Photo) {

        this.mDynamic_Photo2 = mDynamic_Photo;

        return this;
    }
    public UCircle setmDynamic_Photo3(String mDynamic_Photo) {

        this.mDynamic_Photo3 = mDynamic_Photo;

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
        long Timeout = Long.parseLong(mTime);
     return UPublicTool.dateToTimeBefore(new Date(Timeout * 1000));
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

    public String getmPhotoId() {
        return mPhotoId;
    }

    public UCircle setmPhotoId(String mPhotoId) {
        this.mPhotoId = mPhotoId;
        return this;
    }
}
