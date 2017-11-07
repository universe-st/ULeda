package ecnu.uleda.view_controller.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.Contacts;
import ecnu.uleda.model.Friend;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.view_controller.SingleUserInfoActivity;

import static java.lang.Thread.sleep;


/**
 * Created by zhaoning on 2017/5/1.
 * 信息界面右
 */

public class MessageFragmentRightFragment extends Fragment {


//    private List<Contacts> contastsList = new ArrayList<>();
//    private List<Friend> userList;
//    private ViewPager vpContent;
//    private List<Fragment> mFragments = new ArrayList<>();
//    private FragmentPagerAdapter adapter;
//    private Fragment mConversationFragment = null;
//    private Fragment mConversationList;
//    private LayoutInflater inflater;
//    private ViewGroup container;

    private View view;
    private List<Friend> mFriendList;
    private static final String TAG = "MFRF";
    private FriendAdapter mFriendAdapter;
    private ListView mListView;
    private boolean flag=false;
    private UserOperatorController mUOC;
    private UserInfo mUserInfo;
    private MaterialRefreshLayout materialRefreshLayout;
    public interface FriendRefreshEvent{

    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                {
                    mFriendList = new ArrayList<>();
                    JSONArray jsonArray = (JSONArray)msg.obj;
                    for(int i = 0;i < jsonArray.length();i++)
                    {
                        try
                        {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Friend mfriend = new Friend().setUserId(json.getString("id"))
                                    .setUserName(json.getString("username"))
                                    .setImageUrl(("http://118.89.156.167/uploads/avatars/"+json.getString("avatar")))
                                    .setUserTag(json.getString("signature"));
                            mFriendList.add(mfriend);
                            Log.e(TAG, "handleMessage: "+json.getString("id"));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mFriendAdapter = new FriendAdapter(MessageFragmentRightFragment.this.getContext(),R.layout.friend_item,mFriendList);
                    mListView.setAdapter(mFriendAdapter);
                    break;
                }

                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        EventBus.getDefault().register(this);
        mUOC = UserOperatorController.getInstance();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshFriends(FriendRefreshEvent e){
        initFriends();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_right_fragment,container,false);
        mListView = (ListView)view.findViewById(R.id.friend_list_view);

        initFriends();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = mFriendList.get(position);
                Intent intent = new Intent(getContext(), SingleUserInfoActivity.class);
                intent.putExtra("userid", String.valueOf(friend.getUserId()));
                startActivity(intent);
            }
        });
        materialRefreshLayout = (MaterialRefreshLayout)view.findViewById(R.id.refresh);
        materialRefreshLayout.setLoadMore(true);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialLayout) {
                initFriends();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            sleep(1000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        materialRefreshLayout.finishRefresh();
                    }
                }).start();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialLayout) {
                //load more refreshing...
                        materialRefreshLayout.finishRefreshLoadMore();
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        mFriendList.clear();
        initFriends();
    }

    private void initFriends()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = getFriendList();
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = jsonArray;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public static JSONArray getFriendList() throws UServerAccessException
    {
        UserOperatorController user = UserOperatorController.getInstance();
        String id = user.getId();
        id = UrlEncode(id);
        String passport = user.getPassport();
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response = client
                .withService("User.GetFriendList")
                .withParams("id",id)
                .withParams("passport",passport)
                .request();
        if(response.getRet() == 200)
        {
            try {
                JSONArray mFriendList = new JSONArray(response.getData());
                Log.e("MFRF","getFriendList");
                return  mFriendList;
            }catch (JSONException e)
            {
                Log.e("MFRF","ServerAccessApi"+e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }

        }
        else
        {
            throw new UServerAccessException(response.getRet());
        }
    }

    private static String UrlEncode(String str)throws UServerAccessException{
        try{
            if(str==null)return null;
            return URLEncoder.encode(str,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("http://118.89.156.167/mobile/");
    }
//    Intent intent = new Intent(TaskDetailsActivity.this, SingleUserInfoActivity.class);
//                intent.putExtra("userid", String.valueOf(mTask.getAuthorID()));
//    startActivity(intent);
}
