package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import ecnu.uleda.R;

/**
 * Created by jimmyhsu on 2017/4/6.
 */

public class BottomBarButton extends View {

    private int mWidth;
    private int mHeight;
    private float mMarginLeft;
    private float mMarginTop;
    private float mInnerMargin;
    private Bitmap mIcon;
    private String mText;
    private boolean isInited = false;
    private boolean isSelected = false;

    private Paint mTextPaint;

    public BottomBarButton(Context context) {
        this(context, null);
    }

    public BottomBarButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BottomBarButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(sp2px(getContext(), 12));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mMarginLeft = mWidth / 6f;
        mMarginTop = mHeight / 7f;
        mInnerMargin = mHeight / 14f;
    }

    public void init(Bitmap bitmap, String label) {
        mIcon = bitmap;
        mText = label;
        isInited = true;
        invalidate();
    }

    public void select() {
        isSelected = true;
        invalidate();
    }

    public void unSelect() {
        isSelected = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInited) {
            drawBg(canvas);
            drawIcon(canvas);
            drawLabel(canvas);
        }
    }

    private void drawBg(Canvas canvas) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(isSelected ? ContextCompat.getColor(getContext(), R.color.colorUSwitch) :
                ContextCompat.getColor(getContext(), R.color.colorUMain));
        bgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mWidth, mHeight, bgPaint);
    }

    private void drawIcon(Canvas canvas) {
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;
        float iconWidth = Math.min(mWidth - 2 * mMarginLeft, mHeight - 2 * mMarginTop - textHeight - mInnerMargin);
        RectF dst = new RectF(mWidth / 2 - iconWidth / 2, mMarginTop,
                mWidth / 2 + iconWidth / 2, mMarginTop + iconWidth);
        Rect src = new Rect(0, 0, mIcon.getWidth(), mIcon.getHeight());
        canvas.drawBitmap(mIcon, src, dst, null);
    }

    private void drawLabel(Canvas canvas) {

        float textWidth = mTextPaint.measureText(mText, 0, mText.length());
//        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        canvas.drawText(mText, mWidth / 2 - textWidth / 2, mHeight - mMarginTop, mTextPaint);
    }

    private static float sp2px(Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }
}
