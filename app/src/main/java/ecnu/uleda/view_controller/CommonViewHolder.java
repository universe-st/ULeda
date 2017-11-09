package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jimmyhsu on 2017/4/4.
 */

public class CommonViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public CommonViewHolder(Context context, ViewGroup parent, int resId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(resId, parent, false);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder getInstance(Context context, View convertView, ViewGroup parent,
                                               int layoutId, int position) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, position);
        } else {
            CommonViewHolder viewHolder = (CommonViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return viewHolder;
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public CommonViewHolder setText(int viewId, SpannableStringBuilder text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    public CommonViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public TextView getTextView(int viewId) {
        View tv = getView(viewId);
        if (tv instanceof TextView) {
            return (TextView) tv;
        }
        return null;
    }

    public ImageView getImageView(int viewId) {
        View tv = getView(viewId);
        if (tv instanceof ImageView) {
            return (ImageView) tv;
        }
        return null;
    }
}