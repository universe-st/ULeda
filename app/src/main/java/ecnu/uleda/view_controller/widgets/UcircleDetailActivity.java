package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.model.UCircle;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.UCircleListAdapter;
import ecnu.uleda.view_controller.UcircleCommentAdapter;

public class UcircleDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView mphotoImageView;
    private TextView mpublisher_nameTextView;
    private TextView mTitleTextView;
    private TextView marticleTextView;
    private ImageView mdynamic_photoImageView1;
    private ImageView mdynamic_photoImageView2;
    private ImageView mdynamic_photoImageView3;
    private TextView mpublish_timeTextView;
    private TextView mGet_zanTextView;
    private PopupWindow mPopupWindow;
    private TextView SendComment;
    private EditText CommentContent;
    private TextView mBack;
    private ImageView CommentButton;
    private ScrollView Scroll;
    private ArrayList<UCircle> CommentList = new ArrayList<>();
    private ListView mListView;
    private UcircleCommentAdapter mUcircleCommentAdapter;
    private  int PostId = 0;
    private LinearLayout ImageLayout;
    private int flag = 0;
    private int CommentCount = 0;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    JSONObject jsonObject = (JSONObject)msg.obj;
                    try {
                    JSONArray jsonArray = jsonObject.getJSONArray("comment");
                        for(int i = 0;i < jsonArray.length();i++)
                        {
                            JSONObject json = jsonArray.getJSONObject(i);
                            long Timeout = Long.parseLong(json.getString("commentTime"));
                            String time =  UPublicTool.dateToTimeBefore(new Date(Timeout * 1000));
                            CommentList.add(new UCircle()
                                            .setCommentcontent(json.getString("content"))
                                            .setCommentName(json.getString("username"))
                                            .setCommentTime(time)
                                            .setCommentImage(json.getString("authorAvatar"))
                            );
                            int temp = Integer.parseInt(json.getString("id"));
                            if(temp > flag)
                            {
                                flag = temp;
                            }
                        }
                           Glide.with(UcircleDetailActivity.this)
                                   .load("http://118.89.156.167/uploads/avatars/" + jsonObject.getString("authorAvatar"))
                                   .into(mphotoImageView);
                           mpublisher_nameTextView.setText(jsonObject.getString("username"));
                           mTitleTextView.setText(jsonObject.getString("title"));
                           marticleTextView.setText(jsonObject.getString("content"));
                           if (!jsonObject.getString("pic1").equals("null")) {
                               Glide.with(UcircleDetailActivity.this)
                                       .load("http://118.89.156.167/uploads/avatars/" + jsonObject.getString("pic1"))
                                       .into(mdynamic_photoImageView1);
                           } else {
                               mdynamic_photoImageView1.setVisibility(View.GONE);
                           }
                           if (!jsonObject.getString("pic2").equals("null")) {
                               Glide.with(UcircleDetailActivity.this)
                                       .load("http://118.89.156.167/uploads/avatars/" + jsonObject.getString("pic2"))
                                       .into(mdynamic_photoImageView2);
                           } else {
                               mdynamic_photoImageView2.setVisibility(View.GONE);
                           }
                           if (!jsonObject.getString("pic3").equals("null")) {
                               Glide.with(UcircleDetailActivity.this)
                                       .load("http://118.89.156.167/uploads/avatars/" + jsonObject.getString("pic3"))
                                       .into(mdynamic_photoImageView3);
                           } else {
                               mdynamic_photoImageView3.setVisibility(View.GONE);
                           }
                           long Timeout = Long.parseLong(jsonObject.getString("postTime"));
                           mpublish_timeTextView.setText(UPublicTool.dateToTimeBefore(new Date(Timeout * 1000)));
                           mGet_zanTextView.setText(jsonArray.length()+"");
                            CommentCount = jsonArray.length();
                       }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    mUcircleCommentAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mUcircleCommentAdapter);
                    setListViewHeightBasedOnChildren(mListView);
                    Scroll.smoothScrollTo(0,0);
                    break;
                case 2:
                    JSONObject jsonObject1 = (JSONObject)msg.obj;
                        try
                        {
                            int count = 0;
                            JSONArray jsonArray = jsonObject1.getJSONArray("comment");
                            for(int i = 0;i<jsonArray.length();i++)
                            {
                                JSONObject json = jsonArray.getJSONObject(i);
                                int temp = Integer.parseInt(json.getString("id"));
                                if(temp > flag)
                                {
                                    count++;
                                    long Timeout = Long.parseLong(json.getString("commentTime"));
                                    String time =  UPublicTool.dateToTimeBefore(new Date(Timeout * 1000));
                                    CommentList.add(new UCircle()
                                            .setCommentcontent(json.getString("content"))
                                            .setCommentName(json.getString("username"))
                                            .setCommentTime(time)
                                            .setCommentImage(json.getString("authorAvatar"))
                                    );
                                }
                            }
                            mGet_zanTextView.setText(CommentCount+count+"");
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    mUcircleCommentAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mUcircleCommentAdapter);
                    setListViewHeightBasedOnChildren(mListView);
                    Scroll.smoothScrollTo(0,0);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        init();
        mUcircleCommentAdapter = new UcircleCommentAdapter(this,CommentList);
    }
    public void init()
    {
        mBack = (TextView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mphotoImageView = ( CircleImageView) findViewById(R.id.photo);
        mpublisher_nameTextView = (TextView) findViewById(R.id.publisher_name);
        mTitleTextView = (TextView) findViewById(R.id.Title);
        marticleTextView  = (TextView) findViewById(R.id.article);
        marticleTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        Scroll = (ScrollView)findViewById(R.id.Scroll);
        mdynamic_photoImageView1 = (ImageView) findViewById(R.id.dynamic_photo1);
        mdynamic_photoImageView2 = (ImageView) findViewById(R.id.dynamic_photo2);
        mdynamic_photoImageView3 = (ImageView) findViewById(R.id.dynamic_photo3);
        mpublish_timeTextView = (TextView) findViewById(R.id.publish_time);
        mGet_zanTextView = (TextView) findViewById(R.id.Get_zan);
        CommentButton = (ImageView) findViewById(R.id.CommentButton);
        CommentButton.setOnClickListener(this);
        Intent intent = getIntent();
        PostId = Integer.parseInt(intent.getStringExtra("id"));
        mListView = (ListView)findViewById(R.id.comment_listview) ;
        GetPostDetails(1);
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.CommentButton:
                Comment();
                break;
            case R.id.send_comment:
                String commentcontent = CommentContent.getText().toString();
                PostComment(commentcontent);
                mPopupWindow.dismiss();
                break;
        }
    }
    public void Comment()
    {
        showPopup();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
    public void showPopup()
    {
        View view = View.inflate(this, R.layout.comment_dialog, null);
         SendComment = (TextView)view.findViewById(R.id.send_comment);
         CommentContent = (EditText)view.findViewById(R.id.input_comment);

        SendComment.setOnClickListener(this);
        CommentContent.setOnClickListener(this);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_bottom_in));
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }
        mPopupWindow.setContentView(view);
        mPopupWindow.showAtLocation(CommentButton, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
    }
    public void GetPostDetails(final int Flag)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONObject jsonObject = ServerAccessApi.getUciclePost(PostId+"");
                    Message msg = new Message();
                    msg.what = Flag;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void PostComment(final String content)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    ServerAccessApi.Comment(PostId+"",content);
                    JSONObject jsonObject = ServerAccessApi.getUciclePost(PostId+"");
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  // 获取item高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 最后再加上分割线的高度和padding高度，否则显示不完整。
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+listView.getPaddingTop()+listView.getPaddingBottom();
        listView.setLayoutParams(params);
    }
}
