package ecnu.uleda.view_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ecnu.uleda.R;

public class MyTaskInFo extends AppCompatActivity implements
         RadioGroup.OnCheckedChangeListener, OnClickListener {
    public static final String ACTION_REFRESH = "ecnu.uleda.view_controller.my_task_info_refresh";
    public static final String EXTRA_TASK_POS = "extra_task_pos";
    private static final int[] IDS = new int[]{R.id.released, R.id.doing, R.id.evaluate, R.id.done, R.id.evaluation};
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
    private BroadcastReceiver mReceiver;
    private int mCheckedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_release);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        initial();
        init();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_REFRESH);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int affectedTaskPos = intent.getIntExtra(EXTRA_TASK_POS, -1);
                if (affectedTaskPos < 0) return;
                Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.task_layout);
                if (activeFragment instanceof MyTask_ReleasedFragment) {
                    ((MyTask_ReleasedFragment) activeFragment).notifyItemRemoved(affectedTaskPos);
                } else if (activeFragment instanceof MyTask_DoingFragment) {
                    ((MyTask_DoingFragment) activeFragment).notifyItemRemoved(affectedTaskPos);
                } else if (activeFragment instanceof MyTask_DoneFragment) {
                    ((MyTask_DoneFragment) activeFragment).notifyItemRemoved(affectedTaskPos);
                } else if (activeFragment instanceof MyTask_MyEvaluationFragment) {
                    ((MyTask_MyEvaluationFragment) activeFragment).notifyItemRemoved(affectedTaskPos);
                } else if (activeFragment instanceof MyTask_ToEvaluateFragment) {
                    ((MyTask_ToEvaluateFragment) activeFragment).notifyItemRemoved(affectedTaskPos);
                }
            }
        };
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != mCheckedPos) {
            initFragment(v.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public void initial() {
        group = (RadioGroup) findViewById(R.id.radio_group);
        released = (RadioButton) findViewById(R.id.released);
        doing = (RadioButton) findViewById(R.id.doing);
        evaluate = (RadioButton) findViewById(R.id.evaluate);
        done = (RadioButton) findViewById(R.id.done);
        evaluation = (RadioButton) findViewById(R.id.evaluation);
        group.setOnCheckedChangeListener(this);

        released.setOnClickListener(this);
        doing.setOnClickListener(this);
        evaluate.setOnClickListener(this);
        evaluation.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mCheckedPos != checkedId) {
            initFragment(checkedId);
        }
    }


    public void init() {
        Intent i = getIntent();
        String i0 = i.getStringExtra("data");
        initFragment(IDS[Integer.parseInt(i0) - 1]);
    }

    private void initFragment(int id) {
        mCheckedPos = id;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.released: {
                released.setChecked(true);
                transaction.replace(R.id.task_layout, new MyTask_ReleasedFragment());
                break;
            }
            case R.id.doing: {
                doing.setChecked(true);
                transaction.replace(R.id.task_layout, new MyTask_DoingFragment());
                break;
            }
            case R.id.evaluate: {
                evaluate.setChecked(true);
                transaction.replace(R.id.task_layout, new MyTask_ToEvaluateFragment());
                break;
            }
            case R.id.done: {
                done.setChecked(true);
                transaction.replace(R.id.task_layout, new MyTask_DoneFragment());
                break;
            }
            case R.id.evaluation: {
                evaluation.setChecked(true);
                transaction.replace(R.id.task_layout, new MyTask_MyEvaluationFragment());
                break;
            }
        }
        transaction.commit();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
