package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ecnu.uleda.R;
import ecnu.uleda.tool.SPUtil;
import io.rong.imkit.RongIM;

/**
 * Created by zhaoning on 2017/4/15.
 */

public class FriendFragment extends Fragment {

    private static FriendFragment mInstance;
    private Button mButtonBack;
    private TextView mTextView;
    private LinearLayout mLinearLayout;

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
        mButtonBack=(Button) view.findViewById(R.id.button_conversation_back) ;
        mTextView = (TextView) view.findViewById(R.id.textView_friend);
        mLinearLayout=(LinearLayout)view.findViewById(R.id.line_friend);
        String userId = SPUtil.getUserId("userId");
        if("10086".equals(userId)){
            mTextView.setText("中国电信");
        }else{
            mTextView.setText("中国移动");
        }

//        mButtonBack.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                //TODO
//                //等会记得写
//            }
//        });

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
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
                    //转到ConversationActivity
                }
            }
        });
        return view;
    }
}
