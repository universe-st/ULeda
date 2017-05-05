package ecnu.uleda.view_controller.widgets;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ecnu.uleda.tool.UPublicTool;


/**
 * Created by jimmyhsu on 2017/5/2.
 */

public class BannerView extends FrameLayout {

    public static final int DEFAULT_TITLE_BG = 0xaa777777;
    public static final int DEFAULT_TITLE_TEXT_COLOR = 0xffffffff;

    private Holder mHolder;
    private BannerAdapter mAdapter;

    private ViewPager mPager;
    private TextView mTitleView;

    private ValueAnimator mBgAnimator;
    private ValueAnimator mTextAnimator;

    private int mCurrentBg;
    private int mCurrentTextColor;

    private boolean isAutoPlay = true;

//    private OnItemClickedListener mListener;

    private boolean isInited = false;

    private Timer mTimer;
    private TimerTask mTask;
    private Handler mHandler;
    private long mInterval;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPager = new ViewPager(getContext());
        addView(mPager, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setHolder(Holder holder) {
        if (holder != null && holder.mDatas != null && holder.mDatas.size() > 0) {
            mHolder = holder;
            mPager.setAdapter(mAdapter = new BannerAdapter(mHolder));
            mPager.setOffscreenPageLimit(mHolder.mDatas.size() + 2);
            init();

            if (mHolder.showTitles()) {
                mTitleView = new TextView(getContext());
                mTitleView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
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
                if (mCurrentBg > 0) {
                    mTitleView.setBackgroundColor(mCurrentBg);
                } else {
                    mTitleView.setBackgroundColor(DEFAULT_TITLE_BG);
                }
            }
        }
    }

//    public void setOnItemClickedListener(OnItemClickedListener listener) {
//        mListener = listener;
//    }

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
                            DEFAULT_TITLE_BG : mHolder.getTitleBgColor(mAdapter.getRealPosition(position));
                    int newTitleTextColor = mHolder.getTitleTextColor(mAdapter.getRealPosition(position)) == 0 ?
                            DEFAULT_TITLE_TEXT_COLOR : mHolder.getTitleTextColor(mAdapter.getRealPosition(position));
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
        mHandler = new AutoScrollHandler(mPager);
    }

    private void startTimer() {
        if (!isAutoPlay) return;
        if (mHolder != null)
            mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0x110);
            }
        };
        mTimer.schedule(mTask, mInterval, mInterval);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTask = null;
        }
    }

    public void startScrolling(long interval) {
        isAutoPlay = true;
        mInterval = interval;
        startTimer();
    }

    public void stopScrolling() {
        isAutoPlay = false;
        stopTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            startTimer();
        } else {
            stopTimer();
        }
        return super.dispatchTouchEvent(event);
    }

    public void setAutoPlay(boolean flag) {
        isAutoPlay = flag;
    }

    private static class BannerAdapter extends PagerAdapter {

        private WeakReference<Holder> mReferenceHolder;

        public BannerAdapter(Holder holder) {
            mReferenceHolder = new WeakReference<>(holder);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View contentView = mReferenceHolder.get().getView(getRealPosition(position),
                    mReferenceHolder.get().mDatas.get(getRealPosition(position)));
            container.addView(contentView);
            contentView.setTag(position);
            contentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReferenceHolder.get().onItemClicked((Integer) contentView.getTag(), v);
                }
            });
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

        public abstract void onItemClicked(int pos, View v);
    }

    private static class AutoScrollHandler extends Handler {

        private WeakReference<ViewPager> mReferencePager;

        public AutoScrollHandler(ViewPager pager) {
            super(Looper.getMainLooper());
            mReferencePager = new WeakReference<>(pager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mReferencePager.get().setCurrentItem(mReferencePager.get().getCurrentItem() + 1);
        }
    }

//    interface OnItemClickedListener {
//        void OnItemClicked(int pos, View v);
//    }
}
