package ecnu.uleda;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ResourceBundle;

public class UMainActivity extends AppCompatActivity {
    //MainActivity
    private ViewPager mViewPager;
    private Fragment[] mFragments = null;
    private Button[] mButtons = null;
    UserOperatorController Controller = new UserOperatorController();
     boolean mIsLogined=Controller.getIsLogined();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umain);
        init();
        if (!UserOperatorController.getInstance().getIsLogined()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        checkMapPermission();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);

        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtonsPressed(position);
                for (int j = 0; j < 5; j++) {
                    if (j != position) {
                        setButtonsUnpressed(j);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(5);
        mButtons = new Button[5];
        mButtons[0] = (Button) findViewById(R.id.map_button);
        mButtons[1] = (Button) findViewById(R.id.task_button);
        mButtons[2] = (Button) findViewById(R.id.u_circle_button);
        mButtons[3] = (Button) findViewById(R.id.message_button);
        mButtons[4] = (Button) findViewById(R.id.me_button);
        mButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToView(0);
            }
        });
        mButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToView(1);
            }
        });
        mButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                checkIsLogined(2);}
        });
        mButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsLogined(3);
            }
        });
        mButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsLogined(4);
            }
        });
        changeToView(0);
    }


    public void checkIsLogined(int i){
        UserOperatorController uoc=UserOperatorController.getInstance();
        if(!uoc.getIsLogined()){
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
        }
        else  changeToView(i);
        }

    //主Activity翻页
    public void changeToView(int i) {
        mViewPager.setCurrentItem(i);
    }

    //将某按钮视图设置为下压状态
    private void setButtonsPressed(int i) {
        switch (i) {
            case 0:
                mButtons[0].setBackgroundResource(R.drawable.map_bt_pressed);
                break;
            case 1:
                mButtons[1].setBackgroundResource(R.drawable.task_bt_pressed);
                break;
            case 2:
                mButtons[2].setBackgroundResource(R.drawable.ucircle_bt_pressed);
                break;
            case 3:
                mButtons[3].setBackgroundResource(R.drawable.message_bt_pressed);
                break;
            case 4:
                mButtons[4].setBackgroundResource(R.drawable.me_bt_pressed);
                break;
            default:
                throw new RuntimeException("Error button code.");
        }
    }

    private void setButtonsUnpressed(int i) {
        switch (i) {
            case 0:
                mButtons[0].setBackgroundResource(R.drawable.map_bt);
                break;
            case 1:
                mButtons[1].setBackgroundResource(R.drawable.task_bt);
                break;
            case 2:
                mButtons[2].setBackgroundResource(R.drawable.ucircle_bt);
                break;
            case 3:
                mButtons[3].setBackgroundResource(R.drawable.message_bt);
                break;
            case 4:
                mButtons[4].setBackgroundResource(R.drawable.me_bt);
                break;
            default:
                throw new RuntimeException("Error button code.");
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
