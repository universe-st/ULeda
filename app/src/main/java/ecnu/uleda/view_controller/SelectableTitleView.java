package ecnu.uleda.view_controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;

import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;

/**
 * Created by jimmyhsu on 2017/4/2.
 */

public class SelectableTitleView extends View {

    //属性
    private int mSelectedColor;
    private int mTextSize;

    //测量
    private int mWidth;
    private int mHeight;
    private int mHorizontalPadding;
    //    private int mVerticalPadding;
    private int mRadius;
    private int mStrokeWidth;

    //数据
    private List<String> mTitles = new ArrayList<>();
    private int mSelected = 0;

    //canvas相关api
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mFramePaint;

    //touch
    private int mTouchSlop;
    private OnTitleSelectedListner mListner;

    public void setOnTitleSelectedListner(OnTitleSelectedListner listner) {
        this.mListner = listner;
    }

    public SelectableTitleView(Context context) {
        this(context, null);
    }

    public SelectableTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SelectableTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorUMain));
        setClickable(true);
        mSelectedColor = ContextCompat.getColor(getContext(), R.color.colorUSwitch);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mSelectedColor = ContextCompat.getColor(getContext(), R.color.colorUMain);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableTitleView);
        mTextSize = a.getDimensionPixelSize(R.styleable.SelectableTitleView_textSize,
                (int) sp2px(20));
        mRadius = a.getDimensionPixelSize(R.styleable.SelectableTitleView_radius, 0);
        mHorizontalPadding = a.getDimensionPixelSize(R.styleable.SelectableTitleView_horizontalPadding,
                0);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.SelectableTitleView_strokeWidth,
                1);
        a.recycle();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    public void setTitles(List<String> titles) {
        this.mTitles = titles;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFrame(canvas);
        drawSelected(canvas);
        drawText(canvas);
    }

    private void drawFrame(Canvas canvas) {
        mFramePaint.setStrokeWidth(mStrokeWidth);
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setColor(0xffffffff);
        RectF rect = new RectF(mStrokeWidth, mStrokeWidth, mWidth - mStrokeWidth, mHeight - mStrokeWidth);
        canvas.drawRoundRect(rect, mRadius, mRadius, mFramePaint);
        float l = 0;
        for (int i = 0; i < mTitles.size() - 1; i++) {
            l += mStrokeWidth + 2 * mHorizontalPadding + getTextWidth(mTitles.get(i));
            canvas.drawLine(l, mStrokeWidth, l, mHeight - mStrokeWidth, mFramePaint);
        }
    }

    private void drawText(Canvas canvas) {
        float x = 0;
        float descent = mTextPaint.getFontMetrics().descent;
        float y = mHeight / 2f + getTextHeight() / 2f - descent;
        for (int i = 0; i < mTitles.size(); i++) {
            x += mStrokeWidth + mHorizontalPadding;
            if (mSelected == i) {
                mTextPaint.setColor(mSelectedColor);
            } else {
                mTextPaint.setColor(0xffffffff);
            }
            canvas.drawText(mTitles.get(i), x, y, mTextPaint);
            x += mHorizontalPadding + getTextWidth(mTitles.get(i));
        }
    }

    private void drawSelected(Canvas canvas) {
        if (mTitles == null || mTitles.size() == 0) return;
        mBackgroundPaint.setColor(0xffffffff);
        if (mSelected == 0) {
            RectF rectF = new RectF(mStrokeWidth,
                    mStrokeWidth,
                    mHorizontalPadding + getTextWidth(mTitles.get(0)),
                    mHeight - mStrokeWidth);
            canvas.drawRoundRect(rectF, mRadius, mRadius, mBackgroundPaint);
            canvas.drawRect(mHorizontalPadding,
                    mStrokeWidth,
                    2 * mHorizontalPadding + getTextWidth(mTitles.get(0)) + mStrokeWidth,
                    mHeight - mStrokeWidth, mBackgroundPaint);
        } else if (mSelected == mTitles.size() - 1) {
            RectF rectF = new RectF(mWidth - mHorizontalPadding - getTextWidth(mTitles.get(mTitles.size() - 1)),
                    mStrokeWidth,
                    mWidth - mStrokeWidth,
                    mHeight - mStrokeWidth);
            canvas.drawRoundRect(rectF, mRadius, mRadius, mBackgroundPaint);
            canvas.drawRect(mWidth - 2 * mHorizontalPadding - 2 * mStrokeWidth - getTextWidth(mTitles.get(mTitles.size() - 1)),
                    mStrokeWidth, mWidth - mHorizontalPadding, mHeight-mStrokeWidth, mBackgroundPaint);
        } else {
            float l = 0;
            for (int i = 0; i < mSelected; i++) {
                l += 2 * mHorizontalPadding + getTextWidth(mTitles.get(i)) + mStrokeWidth;
            }
            Rect rect = new Rect((int) l, mStrokeWidth,
                    (int) (mStrokeWidth + l + 2 * mHorizontalPadding + getTextWidth(mTitles.get(mSelected))),
                    mHeight - mStrokeWidth);
            canvas.drawRect(rect, mBackgroundPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthResult = 0;
        int heightResult = 0;
        for (String title : mTitles) {
            widthResult += (2 * mHorizontalPadding + getTextWidth(title));
        }
        widthResult += (mTitles.size() + 1) * mStrokeWidth;
        mWidth = widthResult;

        heightResult = (int) (getTextHeight() + 2 * mStrokeWidth + dp2px(4));
        mHeight = heightResult;
        setMeasuredDimension(widthResult, heightResult);
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float dis = calculateFingerMove(x, y);
                if (dis < mTouchSlop) {
                    select(x);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void select(float x) {
        float f = 0;
        for (int i = 0; i < mTitles.size(); i++) {
            f += 2 * mHorizontalPadding + getTextWidth(mTitles.get(i)) + mStrokeWidth;
            if (x <= f) {
                if (mSelected != i) {
                    mSelected = i;
                    invalidate();
                    if (mListner != null) {
                        mListner.onItemSelected(i, mTitles.get(i));
                    }
                }
                break;
            }
        }
    }

    private float calculateFingerMove(float x, float y) {
        float deltaX = Math.abs(x - mDownX);
        float deltaY = Math.abs(y - mDownY);
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private float getTextWidth(String src) {
        return mTextPaint.measureText(src, 0, src.length());
    }

    private float getTextHeight() {
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        return metrics.descent - metrics.ascent;
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public interface OnTitleSelectedListner {
        void onItemSelected(int pos, String title);
    }
}
