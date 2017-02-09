package ecnu.uleda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class UCircleFragment extends Fragment {

    private ListView mlistView;
    private ArrayList<UCircle> mCircleList;

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b)
    {
        View v=inflater.inflate(R.layout.u_circle_fragment,parent,false);
        mlistView = (ListView) v.findViewById(R.id.u_circle_list_view);

        //测试代码
        mCircleList = new ArrayList<>();
        UCircle uc1 = new UCircle(R.drawable.model1,"heikezy","高数求解","求一操作系统要跪求一位大神带带高数跪求！！","4分钟前","5",0);
        UCircle uc2 = new UCircle(R.drawable.model2,"杨先生","求操作系统答案","操作系统！","8分钟前","5",0);
        UCircle uc3 = new UCircle(R.drawable.model3,"大佬","求虐","精通c++java线代数论，求虐求暴打！233333333333","4分钟前","5",R.drawable.kk);
        UCircle uc4 = new UCircle(R.drawable.model4,"美少女","找男友","过年带回家玩耍","4分钟前","5",0);
        mCircleList.add(uc1);
        mCircleList.add(uc2);
        mCircleList.add(uc3);
        mCircleList.add(uc4);

        mlistView.setAdapter(new UCircleListAdapter(this.getActivity(),mCircleList));
        return v;
    }


}
