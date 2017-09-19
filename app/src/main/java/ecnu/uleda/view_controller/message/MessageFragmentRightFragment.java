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
    private List<Friend> mFriendList = new ArrayList<>();
    private static final String TAG = "MFRF";
    private FriendAdapter mFriendAdapter;
    private ListView mListView;
    private boolean flag=false;
    private UserOperatorController mUOC;
    private UserInfo mUserInfo;
    public interface FriendRefreshEvent{

    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:

                    JSONArray jsonArray = (JSONArray)msg.obj;
                    for(int i = 0;i < jsonArray.length();i++)
                    {
                        try
                        {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Friend mfriend = new Friend().setUserId(json.getString("id"))
                                    .setUserName(json.getString("username"))
                                    .setImageUrl(("http://118.89.156.167/uploads/avatars/"+json.getString("avatar")));
                            mFriendList.add(mfriend);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mFriendAdapter = new FriendAdapter(MessageFragmentRightFragment.this.getContext(),R.layout.friend_item,mFriendList);
                    mListView.setAdapter(mFriendAdapter);
                    break;

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

        return view;
    }

    private void initFriends()
    {
        //获取好友列表
//        TIMFriendshipManagerExt.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>(){
//            @Override
//            public void onError(int code, String desc){
//                //错误码code和错误描述desc，可用于定位请求失败原因
//                //错误码code列表请参见错误码表
//                Log.e(TAG, "getFriendList failed: " + code + " desc");
//            }
//
//            @Override
//            public void onSuccess(List<TIMUserProfile> result){
//                for(TIMUserProfile res : result){
//                    Log.e(TAG, "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
//                            + " remark: " + res.getRemark());
//                    Friend friend = new Friend(res.getIdentifier().toString(),res.getNickName().toString(),res.getSelfSignature().toString());
//                            //String userid, String name, String imageUrl,String userTag)
//                    //TODO:头像显示的地址
//                    mFriendList.add(friend);
//
//                }
//                flag=true;
//                mFriendAdapter = new FriendAdapter(MessageFragmentRightFragment.this.getContext(),R.layout.friend_item,mFriendList);
//                mListView.setAdapter(mFriendAdapter);
//
//            }
//        });

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
                return  mFriendList;
            }catch (JSONException e)
            {
                Log.e("ServerAccessApi",e.toString());
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
