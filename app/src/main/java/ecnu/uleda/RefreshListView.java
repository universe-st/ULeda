package ecnu.uleda;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Shensheng on 2017/1/19.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener{

    private View mHeaderView;
    private int mHeaderViewHeight;
    private TextView mStateText;
    private View mFooterView;
    private int mFooterViewHeight;
    private int mRecordY;

    private static final int PULL_REFRESH=0;
    private static final int RELEASE_REFRESH=1;
    private static final int REFRESHING=2;

    private int mCurrentState=PULL_REFRESH;
    private boolean mIsLoadingMore=false;

    public RefreshListView(Context context) {
        this(context,null);
    }

    public RefreshListView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init(){
        //初始化
        setOnScrollListener(this);
        initHeaderView();
        initFooterView();
    }

    private void initHeaderView(){
        mHeaderView=View.inflate(this.getContext(),R.layout.list_refresh_header,null);
        mStateText=(TextView)mHeaderView.findViewById(R.id.list_refresh_header_text);
        mHeaderView.measure(0,0);
        mHeaderViewHeight=mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
        addHeaderView(mHeaderView);
    }

    private void initFooterView(){
        mFooterView=View.inflate(this.getContext(),R.layout.list_loadmore_footer,null);
        mFooterView.measure(0,0);
        mFooterViewHeight=mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);
        addFooterView(mFooterView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                mRecordY=(int)e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(mCurrentState==REFRESHING) {
                    break;
                }
                int delayY=(int)e.getY()-mRecordY;
                int paddingTop=-mHeaderViewHeight+delayY;
                if(delayY>0 && this.getFirstVisiblePosition()==0){
                    mHeaderView.setPadding(0,paddingTop,0,0);
                    if(paddingTop>=0 && mCurrentState==PULL_REFRESH){
                        mCurrentState=RELEASE_REFRESH;
                        refreshHeaderView();
                    }else if(paddingTop<0 && mCurrentState==RELEASE_REFRESH){
                        mCurrentState=PULL_REFRESH;
                        refreshHeaderView();
                    }
                    //return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mCurrentState==PULL_REFRESH){
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
                }else if(mCurrentState==RELEASE_REFRESH){
                    mHeaderView.setPadding(0,0,0,0);
                    mCurrentState=REFRESHING;
                    refreshHeaderView();
                    if(mOnRefreshListener!=null){
                        mOnRefreshListener.onPullRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private void refreshHeaderView(){
        switch (mCurrentState){
            case PULL_REFRESH:
                mStateText.setText("下拉刷新");
                break;
            case REFRESHING:
                mStateText.setText("...刷新中...");
                break;
            case RELEASE_REFRESH:
                mStateText.setText("松开刷新");
                break;
        }
    }

    public void completeRefresh(){
        if(mIsLoadingMore){
            mFooterView.setPadding(0,-mFooterViewHeight,0,0);
            mIsLoadingMore=false;
        }else{
            mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
            mCurrentState=PULL_REFRESH;
            refreshHeaderView();
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view,int scrollState){
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE
                 && this.getLastVisiblePosition() == ( this.getCount() - 1 )
                && !mIsLoadingMore ) {
                mIsLoadingMore=true;
                mFooterView.setPadding(0,0,0,0);
                this.setSelection(getCount());
                if(mOnRefreshListener!=null){
                    mOnRefreshListener.onLoadingMore();
                }
        }
    }

    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount){
        //This method is empty because it is not needed.
    }

    private OnRefreshListener mOnRefreshListener=null;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        mOnRefreshListener=onRefreshListener;
    }
    public interface OnRefreshListener{
        void onPullRefresh();
        void onLoadingMore();
    }
}
