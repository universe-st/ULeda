package ecnu.uleda.view_controller.task.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.task.activity.ActivityDetailsActivity;
import ecnu.uleda.view_controller.widgets.BannerView;
import ecnu.uleda.view_controller.widgets.NoScrollViewPager;
import ecnu.uleda.view_controller.widgets.StickyNavLayout;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class TaskActivityFragment extends Fragment implements StickyNavLayout.OnRefreshListener, ActivityListFragment.OnRefreshListener {

    private static final long BANNER_INTERVAL = 3000;
    private static final String[] TYPES = {"全部", "运动", "社团", "公益"};
    private static final int MESSAGE_REFRESH_COMPLETE = 0x110;
    private Unbinder mUnbinder;
    private Handler mHandler;
    private ActivityListFragment mInnerFragment;

    private int[] mTitleBgColors;
    private int[] mTitleTextColors;
    private boolean isRefreshing = false;

    private List<UActivity> mProActivities = new ArrayList<>();

    @BindView(R.id.stickynavlayout)
    StickyNavLayout mContainer;
    @BindView(R.id.id_stickynavlayout_topview)
    BannerView mBanner;
    @BindView(R.id.id_stickynavlayout_indicator)
    TabLayout mIndicator;
    @BindView(R.id.id_stickynavlayout_viewpager)
    NoScrollViewPager mPager;

    private static TaskActivityFragment mInstance;

    public static Fragment getInstance() {
        if (mInstance == null) {
            synchronized (TaskActivityFragment.class) {
                if (mInstance == null) {
                    mInstance = new TaskActivityFragment();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_activity_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHandler();
        initPager();
        initIndicator();
        getPromotedActivities();
        initRollPager();
        initContainer();
    }

    private void initContainer() {
        mContainer.setOnRefreshListener(this);
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_REFRESH_COMPLETE:
                        if (isRefreshing) {
                            mContainer.refreshComplete();
                            mBanner.startScrolling(BANNER_INTERVAL);
                        }
                        break;
                }
            }
        };

    }

    private void initPager() {
        mPager.setNoScroll(true);
        mPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                mInnerFragment = ActivityListFragment.getInstance();
                mInnerFragment.setOnRefreshListener(TaskActivityFragment.this);
                return mInnerFragment = ActivityListFragment.getInstance();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    private void initIndicator() {
        for (String title : TYPES)
            mIndicator.addTab(mIndicator.newTab().setText(title));
        mIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mInnerFragment != null) {
                    mInnerFragment.setTag(tab.getText());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getPromotedActivities() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                UserOperatorController uoc = UserOperatorController.getInstance();
                e.onNext(ServerAccessApi.getPromotedActivities(uoc.getId(), uoc.getPassport()));
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            mProActivities = parseActivitiesJsonArray(new JSONArray(response.getData()));
                        }
                    }
                });
    }

    private List<UActivity> parseActivitiesJsonArray(JSONArray pros) throws JSONException {
        List<UActivity> proActivities = new ArrayList<>();
        int length = pros.length();
        for (int i = 0; i < length; i++) {
            JSONObject proAct = pros.getJSONObject(i);
            ArrayList<String> imgUrls = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                if (!TextUtils.isEmpty(proAct.getString("pic" + j)) &&
                        !proAct.getString("pic" + j).equals("null")) {
                    imgUrls.add(UPublicTool.BASE_URL_PICTURE + proAct.getString("pic" + j));
                }
            }
            proActivities.add(new UActivity(proAct.getString("act_title"),
                    proAct.getDouble("lat"),
                    proAct.getDouble("lon"),
                    proAct.getString("location"),
                    proAct.getString("tag"),
                    proAct.getInt("author_id"),
                    "no",
                    "no",
                    proAct.getString("description"),
                    System.currentTimeMillis() + proAct.getLong("active_time"),
                    proAct.getInt("taker_count_limit"),
                    imgUrls,
                    proAct.getInt("act_id"),
                    proAct.getInt("status"),
                    proAct.getInt("postdate")));
        }
        return proActivities;
    }

    private void initRollPager() {
        final List<String> datas = new ArrayList<>();
        datas.add(UPublicTool.BASE_URL_PICTURE + "pic-1509967730fUjVHHfvjy9M28B29d.jpg");
        datas.add(UPublicTool.BASE_URL_PICTURE + "pic-1509967778jvR9c5w8ZTOvlZToA4.jpg");
        datas.add(UPublicTool.BASE_URL_PICTURE + "pic-1509967823DzGmyfz0OBsofJey8W.jpg");
        mTitleBgColors = new int[datas.size()];
        mTitleTextColors = new int[datas.size()];
        final List<String> titles = new ArrayList<>();
        titles.add("计软院迎新晚会");
        titles.add("寝室文化节");
        titles.add("计软院运会");
        mBanner.setHolder(new BannerView.Holder<String>(datas) {
            @Override
            public View getView(final int pos, String item) {
                final ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(getContext())
                        .load(item)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bm, GlideAnimation<? super Bitmap> glideAnimation) {
                                Palette.Builder builder = Palette.from(bm);
                                Palette palette = builder.generate();
                                Palette.Swatch swatch = null;
                                swatch = palette.getMutedSwatch();
                                if (swatch == null) {
                                    swatch = palette.getDarkMutedSwatch();
                                }
                                mTitleBgColors[pos] = swatch == null ? BannerView.DEFAULT_TITLE_BG : swatch.getRgb();
                                mTitleTextColors[pos] = swatch == null ? BannerView.DEFAULT_TITLE_TEXT_COLOR : swatch.getTitleTextColor();
                                imageView.setImageBitmap(bm);
                            }
                        });
                return imageView;
            }

            @Override
            public String getTitle(int pos) {
                return titles.get(pos);
            }

            @Override
            public boolean showTitles() {
                return true;
            }

            @Override
            public int getTitleBgColor(int pos) {
                return mTitleBgColors[pos];
            }

            @Override
            public int getTitleTextColor(int pos) {
                return mTitleTextColors[pos];
            }

            @Override
            public void onItemClicked(int pos, View v) {
                if (pos >= mProActivities.size()) return;
                UActivity act = mProActivities.get(pos);
                ActivityDetailsActivity.startActivity(getContext(), act);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.startScrolling(BANNER_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBanner.stopScrolling();
    }

    @Override
    public void onRefresh() {
        mBanner.stopScrolling();
        if (mInnerFragment != null) {
            isRefreshing = true;
            mInnerFragment.refresh();
        }
    }

    @Override
    public void onRefreshComplete() {
        mHandler.sendEmptyMessage(MESSAGE_REFRESH_COMPLETE);
    }
}
