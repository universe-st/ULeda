package ecnu.uleda;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class TaskListFragment extends Fragment {
    //任务列表
    private Spinner mMainSpinner;
    private Spinner mSortSpinner;
    private ArrayAdapter<String> mMainAdapter;
    private ArrayAdapter<String> mSortAdapter;
    private RefreshListView mListView;
    private UTaskManager mUTaskManager=UTaskManager.getInstance();
    private TaskListAdapter mTaskListAdapter;
    private static final int LOAD_MORE=0;
    private static final int REFRESH=1;
    private static final int ERROR=2;
    private Handler mRefreshHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case REFRESH:
                    mListView.completeRefresh();
                    mUTaskManager.setListView(mListView,TaskListFragment.this.getActivity());
                    mTaskListAdapter.notifyDataSetChanged();
                    Toast.makeText(TaskListFragment.this.getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
                    break;
                case LOAD_MORE:
                    mListView.completeRefresh();
                    mTaskListAdapter.notifyDataSetChanged();
                    Toast.makeText(TaskListFragment.this.getActivity(),"加载成功",Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    String error=msg.obj.toString();
                    mListView.completeRefresh();
                    Toast.makeText(TaskListFragment.this.getActivity(),"网络异常："+error,Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
    final private static ArrayList<String> mMainArray;
    final private static ArrayList<String> mSortArray;
    static {
        mMainArray=new ArrayList<>();
        mSortArray=new ArrayList<>();
        mMainArray.add("全部");
        mMainArray.add("跑腿代步");
        mMainArray.add("生活服务");
        mMainArray.add("学习帮助");
        mMainArray.add("技术难题");
        mMainArray.add("寻物启示");
        mMainArray.add("活动相关");
        mMainArray.add("其他");
        mSortArray.add("报酬从高到低");
        mSortArray.add("报酬从低到高");
        mSortArray.add("距离从近到远");
    }
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
    }
    //初始化Spinner
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.task_fragment,parent,false);
        init(v);
        setListViewClick();
        return v;
    }

    private void setListViewClick(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Thanks to MicroDog.
                UTask task=(UTask)adapterView.getItemAtPosition(i);
                Intent intent=new Intent(getActivity().getApplicationContext(),TaskDetailsActivity.class);
                intent.putExtra("UTask",task);
                startActivity(intent);
            }
        });
    }
    private void init(View v){
        mMainSpinner=(Spinner)v.findViewById(R.id.spinner0);
        mMainSpinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.DST);
        mSortSpinner=(Spinner)v.findViewById(R.id.spinner1);
        mSortSpinner.getBackground().setColorFilter(0XFFFFFF,PorterDuff.Mode.DST);

        mMainAdapter=new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                R.layout.u_spiner_text_item,
                mMainArray);
        mMainAdapter.setDropDownViewResource(R.layout.u_spiner_dropdown_item);
        mMainSpinner.setAdapter(mMainAdapter);
        mSortAdapter=new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                R.layout.u_spiner_text_item,
                mSortArray);
        mSortAdapter.setDropDownViewResource(R.layout.u_spiner_dropdown_item);
        mSortSpinner.setAdapter(mSortAdapter);
        mListView=(RefreshListView)v.findViewById(R.id.task_list_view);
        mTaskListAdapter=(TaskListAdapter)mUTaskManager
                .setListView(mListView,this.getActivity().getApplicationContext());
        mListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                Thread thread=new Thread() {
                    @Override
                    public void run(){
                        try {
                            mUTaskManager.refreshTaskInList();
                            Message message = new Message();
                            message.what = REFRESH;
                            mRefreshHandler.sendMessage(message);
                        }catch (UServerAccessException e){
                            e.printStackTrace();
                            Message message=new Message();
                            message.what=ERROR;
                            message.obj=e.getMessage();
                            mRefreshHandler.sendMessage(message);
                        }
                    }
                };
                thread.start();
            }

            @Override
            public void onLoadingMore() {
                Thread thread=new Thread(){
                    @Override
                    public void run(){
                        try {
                            mUTaskManager.loadMoreTaskInList(5);
                        }catch (UServerAccessException e){
                            //TODO:根据异常的状态决定向主线程的handle发送哪些信息
                            e.printStackTrace();
                            Message message=new Message();
                            message.what=ERROR;
                            message.obj=e.getMessage();
                            mRefreshHandler.sendMessage(message);
                        }
                        Message message=new Message();
                        message.what=LOAD_MORE;
                        mRefreshHandler.sendMessage(message);
                    }
                };
                thread.start();
            }
        });

        Button buttonTaskPost = (Button)v.findViewById(R.id.task_post);
         //final UMainActivity activity =(UMainActivity) getActivity();
        buttonTaskPost.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent =new Intent(getActivity(),TaskPostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        mUTaskManager.setListView(mListView,this.getActivity().getApplicationContext());
        mTaskListAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
