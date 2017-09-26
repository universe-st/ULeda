package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ecnu.uleda.R;

/**
 * Created by xuyanzhe on 26/9/17.
 */

public class BubbleView extends ViewGroup {

    private int mWidth;
    private int mHeight;

    private Paint mBgPaint;

    private RectF mBackgroundRect = new RectF();
    private RectF mShadowRect = new RectF();
    private RectF mBorderRect = new RectF();
    private Path mArrowPath = new Path();
    private int mBorderWidth = 2;
    private int mShadowOffset;
    private int mArrowHeight;
    private int mCornerRadius;
    private int mBgColor;
    private int mShadowColor;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgColor = ContextCompat.getColor(context, R.color.colorULightGray);
        mShadowColor = ContextCompat.getColor(context, R.color.colorUDarkAlphaGray);

        mArrowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        mCornerRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        mShadowOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBgPaint.setColor(mBgColor);
        canvas.drawRoundRect(mBackgroundRect, mCornerRadius, mCornerRadius, mBgPaint);
        canvas.drawPath(mArrowPath, mBgPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mBackgroundRect.set(0, 0, mWidth, mHeight - mArrowHeight);
        mShadowRect.set(mShadowOffset, mShadowOffset, mWidth + mShadowOffset, mHeight - mArrowHeight + mShadowOffset);
        mBorderRect.set(0, 0, mWidth, mHeight - mArrowHeight);
        mArrowPath.moveTo((float) (0.75 * mWidth - mArrowHeight), mHeight - mArrowHeight - mBorderWidth);
        mArrowPath.lineTo((float) (0.75 * mWidth), mHeight - mBorderWidth);
        mArrowPath.lineTo((float) (0.75 * mWidth + mArrowHeight), mHeight - mArrowHeight - mBorderWidth);
        mArrowPath.close();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = mBorderWidth;
        View childView = getChildAt(0);
        int widthSpec = MeasureSpec.makeMeasureSpec(mWidth - 2 * mBorderWidth, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(mHeight - 2 * mBorderWidth - mArrowHeight, MeasureSpec.EXACTLY);
        childView.measure(widthSpec, heightSpec);
        childView.layout(left, mBorderWidth, mWidth - mBorderWidth, mHeight - 2 * mBorderWidth - mArrowHeight);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
}
