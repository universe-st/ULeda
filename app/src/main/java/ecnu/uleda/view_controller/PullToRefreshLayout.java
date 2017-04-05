package ecnu.uleda.view_controller;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by jimmyhsu on 2017/4/4.
 */

public class PullToRefreshLayout extends LinearLayout implements NestedScrollingParent {

    private int mWidth;
    private int mHeight;
    private int mTopViewHeight;
    private int mBottomViewHeight;

    private View mTopView;
    private View mScrollableView;
    private View mBottomView;

    private NestedScrollingParentHelper mHelper;
    private int mTouchSlop;
    private float mLastY;
    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new NestedScrollingParentHelper(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (child instanceof RecyclerView) {
            RecyclerView.LayoutManager lm = ((RecyclerView) child).getLayoutManager();
            if (lm instanceof LinearLayoutManager) {
                return ((LinearLayoutManager)lm).findFirstCompletelyVisibleItemPosition() == 0;
            }
        }
        return false;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        mHelper.onStopNestedScroll(child);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        consumed[0] = 0;
        consumed[1] = dy;
        Log.e("ptr", "dy = " + dy);
        float currY = mTopView.getY();
        currY += dy;
        currY = Math.min(currY, 0);
        mTopView.setY(currY);
        super.onNestedPreScroll(target, dx, dy, consumed);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() < 2)
            throw new IllegalStateException("PullToRefreshLayout should be used with at least 2 children.");
        mWidth = w;
        mHeight = h;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = getChildAt(0);
        mScrollableView = getChildAt(1);
        mTopViewHeight = mTopView.getMeasuredHeight();
        mBottomViewHeight = mBottomView.getMeasuredHeight();
        if (getChildCount() == 3) {
            mBottomView = getChildAt(2);
        }
        mTopView.setY(-mTopViewHeight);
        mScrollableView.setY(0);
    }
}
