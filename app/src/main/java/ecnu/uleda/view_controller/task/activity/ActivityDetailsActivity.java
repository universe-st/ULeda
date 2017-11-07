package ecnu.uleda.view_controller.task.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import net.phalapi.sdk.PhalApiClientResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.CommonBigImageActivity;
import ecnu.uleda.view_controller.task.adapter.TakersAdapter;
import ecnu.uleda.view_controller.widgets.BannerView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadResult;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public class ActivityDetailsActivity extends BaseDetailsActivity {

    private static final String EXTRA_ACTIVITY = "extra_activity";
    private static final int REQUEST_EDIT = 1;

    private static final String BUNDLE_DESC = "bundle_desc";
    private static final String BUNDLE_USERNAME = "bundle_username";
    private static final String BUNDLE_PARTICIPATE_COUNT = "bundle_participate_count";
    private static final String BUNDLE_LATITUDE = "bundle_latitude";
    private static final String BUNDLE_LONGITUDE = "bundle_longitude";

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.head_line_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title_text)
    TextView mTitleText;
    @BindView(R.id.activity_detail_publisher_name)
    TextView mUsernameView;
    @BindView(R.id.activity_detail_circle_image)
    SketchImageView mAvatarView;
    @BindView(R.id.activity_detail_info)
    TextView mDescView;
    @BindView(R.id.activity_map_shader)
    View mMapShader;
    @BindView(R.id.activity_tag)
    TextView mTagView;
    @BindView(R.id.activity_title)
    TextView mTitleView;
    @BindView(R.id.activity_location)
    TextView mLocationText;
    @BindView(R.id.activity_detail_state)
    TextView mTimeView;
    @BindView(R.id.activity_banner)
    BannerView mBanner;
    @BindView(R.id.activity_detail_map)
    MapView mMapView;
    @BindView(R.id.right_button)
    Button mRightButton;
    @BindView(R.id.task_detail_list_view)
    LinearLayout mDetailContainer;
    @BindView(R.id.activity_takers_list)
    RecyclerView mTakersList;
    @BindView(R.id.activity_takers_title)
    TextView mTakersTitleView;
    @BindView(R.id.activity_detail_participate_count)
    TextView mMaxTakersCount;
    @BindView(R.id.activity_map_toggle)
    FloatingActionButton mMapToggle;
    @BindView(R.id.activity_detail_comment_container)
    LinearLayout mCommentContainer;
    private ProgressDialog mProgress;

    private List<String> mUrls = new ArrayList<>();
    private List<UserInfo> mTakerInfos;
    private TakersAdapter mTakerAdapter;
    private boolean isSelfParticipated = false;
    private UActivity mActivity;

    private boolean isMapOpen = false;
    private TencentMap mTMap;


    private int mOffSet = 0;
    private ObjectAnimator mShowTitleAnimator;
    private ObjectAnimator mHideTitleAnimator;
    private boolean isTitleShown = false;

    private void translucentStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void parseData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINA);
        mTitleView.setText(mActivity.getTitle());
        mTagView.setText(mActivity.getTag());
        mTimeView.setText(df.format(mActivity.getHoldTime()));
        mDescView.setText(mActivity.getDescription());
        mLocationText.setText(mActivity.getLocation());
        mMaxTakersCount.setText("最大参加人数：" + mActivity.getTakersCount());
        mUrls = mActivity.getImgUrls();
        LatLng position = new LatLng(31.22847,
                121.40640);
        mTMap.setZoom(18);
        mTMap.setCenter(position);
        Marker marker = mTMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.defaultMarker()).draggable(false)
        );
        marker.setTitle(mActivity.getLocation());
        String avatarUrl = mActivity.getAvatar();
        DisplayOptions opt = new DisplayOptions();
        opt.setImageShaper(new CircleImageShaper());
        mAvatarView.setOptions(opt);
        if (avatarUrl.startsWith("http")) {
            mAvatarView.displayImage(avatarUrl);
        } else {
            // for testing
            mAvatarView.displayResourceImage(R.drawable.xiaohong);
        }
        initBanner();
    }


    private void initBanner() {
        final int[] colors = new int[mUrls.size()];
        final int[] textColors = new int[mUrls.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = BannerView.DEFAULT_TITLE_BG;
            textColors[i] = BannerView.DEFAULT_TITLE_TEXT_COLOR;
        }
        mBanner.setAutoPlay(false);
        if (mUrls.size() <= 0) {
            mBanner.setVisibility(View.GONE);
            mMapToggle.setVisibility(View.GONE);
            mMapView.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            mMapShader.setVisibility(View.VISIBLE);
            mMapShader.startAnimation(fadeIn);
        } else {
            mBanner.setVisibility(View.VISIBLE);
            mMapToggle.setVisibility(View.VISIBLE);
            mMapShader.setVisibility(View.GONE);
        }
        mBanner.setHolder(new BannerView.Holder<String>(mUrls) {
            // for testing
            private final int[] RESOURCES = {R.drawable.img1, R.drawable.img2, R.drawable.img3};

            @Override
            public View getView(final int pos, String item) {
                final SketchImageView imageView = new SketchImageView(ActivityDetailsActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.displayResourceImage(R.drawable.ic_landscape_grey600_48dp);
                if (item.startsWith("http")) {
                    Sketch sketch = Sketch.with(ActivityDetailsActivity.this);
                    sketch.load(item, new LoadListener() {
                        @Override
                        public void onCompleted(LoadResult loadResult) {
                            Bitmap bm = loadResult.getBitmap();
                            Palette.Builder paletteBuilder = Palette.from(bm);
                            Palette palette = paletteBuilder.generate();
                            Palette.Swatch swatch = palette.getMutedSwatch();
                            colors[pos] = swatch == null ?
                                    BannerView.DEFAULT_TITLE_BG : swatch.getRgb();
                            textColors[pos] = swatch == null ?
                                    BannerView.DEFAULT_TITLE_TEXT_COLOR : swatch.getTitleTextColor();
                            imageView.setImageBitmap(bm);
                        }

                        @Override
                        public void onStarted() {

                        }

                        @Override
                        public void onError(ErrorCause errorCause) {

                        }

                        @Override
                        public void onCanceled(CancelCause cancelCause) {

                        }
                    });
                    imageView.displayImage(item);
                } else {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), RESOURCES[pos]);
                    Palette.Builder paletteBuilder = Palette.from(bm);
                    Palette palette = paletteBuilder.generate();
                    Palette.Swatch swatch = palette.getMutedSwatch();
                    colors[pos] = swatch == null ?
                            BannerView.DEFAULT_TITLE_BG : swatch.getRgb();
                    textColors[pos] = swatch == null ?
                            BannerView.DEFAULT_TITLE_TEXT_COLOR : swatch.getTitleTextColor();
                    imageView.setImageBitmap(bm);
                }
                return imageView;
            }

            @Override
            public String getTitle(int pos) {
                return String.valueOf(pos + 1) + "/" + (mUrls.size());
            }

            @Override
            public boolean showTitles() {
                return true;
            }

            @Override
            public int getTitleBgColor(int pos) {
                return 0x01000000;
            }

            @Override
            public int getTitleTextColor(int pos) {
                return textColors[pos];
            }

            @Override
            public void onItemClicked(int pos, View v) {
                if (mOffSet == 0) {
                    gotoCommonBigImage(mUrls.get(pos), v);
                } else {
                    mAppBar.setExpanded(true, true);
                }
            }


        });
    }

    private void gotoCommonBigImage(String url, View v) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(ActivityDetailsActivity.this, v, getString(R.string.transition_big_image));
        CommonBigImageActivity.startActivity(ActivityDetailsActivity.this, url, options);
    }

    private void initCollapsingToolbar() {
        mCollapsingToolbar.setTitleEnabled(false);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mOffSet = verticalOffset;
                if (-verticalOffset >= appBarLayout.getTotalScrollRange() && !isTitleShown) {
                    isTitleShown = true;
                    stopAllAnimation();
                    mShowTitleAnimator.start();
                } else if (isTitleShown) {
                    isTitleShown = false;
                    stopAllAnimation();
                    mHideTitleAnimator.start();
                }
            }
        });
        mShowTitleAnimator = ObjectAnimator.ofFloat(mTitleText, "alpha", 0, 1)
                .setDuration(300);
        mShowTitleAnimator.setInterpolator(new LinearInterpolator());
        mHideTitleAnimator = ObjectAnimator.ofFloat(mTitleText, "alpha", 1, 0)
                .setDuration(300);
        mHideTitleAnimator.setInterpolator(new LinearInterpolator());
    }


    private void initTakersList() {
        UserOperatorController uoc = UserOperatorController.getInstance();
        mTakerInfos = new ArrayList<>();
        if (mActivity.getAuthorUsername().equals(uoc.getUserName())) {
            mTakersList.setVisibility(View.VISIBLE);
            mTakersTitleView.setVisibility(View.VISIBLE);
            mTakersList.setAdapter(mTakerAdapter = new TakersAdapter(this, mTakerInfos));
            mTakersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                    false));
        } else {
            mTakersList.setVisibility(View.GONE);
        }
        initTakersData();
    }

    private void initTakersData() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                UserOperatorController uoc = UserOperatorController.getInstance();
                e.onNext(ServerAccessApi.getActivityTakers(uoc.getId(),
                        uoc.getPassport(),
                        String.valueOf(mActivity.getId())));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            JSONArray takersArray = new JSONArray(response.getData());
                            mTakerInfos.clear();
                            int length = takersArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject taker = takersArray.getJSONObject(i);
                                UserInfo user = new UserInfo();
                                if (taker.getString("taker_id").equals(UserOperatorController.getInstance().getId())) {
                                    mRightButton.setText("取消参加");
                                    isSelfParticipated = true;
                                }
                                user.setId(taker.getString("taker_id"))
                                        .setAvatar(UPublicTool.BASE_URL_AVATAR + taker.getString("avatar"))
                                        .setUserName(taker.getString("username"));
                                mTakerInfos.add(user);
                            }
                            if (mTakerAdapter != null && mTakersList.getVisibility() != View.GONE) {
                                mTakerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(ActivityDetailsActivity.this, "获取参与者失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void stopAllAnimation() {
        if (mShowTitleAnimator.isRunning()) mShowTitleAnimator.cancel();
        if (mHideTitleAnimator.isRunning()) mHideTitleAnimator.cancel();
    }

    private void onRightButtonClick() {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getId().equals(String.valueOf(mActivity.getAuthorId())) &&
                mActivity.getStatus() == 0) { // 不是发布者，且活动未开始
            if (!isSelfParticipated) {
                participateInActivity(uoc.getId(), uoc.getPassport(), String.valueOf(mActivity.getId()));
            } else {
                cancelParticipateInActivity(uoc.getId(), uoc.getPassport(), String.valueOf(mActivity.getId()));
            }
        } else if (mActivity.getStatus() == 0) { // 是发布者，且活动未开始
            ActivityEditActivity.startActivity(this, mActivity);
        }
    }

    private void participateInActivity(final String id, final String passport, final String actId) {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(ServerAccessApi.participateInActivity(id, passport, actId));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200 && response.getData().equals("success")) {
                            mRightButton.setText("取消参加");
                            isSelfParticipated = true;
                            Toast.makeText(ActivityDetailsActivity.this, "报名成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityDetailsActivity.this, "报名失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(ActivityDetailsActivity.this, "报名失败：网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelParticipateInActivity(final String id, final String passport, final String actId) {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(ServerAccessApi.cancelParticipateInActivity(id, passport, actId));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200 && response.getData().equals("success")) {
                            mRightButton.setText("参加");
                            isSelfParticipated = false;
                            Toast.makeText(ActivityDetailsActivity.this, "取消报名成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityDetailsActivity.this, "取消报名失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(ActivityDetailsActivity.this, "取消报名失败：网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void toggleMap() {
        if (!isMapOpen) {
            Animator animation = getCircularReveal();
            animation.setDuration(400);
            animation.setInterpolator(new AccelerateInterpolator());
            mMapView.setVisibility(View.VISIBLE);
            animation.start();
            mMapShader.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            mMapShader.startAnimation(fadeIn);
            mCollapsingToolbar.setExpandedTitleColor(0x00000000);
        } else {
            Animator animator = getCircularClose();
            animator.setDuration(400);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mMapView.setVisibility(View.GONE);
                }
            });
            animator.start();
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMapShader.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mMapShader.startAnimation(fadeOut);
            mCollapsingToolbar.setExpandedTitleColor(0xffffffff);
        }
        isMapOpen = !isMapOpen;
    }

    private Animator getCircularReveal() {
        return ViewAnimationUtils.createCircularReveal(mMapView,
                (int) (mBanner.getWidth() - UPublicTool.dp2px(this, 44)),
                mBanner.getHeight(), 0, (float) Math.sqrt(mBanner.getWidth() * mBanner.getWidth() +
                        mBanner.getHeight() * mBanner.getHeight()));
    }

    private Animator getCircularClose() {
        return ViewAnimationUtils.createCircularReveal(mMapView,
                (int) (mBanner.getWidth() - UPublicTool.dp2px(this, 44)),
                mBanner.getHeight(), (float) Math.sqrt(mBanner.getWidth() * mBanner.getWidth() +
                        mBanner.getHeight() * mBanner.getHeight()), 0);
    }


    @Override
    protected void onStop() {
        if (mMapView != null) {
            mMapView.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    private void loadDataFromServer() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                UserOperatorController uoc = UserOperatorController.getInstance();
                e.onNext(ServerAccessApi.getActivityDetail(uoc.getId(),
                        uoc.getPassport(),
                        String.valueOf(mActivity.getId())));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            JSONObject activityObj = new JSONObject(response.getData());
                            mActivity.setTitle(activityObj.getString("act_title"));
                            mActivity.setAuthorId(activityObj.getInt("author_id"));
                            mActivity.setTag(activityObj.getString("tag"));
                            mActivity.setStatus(activityObj.getInt("status"));
                            mActivity.setDescription(activityObj.getString("description"));
                            mActivity.setHoldTime(System.currentTimeMillis() + activityObj.getLong("active_time"));
                            mActivity.setLat(activityObj.getDouble("lat"));
                            mActivity.setLon(activityObj.getDouble("lon"));
                            mActivity.setTakersCount(activityObj.getInt("taker_count_limit"));
                            mActivity.setLocation(activityObj.getString("location"));
                            parseData();
                            initTakersList();
                            initRightButton();
                        } else {
                            Toast.makeText(ActivityDetailsActivity.this, "获取活动详情失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(ActivityDetailsActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initRightButton() {
        // 对于已经开始或者取消的活动，所有用户的处理是一致的
        if (mActivity.getStatus() > 0) {
            mRightButton.setEnabled(false);
            mRightButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            mRightButton.setEnabled(true);
            mRightButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorUMain));
        }
        if (mActivity.getStatus() == 1) { // 进行中
            mRightButton.setText("进行中");
        } else if (mActivity.getStatus() == 2) { // 活动正常结束
            mRightButton.setText("已结束");
        } else if (mActivity.getStatus() == 3) {
            mRightButton.setText("已取消"); // 被发布者取消
        }
        // 以下是未开始活动的处理
        // 发布者
        if (String.valueOf(mActivity.getAuthorId()).equals(UserOperatorController.getInstance().getId())
                && mActivity.getStatus() == 0) {
            mRightButton.setText("编辑活动");
        } else if (mActivity.getStatus() == 0) { // 参与者
            // TODO 判断是参加还是取消参加任务
        }
    }

    @OnClick(R.id.comment_bt)
    void comment() {
        showCommentPopup();
    }

    @Override
    public void initActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        initProgressBar();
        Intent data = getIntent();
        mActivity = (UActivity) data.getSerializableExtra(EXTRA_ACTIVITY);
        mUrls = mActivity.getImgUrls();
        loadDataFromServer();
        initMap(savedInstanceState);
        initCollapsingToolbar();
        initClickEvents();
    }

    private void initMap(Bundle savedInstanceState) {
        mTMap = mMapView.getMap();
        mMapView.onCreate(savedInstanceState);
//        UiSettings uiSettings = mMapView.getUiSettings();
//        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
//        uiSettings.setZoomGesturesEnabled(true);
//        uiSettings.setScrollGesturesEnabled(true);
    }

    private void initProgressBar() {
        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setMessage("发布中...");
        mProgress.setCancelable(false);
    }

    private void initClickEvents() {
        RxView.clicks(mRightButton)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onRightButtonClick();
                    }
                });
        RxView.clicks(mMapToggle)
                .throttleFirst(400, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        toggleMap();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            loadDataFromServer();
        }
    }

    @Override
    public void initContentView() {
        translucentStatusBar();
        setContentView(R.layout.activity_details);
    }

    @Override
    public void onSubmitComment(@NotNull final String comment) {
        mProgress.show();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Thread.sleep(500);
                e.onNext("success");
//                UserOperatorController uoc = UserOperatorController.getInstance();
//                e.onNext(ServerAccessApi.postActivityComment(uoc.getId(), uoc.getPassport(),
//                        String.valueOf(mActivity.getId()), comment,
//                        String.valueOf(System.currentTimeMillis() / 1000)));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
//                        if (response.getRet() == 200 && response.getMsg().equals("success")) {
//                            Toast.makeText(ActivityDetailsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            addCommentView(comment, mCommentContainer, 0);
                        mProgress.dismiss();
//                        } else {
//                            Toast.makeText(ActivityDetailsActivity.this,
//                                    TextUtils.isEmpty(response.getMsg()) ? "未知异常" : response.getMsg(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(ActivityDetailsActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NotNull
    @Override
    public View getChatView(@NotNull TaskDetailsActivity.UserChatItem userChatItem) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(this);
        if (mActivity.getAuthorUsername().equals(userChatItem.name)) {
            v = inflater.inflate(R.layout.task_detail_chat_item_right, mDetailContainer, false);
        } else {
            v = inflater.inflate(R.layout.task_detail_chat_item_left, mDetailContainer, false);
        }
        SketchImageView avatar = (SketchImageView) v.findViewById(R.id.task_detail_chat_item_circle);
        DisplayOptions options = new DisplayOptions().setImageShaper(new CircleImageShaper());
        avatar.setOptions(options);
        // for testing
        if (userChatItem.authorAvatar.equals("test")) {
            avatar.displayResourceImage(R.drawable.model1);
        } else {
            avatar.displayImage(userChatItem.authorAvatar);
        }
        TextView tv = (TextView) v.findViewById(R.id.say_what);
        tv.setText(userChatItem.sayWhat);
        tv = (TextView) v.findViewById(R.id.time_before);
        tv.setText(UPublicTool.timeBefore(userChatItem.postDate));
        tv = (TextView) v.findViewById(R.id.name_of_chatter);
        tv.setText(userChatItem.name);
        return v;
    }

//    private class LoadActivityService implements Runnable {
//        @Override
//        public void run() {
//            try {
//                // for testing
//                Thread.sleep(1000);
//                Bundle bundle = new Bundle();
//                bundle.putString(BUNDLE_USERNAME, "小明");
//                bundle.putString(BUNDLE_DESC, "我是一个无聊的活动\n我没有什么特别的\n\n\n\n\n测试\n哈哈");
//                bundle.putInt(BUNDLE_PARTICIPATE_COUNT, 20);
//                bundle.putDouble(BUNDLE_LATITUDE, 31.2276926429);
//                bundle.putDouble(BUNDLE_LONGITUDE, 121.4040112495);
//                Message msg = Message.obtain();
//                msg.what = MSG_LOAD_COMPLETE;
//                msg.setData(bundle);
//                mHandler.sendMessage(msg);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void startActivity(Context context, UActivity activity) {
        Intent intent = new Intent(context, ActivityDetailsActivity.class)
                .putExtra(EXTRA_ACTIVITY, activity);
        context.startActivity(intent);
    }

}
