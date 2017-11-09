package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

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

import static java.lang.Thread.sleep;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class UCircleFragment extends Fragment implements View.OnClickListener{
    private ListView mlistView;
    private ArrayList<UCircle> mCircleList  = new ArrayList<>();
    private Button mAddButton;
    private UCircleListAdapter mUCircleListAdapter;
    private int LastCount = -1;
    private int ListCount = -1;
    private MaterialRefreshLayout materialRefreshLayout;
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
                                                .setmGet(json.getString("countComment"))
                                                .setmDynamic_Photo1(json.getString("pic1"))
                                                .setmDynamic_Photo2(json.getString("pic2"))
                                                .setmDynamic_Photo3(json.getString("pic3"))
                                                .setId(json.getString("id"))
                                );
                                    if(i == jsonArray.length() - 1)
                                    {
                                        String id = json.getString("id");
                                        LastCount = Integer.parseInt(id);
                                    }
                                        String id = json.getString("id");
                                        int temp = Integer.parseInt(id);
                                        if(temp > ListCount)
                                        {
                                            ListCount = temp;
                                        }
                            }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                    mUCircleListAdapter.notifyDataSetChanged();
                    mlistView.setAdapter(mUCircleListAdapter);
                    break;
                case 2:
                    JSONArray JsonArray = (JSONArray)msg.obj;
                    for(int i = JsonArray.length()-1; i >= 0;i--)
                    {
                        try{
                            JSONObject json = JsonArray.getJSONObject(i);
                            String id = json.getString("id");
                            int Temp = Integer.parseInt(id);
                            if(Temp > ListCount)
                            {
                                ListCount = Temp;
                                mCircleList.add(0,new UCircle()
                                        .setmPhotoId(json.getString("authorAvatar"))
                                        .setmName(json.getString("authorName"))
                                        .setmTitle(json.getString("title"))
                                        .setmArticle(json.getString("absContent"))
                                        .setmTime(json.getString("postTime"))
                                        .setmGet(json.getString("countComment"))
                                        .setmDynamic_Photo1(json.getString("pic1"))
                                        .setmDynamic_Photo2(json.getString("pic2"))
                                        .setmDynamic_Photo3(json.getString("pic3"))
                                        .setId(json.getString("id"))
                                );
                            }
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle b)
    {
        View v=inflater.inflate(R.layout.u_circle_fragment,parent,false);
        init(v);
        mUCircleListAdapter = new UCircleListAdapter(this.getActivity(),mCircleList);
        getUCircleList(-1,1);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UCircle Item = (UCircle) mUCircleListAdapter.getItem(position);
            Intent intent = new Intent(UCircleFragment.this.getActivity(), UcircleDetailActivity.class);
                intent.putExtra("id",Item.getId());
                startActivity(intent);
            }
        });
        materialRefreshLayout.setLoadMore(true);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialLayout) {
                    getUCircleList(-1,2);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                sleep(1000);
                            }catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            materialRefreshLayout.finishRefresh();
                        }
                    }).start();

            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialLayout) {
                //load more refreshing...
                getUCircleList(LastCount,1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            sleep(1000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }).start();
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
        materialRefreshLayout = (MaterialRefreshLayout)v.findViewById(R.id.refresh);
    }
    public void getUCircleList(final int fromPost,final int Index)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUCicleList(fromPost);
                    if(jsonArray.length() > 0)
                    {
                        Message msg = new Message();
                        msg.obj = jsonArray;
                        msg.what = Index;
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
