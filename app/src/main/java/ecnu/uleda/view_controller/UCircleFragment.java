package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ecnu.uleda.R;
import ecnu.uleda.model.UCircle;
import ecnu.uleda.view_controller.widgets.UcircleDetailActivity;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class UCircleFragment extends Fragment implements View.OnClickListener{

    private ListView mlistView;
    private ArrayList<UCircle> mCircleList;
    private Button mAddButton;
    private UCircleListAdapter mUCircleListAdapter;
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b)
    {
        View v=inflater.inflate(R.layout.u_circle_fragment,parent,false);
        init(v);

        //测试代码
        mCircleList = new ArrayList<>();
        mCircleList.add(new UCircle()
                .setmPhotoId(R.drawable.user1)
                .setmName("黄老邪")
                .setmTitle("找黄蓉")
                .setmArticle("黄蓉这个臭丫头又和郭靖那个傻小子跑了，谁把她找回来，" +
                        "本岛主重重有赏")
                .setmTime("9分钟前")
                .setmGet("9")
                .setmDynamic_Photo(0)
        );
        mCircleList.add(new UCircle()
                .setmPhotoId(R.drawable.user2)
                .setmName("老顽童")
                .setmTitle("寻找九阴真经")
                .setmArticle("黄老邪和他鬼灵精怪的老婆把我师兄给我的九阴真经给骗走了！！！！")
                .setmTime("10分钟前")
                .setmGet("9")
                .setmDynamic_Photo(0)
        );
        mCircleList.add(new UCircle()
                .setmPhotoId(R.drawable.user3)
                .setmName("杨康")
                .setmTitle("报仇")
                .setmArticle("完颜洪烈这个贼人，害我亲生父母，并且让我认贼作父20多年，我要杀了你")
                .setmTime("11分钟前")
                .setmGet("11")
                .setmDynamic_Photo(0)
        );
        mCircleList.add(new UCircle()
                .setmPhotoId(R.drawable.user4)
                .setmName("郭靖")
                .setmTitle("报杀父之仇")
                .setmArticle("当年在牛家村，完颜洪烈指使贼人杀我亲父，我要报仇！！！！！")
                .setmTime("100分钟前")
                .setmGet("11")
                .setmDynamic_Photo(R.drawable.kk)
        );
        mCircleList.add(new UCircle()
                .setmPhotoId(R.drawable.user5)
                .setmName("黄蓉")
                .setmTitle("找郭靖")
                .setmArticle("婧哥哥你去哪里蓉儿就去哪里")
                .setmTime("9分钟前")
                .setmGet("9")
                .setmDynamic_Photo(0)
        );
        mUCircleListAdapter = new UCircleListAdapter(this.getActivity(),mCircleList);
        mlistView.setAdapter(mUCircleListAdapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UCircle Item = (UCircle) mUCircleListAdapter.getItem(position);
            Intent intent = new Intent(UCircleFragment.this.getActivity(), UcircleDetailActivity.class);
                String photo = String.valueOf(Item.getmPhotoId());
                intent.putExtra("photo",photo);
                intent.putExtra("publisher_name",Item.getmName());
                intent.putExtra("Title",Item.getmTitle());
                intent.putExtra("article",Item.getmArticle());
                String dynamic_photo = String.valueOf(Item.getmDynamic_Photo());
                intent.putExtra("dynamic_photo",dynamic_photo);
                intent.putExtra("publish_time",Item.getmTime());
                intent.putExtra("Get_zan",Item.getmGet());
                startActivity(intent);
            }
        });
        return v;
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.u_circle_button:
            {
                Intent i = new Intent(UCircleFragment.this.getActivity(),ReleasedUcircleActivity.class);
                startActivity(i);
                break;
            }
        }
    }
    public void init(View v)
    {
        mlistView = (ListView) v.findViewById(R.id.u_circle_list_view);
        mAddButton = (Button) v.findViewById(R.id.u_circle_button);
        mAddButton.setOnClickListener(this);
    }

}
