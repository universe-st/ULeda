package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by jimmyhsu on 2017/4/6.
 */

public class BottomBarLayout extends LinearLayout {

    private String[] mLabels;
    private int[] mIconIds;
    private BottomBarButton[] mChildren;
    private int mChildWidth;

    private int mSelected = 0;
    private OnLabelSelectedListener mListener;

    public BottomBarLayout(Context context) {
        this(context, null);
    }

    public BottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setClickable(true);
    }

    public void setOnLabelSelectedListener(OnLabelSelectedListener listener) {
        mListener = listener;
    }

    public void select(int pos) {
        if (pos < mChildren.length && pos != mSelected) {
            mChildren[pos].select();
            mChildren[mSelected].unSelect();
            mSelected = pos;
        }
    }

    public void init(String[] labels, int[] iconIds) {
        if (labels.length != iconIds.length) throw new IllegalArgumentException("labels and iconIds should have same size!");
        mLabels = labels;
        mIconIds = iconIds;
        mChildren = new BottomBarButton[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            BottomBarButton button = (BottomBarButton) getChildAt(i);
            mChildren[i] = button;
            button.init(BitmapFactory.decodeResource(getResources(), mIconIds[i]),
                    mLabels[i]);
        }
        if (labels.length > 0) {
            mChildren[0].select();
            if (mListener != null) {
                mListener.labelSelected(0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof BottomBarButton)) {
                throw new IllegalStateException("BottomBarLayout should be used with ButtonBarButton");
            }
            LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mChildWidth = MeasureSpec.getSize(widthMeasureSpec) / getChildCount();
    }

    private int mDownPos;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownPos = calFingerPos(x);
                break;
            case MotionEvent.ACTION_UP:
                int upPos = calFingerPos(x);
                if (upPos == mDownPos && upPos != mSelected) {
                    mChildren[upPos].select();
                    mChildren[mSelected].unSelect();
                    mSelected = upPos;
                    if (mListener != null) {
                        mListener.labelSelected(upPos);
                    }
                    return true;
                }

        }
        return super.onTouchEvent(event);
    }

    private int calFingerPos(float x) {
        return (int) (x / mChildWidth);
    }

    public interface OnLabelSelectedListener {
        void labelSelected(int pos);
    }
}
