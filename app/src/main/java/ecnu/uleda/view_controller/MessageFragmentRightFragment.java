package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.model.Contacts;


/**
 * Created by zhaoning on 2017/5/1.
 */

public class MessageFragmentRightFragment extends Fragment{

    private ListView mListView;
    private List<Contacts> contastsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
//        SPUtil.init(this.getContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment_right_fragment, container, false);
        initContacts();
        ContactsAdapter adapter = new ContactsAdapter(
                MessageFragmentRightFragment.this.getContext(),R.layout.contacts_item,contastsList);
        mListView=(ListView)view.findViewById(R.id.contacts_list_view);
        mListView.setAdapter(adapter);
        final Fragment mfragment= new MessageFragmentChatFragment();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                Contacts contacts=contastsList.get(position);
                Toast.makeText(view.getContext(),"别急呀～转啥界面？",Toast.LENGTH_SHORT).show();
//                replaceFragment(mfragment);
            }
        });
        return view;
    }

    private void initContacts(){
        for(int i=0;i<2;i++) {
            Contacts dyz = new Contacts("丁义珍",R.drawable.username);
            contastsList.add(dyz);
            Contacts hlp = new Contacts("侯亮平",R.drawable.user2);
            contastsList.add(hlp);
            Contacts ldk = new Contacts("李达康",R.drawable.username);
            contastsList.add(ldk);
            Contacts qtw = new Contacts("祁同伟",R.drawable.user4);
            contastsList.add(qtw);
            Contacts srj = new Contacts("沙瑞金",R.drawable.user5);
            contastsList.add(srj);
        }
    }

//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.message_fragment_layout,fragment);
//        transaction.commit();
//    }

}
