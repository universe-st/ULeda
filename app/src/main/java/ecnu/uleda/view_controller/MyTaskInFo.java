package ecnu.uleda.view_controller;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ecnu.uleda.R;

public class MyTaskInFo extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, OnClickListener {
    private RadioGroup group;
    private RadioButton released;
    private RadioButton doing;
    private RadioButton evaluate;
    private RadioButton done;
    private RadioButton evaluation;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;
    public static final int PAGE_FIVE = 4;
    private MyinfoFragmentAdapter mAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_release);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        mAdapter = new MyinfoFragmentAdapter(getSupportFragmentManager());
        initial();
        init();
    }

    @Override
    public void onClick(View v) {
        mSwitch(v.getId());
    }

    public void initial() {
        group = (RadioGroup) findViewById(R.id.radio_group);
        released = (RadioButton) findViewById(R.id.released);
        doing = (RadioButton) findViewById(R.id.doing);
        evaluate = (RadioButton) findViewById(R.id.evaluate);
        done = (RadioButton) findViewById(R.id.done);
        evaluation = (RadioButton) findViewById(R.id.evaluation);
        group.setOnCheckedChangeListener(this);
        mViewPager = (ViewPager) findViewById(R.id.task_layout);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);

        released.setOnClickListener(this);
        doing.setOnClickListener(this);
        evaluate.setOnClickListener(this);
        evaluation.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mSwitch(checkedId);
    }

    public void mSwitch(int Id) {
        switch (Id) {
            case R.id.released:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.doing:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.evaluate:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.done:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.evaluation:
                mViewPager.setCurrentItem(4);
                break;
        }
    }

    public void init() {
        Intent i = getIntent();
        String i0 = i.getStringExtra("data");
        int i1 = Integer.valueOf(i0);
        switch (i1) {
            case 1: {
                released.setChecked(true);
                replaceFragment(new MyTask_ReleasedFragment());
                break;
            }
            case 2: {
                doing.setChecked(true);
                replaceFragment(new MyTask_DoingFragment());
                break;
            }
            case 3: {
                evaluate.setChecked(true);
                replaceFragment(new MyTask_ToEvaluateFragment());
                break;
            }
            case 4: {
                done.setChecked(true);
                replaceFragment(new MyTask_DoneFragment());
                break;
            }
            case 5: {
                evaluation.setChecked(true);
                replaceFragment(new MyTask_MyEvaluationFragment());
                break;
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.task_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (mViewPager.getCurrentItem()) {
                case PAGE_ONE:
                    released.setChecked(true);
                    break;
                case PAGE_TWO:
                    doing.setChecked(true);
                    break;
                case PAGE_THREE:
                    evaluate.setChecked(true);
                    break;
                case PAGE_FOUR:
                    done.setChecked(true);
                    break;
                case PAGE_FIVE:
                    evaluation.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
