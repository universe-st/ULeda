package ecnu.uleda.view_controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMAddFriendRequest;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import ecnu.uleda.R;

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
    private String tag = "AddNewFriends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

        back =(TextView) findViewById(R.id.add_Back);
        mSearchFriends = (TextView) findViewById(R.id.search_friends);
        // phoneNumber.findViewById(R.id.findByPhoneNumber);
        //paint.findViewById(R.id.findByPaint);
        mSearchInput = (EditText) findViewById(R.id.edit_add_friends);
        mSearchIdentifier = mSearchInput.getText().toString();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        //创建请求列表
        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();

//添加好友请求
        TIMAddFriendRequest req = new TIMAddFriendRequest("8");//identifier
//        req.setIdentifier(mSearchIdentifier);
        req.setAddrSource("DemoApp");
        req.setAddWording("add me");
        req.setRemark("Cat");

        reqList.add(req);

//申请添加好友
        TIMFriendshipManagerExt.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "addFriend failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMFriendResult> result){
                Log.e(tag, "addFriend succ");
                for(TIMFriendResult res : result){
                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
                }
            }
        });
    }
}
