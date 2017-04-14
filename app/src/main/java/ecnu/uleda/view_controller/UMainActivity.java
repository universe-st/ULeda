package ecnu.uleda.view_controller;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.view_controller.taskfragment.TaskListFragment;
import ecnu.uleda.view_controller.widgets.BottomBarLayout;
import ecnu.uleda.view_controller.widgets.NoScrollViewPager;
import io.rong.eventbus.EventBus;
import io.rong.imageloader.utils.L;


public class UMainActivity extends AppCompatActivity implements BottomBarLayout.OnLabelSelectedListener {

    @BindView(R.id.bottom_bar)
    BottomBarLayout mBottomBar;

    @BindView(R.id.main_view_pager)
    RelativeLayout mViewPager;

    private static final String[] BOTTOM_LABELS = new String[]{"地图", "发布", "U圈", "消息", "我"};
    private static final int[] BOTTOM_ICONS = new int[]{R.drawable.ic_room_white_48dp,
            R.drawable.ic_create_white_48dp,R.drawable.ic_explore_white_48dp,
            R.drawable.ic_question_answer_white_48dp,R.drawable.ic_account_circle_white_48dp};
    private static UMainActivity sHolder;

    private boolean[] isAdded = {true, false, false, false, false};
    private int mLastPos = 0;

    public static void finishMainActivity(){
        if(sHolder!=null){
            sHolder.finish();
            sHolder=null;
        }
    }
    //MainActivity

    private Fragment[] mFragments = null;
    UserOperatorController Controller = UserOperatorController.getInstance();
    boolean mIsLogined=Controller.getIsLogined();


    public static final String TAG_EXIT = "exit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sHolder=this;
        setContentView(R.layout.activity_umain);
        ButterKnife.bind(this);
        init();
        if (!UserOperatorController.getInstance().getIsLogined()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_view_pager, mFragments[0])
                .commit();
        checkMapPermission();
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }

    private void checkMapPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (this.checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void init() {
        mFragments = new Fragment[5];
        mFragments[0] = new UMainFragment();
        mFragments[1] = new TaskListFragment();
        mFragments[2] = new UCircleFragment();
        mFragments[3] = new MessageFragment();
        mFragments[4] = new UserInfoFragment();
        FragmentManager fm = getSupportFragmentManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
//        mViewPager.setNoScroll(true);
//        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
//            @Override
//            public Fragment getItem(int position) {
//                return mFragments[position];
//            }
//
//            @Override
//            public int getCount() {
//                return mFragments.length;
//            }
//
//        });
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                mBottomBar.select(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        mViewPager.setOffscreenPageLimit(5);
        mBottomBar.init(BOTTOM_LABELS, BOTTOM_ICONS);
        mBottomBar.setOnLabelSelectedListener(this);
    }


    public void checkIsLogined(int i){
        UserOperatorController uoc=UserOperatorController.getInstance();
        if(!uoc.getIsLogined()){
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
        }
        else{

            changeToView(i);
        }
    }


    //主Activity翻页
    public void changeToView(int i) {
//        mViewPager.setCurrentItem(i);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mLastPos == 0) {
            ft.hide(mFragments[mLastPos]);
        } else {
            ft.remove(mFragments[mLastPos]);
            isAdded[mLastPos] = false;
        }
        mLastPos = i;
        if (!isAdded[i]) {
            ft.add(R.id.main_view_pager, mFragments[i]);
        }
        ft.show(mFragments[i]);
        ft.commit();
    }

    @Override
    public void labelSelected(int pos) {
        changeToView(pos);
    }

}
