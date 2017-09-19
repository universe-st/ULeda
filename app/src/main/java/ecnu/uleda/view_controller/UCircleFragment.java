package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.model.UCircle;
import ecnu.uleda.view_controller.widgets.UcircleDetailActivity;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class UCircleFragment extends Fragment implements View.OnClickListener{

    private ListView mlistView;
    private ArrayList<UCircle> mCircleList  = new ArrayList<>();
    private Button mAddButton;
    private UCircleListAdapter mUCircleListAdapter;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                        JSONArray jsonArray = (JSONArray)msg.obj;

                            for(int i = 0; i < jsonArray.length();i++)
                            {
                                try{
                                    JSONObject json = jsonArray.getJSONObject(i);
                                mCircleList.add(new UCircle()
                                                .setmPhotoId(json.getString("authorAvatar"))
                                                .setmName(json.getString("authorName"))
                                                .setmTitle(json.getString("title"))
                                                .setmArticle(json.getString("absContent"))
                                                .setmTime(json.getString("postTime"))
                                                .setmGet("9")
                                                .setmDynamic_Photo1(json.getString("pic1"))
                                                .setmDynamic_Photo2(json.getString("pic2"))
                                                .setmDynamic_Photo3(json.getString("pic3"))
                                );
                            }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                    mUCircleListAdapter.notifyDataSetChanged();
                    mlistView.setAdapter(mUCircleListAdapter);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b)
    {
        View v=inflater.inflate(R.layout.u_circle_fragment,parent,false);
        init(v);
        mUCircleListAdapter = new UCircleListAdapter(this.getActivity(),mCircleList);
        getUCircleList();
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UCircle Item = (UCircle) mUCircleListAdapter.getItem(position);
            Intent intent = new Intent(UCircleFragment.this.getActivity(), UcircleDetailActivity.class);

                String photo = Item.getmPhotoId();
                intent.putExtra("photo",photo);
                intent.putExtra("publisher_name",Item.getmName());
                intent.putExtra("Title",Item.getmTitle());
                intent.putExtra("article",Item.getmArticle());
                String dynamic_photo1 = Item.getmDynamic_Photo1();
                String dynamic_photo2 = Item.getmDynamic_Photo2();
                String dynamic_photo3 = Item.getmDynamic_Photo3();
                intent.putExtra("dynamic_photo1",dynamic_photo1);
                intent.putExtra("dynamic_photo2",dynamic_photo2);
                intent.putExtra("dynamic_photo3",dynamic_photo3);
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

                Intent i = new Intent(UCircleFragment.this.getActivity(),ReleasedUcircleActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
    public void init(View v)
    {
        mlistView = (ListView) v.findViewById(R.id.u_circle_list_view);
        mAddButton = (Button) v.findViewById(R.id.u_circle_button);
        mAddButton.setOnClickListener(this);
    }
    public void getUCircleList()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUCicleList();
                    if(jsonArray.length() > 0)
                    {
                        Message msg = new Message();
                        msg.obj = jsonArray;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
