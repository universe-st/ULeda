package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ecnu.uleda.R;
import ecnu.uleda.tool.SPUtil;
import io.rong.imkit.RongIM;

/**
 * Created by zhaoning on 2017/4/15.
 */

public class FriendFragment extends Fragment {

    private static FriendFragment mInstance;
    private Button btnFriend;

    public static FriendFragment getInstance(){
        if(mInstance == null){
            mInstance = new FriendFragment();
        }
        return mInstance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.message_fragment_friend_fragment, null);
        btnFriend = (Button) view.findViewById(R.id.btn_frient);
        String userId = SPUtil.getUserId("userId");
        if("10086".equals(userId)){
            btnFriend.setText("特兰克斯");
        }else{
            btnFriend.setText("孙悟天");
        }
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RongIM.getInstance()!=null){
                    String userId = SPUtil.getUserId("userId");
                    if("10086".equals(userId)){
                        userId = "10010";
                    }else{
                        userId = "10086";
                    }
                    //开启单聊界面
                    RongIM.getInstance().startPrivateChat(getActivity(),userId,"单聊");
                }
            }
        });
        return view;
    }
}
