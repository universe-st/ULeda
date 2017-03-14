package ecnu.uleda;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by VinnyHu on 2017/3/13.
 */

public class MyinfoFragmentAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 5;
    private MyTask_ReleasedFragment mMyTask_releasedFragment;
    private MyTask_DoingFragment mMyTask_doingFragment;
    private MyTask_DoneFragment mMyTask_doneFragment;
    private MyTask_ToEvaluateFragment mMyTask_toEvaluateFragment;
    private MyTask_MyEvaluationFragment mMyTask_myEvaluationFragment;
    public MyinfoFragmentAdapter(FragmentManager fm)
    {
        super(fm);
        mMyTask_releasedFragment = new MyTask_ReleasedFragment();
        mMyTask_doingFragment = new MyTask_DoingFragment();
        mMyTask_doneFragment = new MyTask_DoneFragment();
        mMyTask_myEvaluationFragment = new MyTask_MyEvaluationFragment();
        mMyTask_toEvaluateFragment = new MyTask_ToEvaluateFragment();
    }
    @Override
    public int getCount()
    {
        return PAGER_COUNT;
    }
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MyTaskInFo.PAGE_ONE:
                fragment = mMyTask_releasedFragment;
                break;
            case MyTaskInFo.PAGE_TWO:
                fragment = mMyTask_doingFragment;
                break;
            case MyTaskInFo.PAGE_THREE:
                fragment = mMyTask_toEvaluateFragment;
                break;
            case MyTaskInFo.PAGE_FOUR:
                fragment = mMyTask_doneFragment;
                break;
            case MyTaskInFo.PAGE_FIVE:
                fragment = mMyTask_myEvaluationFragment;
        }
        return fragment;
    }
}
