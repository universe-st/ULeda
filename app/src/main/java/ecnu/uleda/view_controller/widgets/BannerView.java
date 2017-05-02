package ecnu.uleda.view_controller.widgets;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import ecnu.uleda.tool.UPublicTool;


/**
 * Created by jimmyhsu on 2017/5/2.
 */

public class BannerView extends FrameLayout {

    private Holder mHolder;
    private BannerAdapter mAdapter;

    private ViewPager mPager;
    private TextView mTitleView;

    private ValueAnimator mBgAnimator;
    private ValueAnimator mTextAnimator;

    private int mCurrentBg;
    private int mCurrentTextColor;

    private boolean isInited = false;
    private boolean isDragging = false;
    private float mLastX;
    private float mLastY;
    private int mTouchSlop;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mPager = new ViewPager(getContext());
        addView(mPager, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setHolder(Holder holder) {
        if (holder != null && holder.mDatas != null && holder.mDatas.size() > 0) {
            mHolder = holder;
            mPager.setAdapter(mAdapter = new BannerAdapter(mHolder, mTitleView, mPager));
            mPager.setOffscreenPageLimit(mHolder.mDatas.size() + 2);
            init();

            if (mHolder.showTitles()) {
                mTitleView = new TextView(getContext());

                mTitleView.setText(mHolder.getTitle(1));
                mTitleView.setTextSize(16);
                mTitleView.setMaxLines(1);
                mTitleView.setEllipsize(TextUtils.TruncateAt.END);
                int hPadding = (int) UPublicTool.dp2px(getContext(), 12);
                int vPadding = (int) UPublicTool.dp2px(getContext(), 8);
                mTitleView.setPadding(hPadding, vPadding, hPadding, vPadding);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.BOTTOM;
                addView(mTitleView, 1, lp);
//                mCurrentBg = mHolder.getTitleBackgroundColor(0);
                if (mCurrentBg > 0) {
                    mTitleView.setBackgroundColor(mCurrentBg);
                } else {
                    mTitleView.setBackgroundColor(0xaa777777);
                }
            }
        }
    }

    private void init() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageSelected(final int position) {

                if (mTitleView != null) {
                    int realPos = mAdapter.getRealPosition(position);
                    mTitleView.setText(mHolder.getTitle(realPos));
                    int newTitleBg = mHolder.getTitleBgColor(mAdapter.getRealPosition(position)) == 0 ?
                            0xaa777777 : mHolder.getTitleBgColor(mAdapter.getRealPosition(position));
                    int newTitleTextColor = mHolder.getTitleTextColor(mAdapter.getRealPosition(position)) == 0 ?
                            0xffffffff : mHolder.getTitleTextColor(mAdapter.getRealPosition(position));
                    if (mBgAnimator != null && mBgAnimator.isRunning()) {
                        mBgAnimator.cancel();
                    }
                    if (mTextAnimator != null && mTextAnimator.isRunning()) {
                        mTextAnimator.cancel();
                    }
                    mBgAnimator = ValueAnimator.ofArgb(mCurrentBg, newTitleBg).setDuration(500);
                    mBgAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    mBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mTitleView.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                    mTextAnimator = ValueAnimator.ofArgb(mCurrentTextColor, newTitleTextColor).setDuration(500);
                    mTextAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    mTextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mTitleView.setTextColor((Integer) animation.getAnimatedValue());
                        }
                    });
                    mBgAnimator.start();
                    mTextAnimator.start();
                    mCurrentBg = newTitleBg;
                    mCurrentTextColor = newTitleTextColor;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int position = mPager.getCurrentItem();
                    if (position == 0) {
                        mPager.setCurrentItem(mHolder.mDatas.size(), false);
                    } else if (position == mHolder.mDatas.size() + 1) {
                        mPager.setCurrentItem(1, false);
                    }
                }
            }
        });
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isInited) {
                    isInited = true;
                    mPager.setCurrentItem(1, false);
                    mCurrentBg = mHolder.getTitleBgColor(0);
                    mCurrentTextColor = mHolder.getTitleTextColor(0);
                    mTitleView.setBackgroundColor(mCurrentBg);
                    mTitleView.setTextColor(mCurrentTextColor);
                }
                return true;
            }
        });
    }

    private static class BannerAdapter extends PagerAdapter {

        private WeakReference<Holder> mReferenceHolder;

        public BannerAdapter(Holder holder, TextView tv, ViewPager pager) {
            mReferenceHolder = new WeakReference<>(holder);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View contentView = mReferenceHolder.get().getView(getRealPosition(position),
                    mReferenceHolder.get().mDatas.get(getRealPosition(position)));
            container.addView(contentView);
            return contentView;
        }

        @Override
        public int getCount() {
            return mReferenceHolder.get().mDatas.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private int getRealPosition(int pos) {
            if (pos == 0) {
                return mReferenceHolder.get().mDatas.size() - 1;
            } else if (pos == mReferenceHolder.get().mDatas.size() + 1) {
                return 0;
            } else {
                return pos - 1;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (isDragging) break;
                if (Math.abs(dx * dx + dy * dy) > mTouchSlop) {
                    isDragging = true;
                    if (Math.abs(dx) * 2 > Math.abs(dy)) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public static abstract class Holder<T> {
        private List<T> mDatas;

        public Holder(List<T> mDatas) {
            this.mDatas = mDatas;
        }

        public abstract View getView(int pos, T item);

        public abstract String getTitle(int pos);

        public abstract boolean showTitles();

        public abstract int getTitleBgColor(int pos);

        public abstract int getTitleTextColor(int pos);
    }
}
