package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ecnu.uleda.R;

/**
 * Created by zhaoning on 2017/4/9.
 */

public class MessageFragmentMessageFragment extends Fragment {
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.message_fragment_message_fragment,container,false);
        return view;
    }
}
