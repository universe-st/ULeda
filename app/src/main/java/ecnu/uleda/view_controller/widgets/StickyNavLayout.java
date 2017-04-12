package ecnu.uleda.view_controller.widgets;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.loonggg.rvbanner.lib.RecyclerViewBanner;
import com.wang.avi.AVLoadingIndicatorView;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;


public class StickyNavLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "StickyNavLayout";
    public final static int STATE_NORMAL = 0;
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_DONE = 3;
    private int mState = STATE_NORMAL;
    private boolean scrollEnabled = true;
    private ValueAnimator mArrowAnimatorToRefresh;
    private ValueAnimator mArrowAnimatorToNormal;
    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void refreshComplete() {
        if (mState == STATE_REFRESHING) {
            mState = STATE_DONE;
            stateRefreshComplete();
            smoothlyScrollTo(mRefreshViewHeight);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && target instanceof RecyclerView;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (getScrollY() < 0) {
            smoothlyScrollTo(0);
            mState = STATE_REFRESHING;
            stateRefreshing();
            if (mListener != null) {
                mListener.onRefresh();
            }
        } else if (getScrollY() > 0 && getScrollY() < mRefreshViewHeight) {
            smoothlyScrollTo(mRefreshViewHeight);
        }
    }

    private void smoothlyScrollTo(int targetY) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), targetY)
                .setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!scrollEnabled) return;

        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight + mRefreshViewHeight;
        boolean showTop = dy < 0 && !ViewCompat.canScrollVertically(target, -1);

        if (hiddenTop || showTop) {
            if (isAutoPlay && mTop instanceof RecyclerViewBanner) {
                RecyclerViewBanner top = (RecyclerViewBanner) mTop;
                isAutoPlay = false;
            }
            if (dy < 0 && getScrollY() <= mRefreshViewHeight) {
                dy *= 0.3;
            } else if (dy < 0 && getScrollY() <= 0) {
                dy *= 0.2;
            }

            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //down - //up+
        if (getScrollY() >= mTopViewHeight + mRefreshViewHeight) return false;
        fling((int) velocityY);
        return true;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    private View mTop;
    private View mNav;
    private ConstraintLayout mRefresh;
    private ViewPager mViewPager;
    private ImageView mRefreshArrow;
    private AVLoadingIndicatorView mPacman;
    private TextView mRefreshText;

    private int mTopViewHeight;
    private int mRefreshViewHeight;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;

    private float mLastY;
    private boolean mDragging;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
        mArrowAnimatorToRefresh = ValueAnimator.ofInt(0, 180).setDuration(300);
        mArrowAnimatorToRefresh.setInterpolator(new LinearInterpolator());
        mArrowAnimatorToRefresh.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mRefreshArrow.setRotation(val);
            }
        });
        mArrowAnimatorToNormal = ValueAnimator.ofInt(180, 0).setDuration(300);
        mArrowAnimatorToNormal.setInterpolator(new LinearInterpolator());
        mArrowAnimatorToNormal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mRefreshArrow.setRotation(val);
            }
        });
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        initVelocityTrackerIfNotExists();
//        mVelocityTracker.addMovement(event);
//        int action = event.getAction();
//        float y = event.getY();
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                if (!mScroller.isFinished())
//                    mScroller.abortAnimation();
//                mLastY = y;
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                float dy = y - mLastY;
//
//                if (!mDragging && Math.abs(dy) > mTouchSlop) {
//                    mDragging = true;
//                }
//                if (mDragging) {
//                    scrollBy(0, (int) -dy);
//                }
//
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                mDragging = false;
//                recycleVelocityTracker();
//                if (!mScroller.isFinished()) {
//                    mScroller.abortAnimation();
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                mDragging = false;
//                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                int velocityY = (int) mVelocityTracker.getYVelocity();
//                if (Math.abs(velocityY) > mMinimumVelocity) {
//                    fling(-velocityY);
//                }
//                recycleVelocityTracker();
//                break;
//        }
//
//        return super.onTouchEvent(event);
//    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRefresh = (ConstraintLayout) findViewById(R.id.id_stickynavlayout_refreshheader);
        mTop = findViewById(R.id.id_stickynavlayout_topview);
        mNav = findViewById(R.id.id_stickynavlayout_indicator);
        View view = findViewById(R.id.id_stickynavlayout_viewpager);
        mRefreshArrow = (ImageView) findViewById(R.id.id_refresh_arrow);
        mRefreshText = (TextView) findViewById(R.id.id_refresh_text);
        mPacman = (AVLoadingIndicatorView) findViewById(R.id.id_avloading);
        if (!(view instanceof ViewPager)) {
            throw new RuntimeException(
                    "id_stickynavlayout_viewpager should be used by ViewPager !");
        }
        mViewPager = (ViewPager) view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getChildAt(0).measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)UPublicTool.dp2px(getContext(), 60), MeasureSpec.EXACTLY));
        getChildAt(1).measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) UPublicTool.dp2px(getContext(), 180), MeasureSpec.EXACTLY));
        mRefreshViewHeight = mRefresh.getMeasuredHeight();
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
        setMeasuredDimension(getMeasuredWidth(), mTop.getMeasuredHeight() + mNav.getMeasuredHeight() + mViewPager.getMeasuredHeight());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            super.onLayout(changed, l, t, r, b);
            scrollTo(0, mRefreshViewHeight);
            stateNormal();
        }
    }

    public void fling(int velocityY) {
        if (getScrollY() < mRefreshViewHeight) return;
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, mRefreshViewHeight,
                mTopViewHeight + mRefreshViewHeight);
        invalidate();
    }

    private boolean isAutoPlay = true;
    @Override
    public void scrollTo(int x, int y) {
//        if (y < 0) {
//            y = 0;
//        }
        if (y < mRefreshViewHeight) {
            if (isAutoPlay && mTop instanceof RecyclerViewBanner) {
                RecyclerViewBanner top = (RecyclerViewBanner) mTop;
                isAutoPlay = false;
                top.setRvAutoPlaying(false);
            }
        } else if (y >= mRefreshViewHeight && y < mRefreshViewHeight + mTopViewHeight){
            if (!isAutoPlay && mTop instanceof RecyclerViewBanner) {
                RecyclerViewBanner top = (RecyclerViewBanner) mTop;
                isAutoPlay = true;
                top.setRvAutoPlaying(true);
            }
        } else {
            if (isAutoPlay && mTop instanceof RecyclerViewBanner) {
                RecyclerViewBanner top = (RecyclerViewBanner) mTop;
                isAutoPlay = false;
                top.setRvAutoPlaying(false);
            }
        }
        if (y > mTopViewHeight + mRefreshViewHeight) {
            y = mTopViewHeight + mRefreshViewHeight;
        }
        if (y != getScrollY()) {
            if (y >= mRefreshViewHeight && mState == STATE_DONE) {
                mState = STATE_NORMAL;
                stateNormal();
            } else if (y <= 0 && mState == STATE_NORMAL) {
                mState = STATE_RELEASE_TO_REFRESH;
                stateReleaseToRefresh();
            } else if (y > 0 && mState == STATE_RELEASE_TO_REFRESH) {
                mState = STATE_NORMAL;
                stateNormal();
            }
            super.scrollTo(x, y);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private void stateNormal() {
        mPacman.setVisibility(GONE);
        mRefreshArrow.setVisibility(VISIBLE);
        mRefreshText.setVisibility(VISIBLE);
        mRefreshText.setText(R.string.listview_header_hint_normal);
        if (mRefreshArrow.getRotation() != 0) {
            if (mArrowAnimatorToRefresh.isRunning()) {
                mArrowAnimatorToRefresh.cancel();
            }
            if (mArrowAnimatorToNormal.isRunning()) {
                mArrowAnimatorToNormal.cancel();
            }
            mArrowAnimatorToNormal.start();
        }
    }

    private void stateReleaseToRefresh() {
        mPacman.setVisibility(GONE);
        mRefreshArrow.setVisibility(VISIBLE);
        mRefreshText.setVisibility(VISIBLE);
        mRefreshText.setText(R.string.listview_header_hint_release);
        if (mArrowAnimatorToRefresh.isRunning()) {
            mArrowAnimatorToRefresh.cancel();
        }
        if (mArrowAnimatorToNormal.isRunning()) {
            mArrowAnimatorToNormal.cancel();
        }
        mArrowAnimatorToRefresh.start();
    }

    private void stateRefreshing() {
        mPacman.setVisibility(VISIBLE);
        mRefreshArrow.setVisibility(GONE);
        mRefreshText.setVisibility(GONE);
    }
    private void stateRefreshComplete() {
        mPacman.setVisibility(GONE);
        mRefreshArrow.setVisibility(VISIBLE);
        mRefreshText.setVisibility(VISIBLE);
        mRefreshText.setText(R.string.refresh_done);
        mRefreshArrow.setRotation(0);
    }

    public void disableScrolling() {
        scrollEnabled = false;
    }

    public void enableScrolling() {
        scrollEnabled = true;
    }

    public interface OnRefreshListener {
        void onRefresh();
        void onLoadMore();
    }
}