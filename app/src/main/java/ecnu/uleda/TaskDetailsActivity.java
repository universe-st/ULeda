package ecnu.uleda;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskDetailsActivity extends AppCompatActivity
implements View.OnClickListener{
    private UTask mTask;
    private UHeadlineLayout mHeadlineLayout;
    private MapView mMapView;
    private TencentMap mTencentMap;
    private ListView mListView;
    private TextView mTaskTitle;
    private TextView mTaskLocation;
    private TextView mTaskReward;
    private TextView mTaskDetailInfo;
    PopupWindow mPopupWindow;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment: {
                showPopupWindow();
                mListView.setSelection(mListView.getCount()-1);
            }

        }
    }

    private void showPopupWindow() {

            View view = View.inflate(this, R.layout.activity_addcomment, null);

            Button send = (Button) view.findViewById(R.id.send);

            send.setOnClickListener(this);


            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });

            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            LinearLayout comment= (LinearLayout) view.findViewById(R.id.comment);
            comment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_bottom_in));

            if(mPopupWindow==null){
                mPopupWindow = new PopupWindow(this);
                mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOutsideTouchable(true);
            }

            mPopupWindow.setContentView(view);
            mPopupWindow.showAtLocation(findViewById(R.id.foot_line_button_bar), Gravity.BOTTOM
                    , 0, 0);
            mPopupWindow.update();

    }

    //测试代码
    public static class UserChatItem{
        public int imageID=0;
        public String name="";
        public String sayWhat="";
        public String timeBefore="";
    }
    public static class UserChatItemAdapter extends ArrayAdapter<UserChatItem>{

        private UTask mTask;
        public UserChatItemAdapter(Context context,List<UserChatItem> items,UTask task){
            super(context,R.layout.task_detail_chat_item_left,items);
            mTask=task;
        }
        @Override
        @NonNull
        public View getView(int position, View convertView,@NonNull ViewGroup parent){
            UserChatItem userChatItem=getItem(position);
            View v=null;
            if(mTask.getAuthorUserName().equals(userChatItem.name)){
                v=View.inflate(getContext().getApplicationContext(),R.layout.task_detail_chat_item_right,null);
            }else{
                v=View.inflate(getContext().getApplicationContext(),R.layout.task_detail_chat_item_left,null);
            }
            CircleImageView civ=(CircleImageView)v.findViewById(R.id.task_detail_chat_item_circle);
            civ.setImageBitmap(BitmapFactory.decodeResource(this.getContext().getResources(),userChatItem.imageID));
            TextView tv=(TextView)v.findViewById(R.id.say_what);
            tv.setText(userChatItem.sayWhat);
            tv=(TextView)v.findViewById(R.id.time_before);
            tv.setText(userChatItem.timeBefore);
            tv=(TextView)v.findViewById(R.id.name_of_chatter);
            tv.setText(userChatItem.name);
            return v;
        }
    }
    private ArrayList<UserChatItem> mUserChatItems=new ArrayList<>();
    //测试代码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Intent intent=getIntent();
        mTask=(UTask)intent.getSerializableExtra("UTask");
        init();
        mapInit();
        listViewInit();
    }
    public void listViewInit(){
        View v=View.inflate(this.getApplicationContext(),R.layout.task_details_list_header_view,null);
        View fv=View.inflate(this.getApplicationContext(),R.layout.task_detail_chat_list_footer_view,null);
        mTaskTitle=(TextView)v.findViewById(R.id.task_title);
        mTaskLocation=(TextView)v.findViewById(R.id.task_location);
        mTaskReward=(TextView)v.findViewById(R.id.task_details_reward);
        mTaskDetailInfo=(TextView)v.findViewById(R.id.task_detail_info);
        mTaskDetailInfo.setText(mTask.getDescription());
        mTaskReward.setText(String.format(Locale.ENGLISH,"¥%.2f",mTask.getPrice()));
        mTaskTitle.setText(mTask.getTitle());
        CircleImageView civ=(CircleImageView)v.findViewById(R.id.task_detail_circle_image);
        civ.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.user));
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TaskDetailsActivity.this,SingleUserInfoActivity.class);
                startActivity(intent);
            }
        });
        TextView tv=(TextView)v.findViewById(R.id.task_detail_publisher_name);
        tv.setText(mTask.getAuthorUserName());
        tv=(TextView)v.findViewById(R.id.task_detail_stars);
        tv.setText(mTask.getStarString());
        if(mTask.getPosition()!=null){
            Point size=UPublicTool.getScreenSize(this.getApplicationContext(),0.03,0.03);
            SpannableStringBuilder str=UPublicTool.addICONtoString(this.getApplicationContext(),"#LO"+mTask.getToWhere(),"#LO",R.drawable.location,size.x,size.y);
            mTaskLocation.setText(str);
        }
        mListView=(ListView)findViewById(R.id.task_detail_list_view);
       // mListView.addHeaderView(tv);
        mListView.addHeaderView(v);
        mListView.addFooterView(fv);
        mListView.setAdapter(new UserChatItemAdapter(this,mUserChatItems,mTask));
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int scrollY=getListScrollY();
                if(scrollY<=200){
                    mHeadlineLayout.setRedAlpha(1.0f);
                }else if(scrollY>200 && scrollY<=600){
                    mHeadlineLayout.setRedAlpha(1.0f-(scrollY-200)/400.0f);
                }else{
                    mHeadlineLayout.setRedAlpha(0.0f);
                }
            }
        });
    }
    private int getListScrollY() {//获取滚动距离
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mListView.getHeight();
        }
        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }
    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mMapView=(MapView)findViewById(R.id.task_map_view);
        mTencentMap=mMapView.getMap();
        mHeadlineLayout=(UHeadlineLayout)findViewById(R.id.head_line_layout);
        mHeadlineLayout.setTitleRed(mTask.getTitle());
        mHeadlineLayout.setTitleWhite(mTask.getTitle());
        mHeadlineLayout.setRedAlpha(1f);
        mHeadlineLayout.setBackButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void mapInit(){
    }
}
