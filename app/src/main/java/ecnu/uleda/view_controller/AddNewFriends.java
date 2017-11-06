package ecnu.uleda.view_controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMAddFriendRequest;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONException;
import org.json.JSONObject;



import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UserInfo;

/**
 * modefied by Zhao Ning
 */
public class AddNewFriends extends AppCompatActivity {

    private EditText mSearchInput;
    private TextView back;
    private TextView mSearchFriend;
    private String mSearchName;
    private static final String TAG = "AddNewFriends";
    private CircleImageView mNewFriendImage;
    private TextView mNewFriendName;
    private TextView mNewFriendTag;
    private TextView mNewFriendSex;
    private UserOperatorController mUOC;
    private UserInfo mFriendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

        back =(TextView) findViewById(R.id.add_Back);
        mSearchFriend = (TextView) findViewById(R.id.search_friends);
        mSearchInput = (EditText) findViewById(R.id.edit_add_friends);
        mNewFriendImage = (CircleImageView)findViewById(R.id.image_new_friend);
        mNewFriendName = (TextView)findViewById(R.id.new_friend_name);
        mNewFriendTag = (TextView)findViewById(R.id.new_friend_tag);
        mUOC = UserOperatorController.getInstance();
        mNewFriendSex = (TextView)findViewById(R.id.new_friend_sex);

        mFriendInfo = new UserInfo();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSearchName = mSearchInput.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchNewFriend(mSearchName);
                    }
                }).start();
            }
        });

        setInvisiable();

        mNewFriendName.setOnClickListener(new MyClickListener());
        mNewFriendImage.setOnClickListener(new MyClickListener());
        mNewFriendSex.setOnClickListener(new MyClickListener());
        mNewFriendTag.setOnClickListener(new MyClickListener());

    }

    void searchNewFriend(final String friendName) {
        final String id = mUOC.getId();
        String pwd = mUOC.getPassport();
        try {
            JSONObject jsonObject = ServerAccessApi.getBasicInfoByName(id,pwd,friendName);
            Log.e(TAG, "searchNewFriend: "+jsonObject.toString() );
            mFriendInfo.setId(jsonObject.getString("id"))
                    .setAvatar("http://118.89.156.167/uploads/avatars/"+jsonObject.getString("avatar"))
                    .setFriendStatus(jsonObject.getInt("friendStatus"))
                    .setUserType(jsonObject.getInt("usertype"))
                    .setUserName(friendName)
                    .setSchool(jsonObject.getString("school"))
                    .setSex(jsonObject.getInt("sex"));
            AddNewFriends.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setVisiable();
                    if(friendName.equals(id))
                        Toast.makeText(AddNewFriends.this,"您搜索的用户是您自己诶～",Toast.LENGTH_LONG).show();
                    else {
                        if(mFriendInfo.getUserType()==-1)
                            Toast.makeText(AddNewFriends.this,"您搜索的用户被禁辣sorry～",Toast.LENGTH_LONG).show();
                        else {
                            if(mFriendInfo.getFriendStatus()==2)
                                Toast.makeText(AddNewFriends.this,"ta已经是您的好友哇～",Toast.LENGTH_LONG).show();
                            else {
                                Glide.with(AddNewFriends.this)
                                        .load(mFriendInfo.getAvatar())
                                        .into(mNewFriendImage);
                                mNewFriendName.setText(mFriendInfo.getUserName());
                                mNewFriendTag.setText(mFriendInfo.getSchool());
                                if(mFriendInfo.getSex()==0)
                                    mNewFriendSex.setText("♂");
                                else
                                {
                                    mNewFriendSex.setText("♀");
                                    mNewFriendSex.setTextColor(Color.parseColor("#FF4081"));
                                }
                            }
                        }
                    }

                }
            });
        }catch (JSONException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (UServerAccessException e)
        {
            switch (e.getStatus())
            {
                case 401:
                    AddNewFriends.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewFriends.this,"您搜索的用户不存在╭(°A°`)╮",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 402:
                    AddNewFriends.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewFriends.this,"用户被禁止登录╭(°A°`)╮",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 408:
                    AddNewFriends.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewFriends.this,"请输入正确的用户名(￣^￣)ゞ",Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                default:
                    AddNewFriends.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewFriends.this,"请输入正确的用户名(◐‿◑)",Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    System.exit(1);
                    break;
            }
        }
    }

    class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AddNewFriends.this,SingleUserInfoActivity.class);
            intent.putExtra("userid", String.valueOf(mFriendInfo.getId()));
            Log.e(TAG, "onClick userid: "+mFriendInfo.getId() );
            startActivity(intent);
        }
    }

    void setInvisiable(){
        mNewFriendName.setVisibility(View.INVISIBLE);
        mNewFriendName.setEnabled(false);
        mNewFriendImage.setVisibility(View.INVISIBLE);
        mNewFriendImage.setEnabled(false);
        mNewFriendSex.setVisibility(View.INVISIBLE);
        mNewFriendSex.setEnabled(false);
        mNewFriendTag.setVisibility(View.INVISIBLE);
        mNewFriendTag.setEnabled(false);
    }

    void setVisiable(){
        mNewFriendName.setVisibility(View.VISIBLE);
        mNewFriendName.setEnabled(true);
        mNewFriendImage.setVisibility(View.VISIBLE);
        mNewFriendImage.setEnabled(true);
        mNewFriendSex.setVisibility(View.VISIBLE);
        mNewFriendSex.setEnabled(true);
        mNewFriendTag.setVisibility(View.VISIBLE);
        mNewFriendTag.setEnabled(true);
    }
}
