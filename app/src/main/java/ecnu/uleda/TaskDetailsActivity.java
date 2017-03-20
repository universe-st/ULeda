package ecnu.uleda;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
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
    private PopupWindow mPopupWindow;
    private EditText mPostCommentEdit;
    private Button mButtonLeft;
    private Button mButtonRight;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                mTask=(UTask)msg.obj;
                listViewInit();
                mapInit();
                final UserOperatorController uoc=UserOperatorController.getInstance();
                if(mTask.getAuthorID()==Integer.parseInt(uoc.getId())){
                    mButtonRight.setText("编辑任务");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =new Intent(TaskDetailsActivity.this,TaskEditActivity.class);
                            intent.putExtra("Task",mTask);
                            startActivityForResult(intent,1);
                        }
                    });
                }else if(mTask.getStatus()!=0){
                    mButtonRight.setEnabled(false);
                }
            }else{
                Toast.makeText(TaskDetailsActivity.this,"错误："+(String)msg.obj,Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(resultCode==1){
            Message msg=new Message();
            msg.obj=intent.getSerializableExtra("Task");
            msg.what=0;
            mHandler.sendMessage(msg);
        }else if(resultCode == 2){
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_bt: {
                showPopupWindow();
                mListView.setSelection(mListView.getCount()-1);
                mPostCommentEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                break;
            }
            case R.id.send:{
                String text=mPostCommentEdit.getText().toString();
                if(text.length()==0){
                    Toast.makeText(this,"评论内容不可以为空哦！",Toast.LENGTH_SHORT).show();
                    break;
                }
                UserChatItem uci=new UserChatItem();
                uci.imageID=R.drawable.model1;
                uci.timeBefore="1小时前";
                uci.name="你";
                uci.sayWhat=text;
                mUserChatItems.add(uci);
                mPopupWindow.dismiss();
                mListView.setSelection(mListView.getCount()-1);
                break;
            }
        }
    }

    private void showPopupWindow() {
            View view = View.inflate(this, R.layout.activity_addcomment, null);
            Button send = (Button) view.findViewById(R.id.send);
            mPostCommentEdit=(EditText)view.findViewById(R.id.comment_edit);
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
            mPopupWindow.showAtLocation(comment, Gravity.BOTTOM
                    , 0, 0);
            if (Build.VERSION.SDK_INT != 24) {
                mPopupWindow.update();
            }
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

    @Override
    public void onResume(){
        mMapView.onResume();
        super.onResume();
    }
    @Override
    public void onStop(){
        mMapView.onStop();
        super.onStop();
    }
    @Override
    public void onPause(){
        mMapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy(){
        mMapView.onDestroy();
        super.onDestroy();
    }
    //测试代码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Intent intent=getIntent();
        mTask=(UTask)intent.getSerializableExtra("UTask");
        final UserOperatorController uoc=UserOperatorController.getInstance();
        mMapView=(MapView)findViewById(R.id.task_map_view);
        mTencentMap=mMapView.getMap();
        mMapView.onCreate(savedInstanceState);
        if(!uoc.getIsLogined())return;
        new Thread(){
            @Override
            public void run(){
                try{
                    JSONObject j=ServerAccessApi.getTaskPost(uoc.getId(),uoc.getPassport(),mTask.getPostID());
                    UTask task=new UTask()
                            .setPath( j.getString("path") )
                            .setTitle( j.getString("title") )
                            .setTag( j.getString("tag") )
                            .setPostDate(j.getLong("postdate"))
                            .setPrice(new BigDecimal(j.getString("price")))
                            .setAuthorID(j.getInt("author"))
                            .setDescription(j.getString("description"))
                            .setAuthorUserName(j.getString("authorUsername"))
                            .setAuthorCredit(5)
                            .setPostID(mTask.getPostID())
                            .setActiveTime(j.getLong("activetime"))
                            .setStatus(j.getInt("status"));
                    String[] ps=j.getString("position").split(",");
                    task.setPosition(
                            new LatLng(Double.parseDouble(ps[0]),Double.parseDouble(ps[1]))
                    );
                    Message message = new Message();
                    message.what=0;
                    message.obj=task;
                    mHandler.sendMessage(message);
                }
                catch(UServerAccessException e){
                    e.printStackTrace();
                    Message message=new Message();
                    message.obj=e.getMessage();
                    message.what=1;
                    mHandler.sendMessage(message);
                }
                catch (JSONException e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }.start();
        init();
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
                intent.putExtra("userid",String.valueOf(mTask.getAuthorID()));
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
        mButtonLeft=(Button)findViewById(R.id.comment_bt);
        mButtonLeft.setOnClickListener(this);
        mButtonRight=(Button)findViewById(R.id.right_button);
    }
    public void mapInit(){
        mTencentMap.setZoom(18);
        mTencentMap.setCenter(mTask.getPosition());
        Marker marker=mTencentMap.addMarker(new MarkerOptions()
                        .position(mTask.getPosition())
                        .icon(BitmapDescriptorFactory.defaultMarker()).draggable(false)
        );
        marker.setTitle(mTask.getToWhere());
    }
}
