package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMAddFriendRequest;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.UserOperatorController;

/**
 * modefied by Zhao Ning
 */
public class AddNewFriends extends AppCompatActivity {

    //private TextView phoneNumber ;
    //private TextView paint ;
    private EditText mSearchInput;
    private TextView back;
    private TextView mSearchFriends;
    private String mSearchIdentifier;
    private static final String TAG = "AddNewFriends";

    private static final String ADD_FRIEND_SUCCESS="success";
    private static final String ADD_FRIEND_ALREADY="already";
    private static final String ADD_FRIEND_NOT_FOUND="notFound";
    private static final String ADD_FRIEND_NOT_MYSELF="notMyself";
    private static final String ADD_FRIEND_FAILED="failed";


    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ret = (String) msg.obj;
                if (ret == ADD_FRIEND_SUCCESS)
                    Toast.makeText(AddNewFriends.this, "添加好友成功～", Toast.LENGTH_SHORT).show();
                else if (ret == ADD_FRIEND_ALREADY)
                    Toast.makeText(AddNewFriends.this, "您已经添加过该好友～", Toast.LENGTH_SHORT).show();
                else if (ret == ADD_FRIEND_NOT_FOUND)
                    Toast.makeText(AddNewFriends.this, "抱歉～该用户不存在", Toast.LENGTH_SHORT).show();
                else if (ret == ADD_FRIEND_NOT_MYSELF)
                    Toast.makeText(AddNewFriends.this, "不可以添加自己哦～", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AddNewFriends.this, "添加好友失败！", Toast.LENGTH_SHORT).show();
            } else {
                UServerAccessException exception = (UServerAccessException) msg.obj;
                Toast.makeText(AddNewFriends.this, "获取信息失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

        back =(TextView) findViewById(R.id.add_Back);
        mSearchFriends = (TextView) findViewById(R.id.search_friends);
        // phoneNumber.findViewById(R.id.findByPhoneNumber);
        //paint.findViewById(R.id.findByPaint);
        mSearchInput = (EditText) findViewById(R.id.edit_add_friends);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchIdentifier = mSearchInput.getText().toString();
                Log.e(TAG, "mSearchIdentifier222: "+mSearchIdentifier );
                addFriends();
            }
        });
        /*button_phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewFriends.this,)
            }
        });*/
    }

    void addFriends()
    {
//        //创建请求列表
//        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
//
////添加好友请求
//        TIMAddFriendRequest req = new TIMAddFriendRequest(mSearchIdentifier);//identifier
////        req.setIdentifier(mSearchIdentifier);
//        req.setAddrSource("AddSource_Type_Android");
//        req.setAddWording("add me");
//        req.setRemark("Cat");
//
//        reqList.add(req);
//
////申请添加好友
//        TIMFriendshipManagerExt.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
//            @Override
//            public void onError(int code, String desc){
//                //错误码code和错误描述desc，可用于定位请求失败原因
//                //错误码code列表请参见错误码表
//                Log.e(tag, "addFriend failed: " + code + " desc");
//            }
//
//            @Override
//            public void onSuccess(List<TIMFriendResult> result){
//                Log.e(tag, "addFriend succ");
//                for(TIMFriendResult res : result){
//                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
//                }
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String ret = onAddFriend(mSearchIdentifier);
                    Log.e(TAG, "run: "+mSearchIdentifier);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ret;
                    mHandler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public static String onAddFriend(@NonNull String inviteByID )throws UServerAccessException{
        UserOperatorController user = UserOperatorController.getInstance();
        String id = user.getId();
        id = UrlEncode(id);
        String passport = user.getPassport();
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response=client
                .withService("User.InviteFriend")//接口的名称
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("inviteByID",inviteByID)
                .request();
        Log.e(TAG, "onAddFriend: "+response.getRet());
        if(response.getRet()==200) {
//            try{
//                JSONObject data=new JSONObject(response.getData());
                return ADD_FRIEND_SUCCESS;// "success"
//            }catch (JSONException e){
//                Log.e("ServerAccessApi",e.toString());
//                数据包无法解析，向上抛出一个异常
//                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
//            }
        }
        else if(response.getRet()==410)
        {
            return ADD_FRIEND_ALREADY;
        }
        else if(response.getRet()==408)
        {
            Log.e("add new friend","?????");
            return ADD_FRIEND_NOT_FOUND;
        }
        else if(response.getRet()==409)
        {
            return ADD_FRIEND_NOT_MYSELF;
        }
        else {
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
}
