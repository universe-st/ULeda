package ecnu.uleda.view_controller.task.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.widgets.BannerView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadResult;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public class ActivityDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_TAG = "extra_tag";
    private static final String EXTRA_AVATAR_URL = "extra_avatar_url";
    private static final String EXTRA_TIME = "extra_time";
    private static final String EXTRA_LOCATION = "extra_location";
    private static final String EXTRA_URLS = "extra_urls";

    private static final int MSG_LOAD_COMPLETE = 1;
    private static final String BUNDLE_DESC = "bundle_desc";
    private static final String BUNDLE_USERNAME = "bundle_username";
    private static final String BUNDLE_PARTICIPATE_COUNT = "bundle_participate_count";
    private static final String BUNDLE_LATITUDE = "bundle_latitude";
    private static final String BUNDLE_LONGITUDE = "bundle_longitude";

    private DetailsHandler mHandler;

    private boolean isMapOpen = false;

    @BindView(R.id.head_line_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_detail_publisher_name)
    TextView mUsernameView;
    @BindView(R.id.activity_detail_circle_image)
    SketchImageView mAvatarView;
    @BindView(R.id.activity_detail_info)
    TextView mDescView;
    @BindView(R.id.activity_tag)
    TextView mTagView;
    @BindView(R.id.activity_location)
    TextView mLocationText;
    @BindView(R.id.activity_detail_state)
    TextView mTimeView;
    @BindView(R.id.activity_detail_participate_count)
    TextView mParticipateCountView;
    @BindView(R.id.activity_banner)
    BannerView mBanner;
    @BindView(R.id.activity_detail_map)
    MapView mMapView;

    private TencentMap mTMap;
    private List<String> mUrls;

    private ExecutorService mThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCollapsingToolbar.setExpandedTitleColor(0xffffffff);
        mHandler = new DetailsHandler(this);
        initDataFromIntent();
        loadDataFromServer();
        initBanner(savedInstanceState);
    }

    private void initDataFromIntent() {
        Intent data = getIntent();
//        mTitleView.setText(data.getStringExtra(EXTRA_TITLE));
        mTagView.setText(data.getStringExtra(EXTRA_TAG));
        mTimeView.setText(data.getStringExtra(EXTRA_TIME));
        mLocationText.setText(data.getStringExtra(EXTRA_LOCATION));
        mUrls = data.getStringArrayListExtra(EXTRA_URLS);
        String avatarUrl = data.getStringExtra(EXTRA_AVATAR_URL);
        DisplayOptions opt = new DisplayOptions();
        opt.setImageShaper(new CircleImageShaper());
        mAvatarView.setOptions(opt);
        if (avatarUrl.startsWith("http")) {
            mAvatarView.displayImage(avatarUrl);
        } else {
            // for testing
            mAvatarView.displayResourceImage(R.drawable.xiaohong);
        }
    }


    private void initBanner(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mTMap = mMapView.getMap();
        UiSettings uiSettings = mMapView.getUiSettings();
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        final int[] colors = new int[mUrls.size()];
        final int[] textColors = new int[mUrls.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = BannerView.DEFAULT_TITLE_BG;
            textColors[i] = BannerView.DEFAULT_TITLE_TEXT_COLOR;
        }
        mBanner.setAutoPlay(false);
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

            }
        });
    }

    @OnClick(R.id.activity_map_toggle)
    void toggleMap() {
        if (!isMapOpen) {
            Animator animation = getCircularReveal();
            animation.setDuration(400);
            animation.setInterpolator(new AccelerateInterpolator());
            mMapView.setVisibility(View.VISIBLE);
            animation.start();
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
        mThreadPool.shutdownNow();
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
        mThreadPool = Executors.newCachedThreadPool();
        mThreadPool.submit(new LoadActivityService());
    }

    private class LoadActivityService implements Runnable {
        @Override
        public void run() {
            try {
                // for testing
                Thread.sleep(1000);
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_USERNAME, "小明");
                bundle.putString(BUNDLE_DESC, "我是一个无聊的活动\n我没有什么特别的\n\n\n\n\n测试\n哈哈");
                bundle.putInt(BUNDLE_PARTICIPATE_COUNT, 20);
                bundle.putDouble(BUNDLE_LATITUDE, 31.2276926429);
                bundle.putDouble(BUNDLE_LONGITUDE, 121.4040112495);
                Message msg = Message.obtain();
                msg.what = MSG_LOAD_COMPLETE;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class DetailsHandler extends Handler {

        private WeakReference<ActivityDetailsActivity> mActivityReference;

        public DetailsHandler(ActivityDetailsActivity activity) {
            super(Looper.getMainLooper());
            this.mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_LOAD_COMPLETE) {
                SimpleDateFormat df = new SimpleDateFormat("MM月dd日 HH:mm");
                ActivityDetailsActivity activity = mActivityReference.get();
                Bundle bundle = msg.getData();
                activity.mDescView.setText(bundle.getString(BUNDLE_DESC));
                activity.mUsernameView.setText(bundle.getString(BUNDLE_USERNAME));
                activity.mParticipateCountView.setText("已有" +
                        bundle.getInt(BUNDLE_PARTICIPATE_COUNT) + "人报名");
                LatLng position = new LatLng(bundle.getDouble(BUNDLE_LATITUDE),
                        bundle.getDouble(BUNDLE_LONGITUDE));
                activity.mTMap.setCenter(position);
                activity.mTMap.setZoom(18);
                Marker marker = activity.mTMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker()).draggable(false)
                );
            }
        }
    }

    public static void startActivity(Context context, String avatarUrl, String title,
                                     String tag, String time, String location,
                                     ArrayList<String> urls) {
        Intent intent = new Intent(context, ActivityDetailsActivity.class)
                .putExtra(EXTRA_TITLE, title)
                .putExtra(EXTRA_AVATAR_URL, avatarUrl)
                .putExtra(EXTRA_TAG, tag)
                .putExtra(EXTRA_LOCATION, location)
                .putExtra(EXTRA_URLS, urls)
                .putExtra(EXTRA_TIME, time);
        context.startActivity(intent);
    }

}
