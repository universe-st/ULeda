package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.ChatMessage;

/**
 * Created by zhaoning on 2017/5/1.
 */

public class MessageFragmentLeftFragment extends Fragment{
    private List<ChatMessage> mChatMessageList=new ArrayList<>();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        initMessages();
    }

//    public static TaskMissionFragment getInstance() {
//        if (mInstance == null) {
//            synchronized (TaskMissionFragment.class) {
//                if (mInstance == null) {
//                    mInstance = new TaskMissionFragment();
//                }
//            }
//        }
//        return mInstance;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment_left_fragment, container, false);
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.chat_message_recycle_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.getContext());
        final Fragment fragment=new MessageFragmentChatFragment();
        recyclerView.setLayoutManager(layoutManager);
        ChatMessageAdapter adapter= new ChatMessageAdapter(mChatMessageList);

        adapter.setOnItemClickListener(new ChatMessageAdapter.OnItemClickListener(){
            @Override
            public void onItemClicked(View v, ChatMessage chatMessage) {

                replaceFragment(fragment);

//                Intent intent = new Intent(getActivity().getApplicationContext(), TaskDetailsActivity.class);
//                intent.putExtra("UTask", task);
//                startActivity(intent);
            }
        });
//        adapter = new ChatMessageAdapter(mChatMessageList,new ChatMessageAdapter.OnItemClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                replaceFragment(fragment);
//            }
//        },new ChatMessageAdapter.OnItemLongClickListener() {
//
//            @Override
//            public void onLongClick(View v) {
//                Toast.makeText(getContext(), "长按", Toast.LENGTH_LONG).show();
//
//            }
//        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initMessages(){
        for(int i=0;i<3;i++) {
            ChatMessage dyz = new ChatMessage("丁义珍",R.drawable.username);
            mChatMessageList.add(dyz);
            ChatMessage hlp = new ChatMessage("侯亮平",R.drawable.user2);
            mChatMessageList.add(hlp);
            ChatMessage ldk = new ChatMessage("李达康",R.drawable.username);
            mChatMessageList.add(ldk);
            ChatMessage qtw = new ChatMessage("祁同伟",R.drawable.user4);
            mChatMessageList.add(qtw);
            ChatMessage srj = new ChatMessage("沙瑞金",R.drawable.user5);
            mChatMessageList.add(srj);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.message_fragment_layout,fragment);
        transaction.commit();
    }


}
