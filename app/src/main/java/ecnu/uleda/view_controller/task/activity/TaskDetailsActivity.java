package ecnu.uleda.view_controller.task.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.view_controller.SingleUserInfoActivity;
import ecnu.uleda.view_controller.TaskEditActivity;
import ecnu.uleda.view_controller.task.adapter.TakersAdapter;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public class TaskDetailsActivity extends BaseDetailsActivity {

    public static final String EXTRA_UTASK = "UTask";
    private static final int MSG_REFRESH_SUCCESS = 0;
    private static final int MSG_REFRESH_FAIL = 1;
    private static final int MSG_COMMENT_GET = 2;
    private static final int MSG_TAKE_SUCCESS = 3;
    private static final int MSG_COMMENT_SUCCESS = 4;
    private static final int MSG_COMMENT_FAILED = 5;
    private static final int MSG_TAKERS_GET = 6;
    private static final int MSG_TASK_SUCCESS = 7;
    private UTask mTask;
    private TencentMap mTencentMap;

    @BindView(R.id.head_line_layout)
    Toolbar mToolbar;
    @BindView(R.id.task_tool_title)
    TextView mHeadlineLayout;
    @BindView(R.id.task_map_view)
    MapView mMapView;
    @BindView(R.id.comment_bt)
    Button mButtonLeft;
    @BindView(R.id.right_button)
    Button mButtonRight;
    @BindView(R.id.task_title)
    TextView mTaskTitle;
    @BindView(R.id.task_location)
    TextView mTaskLocation;
    @BindView(R.id.task_details_reward)
    TextView mTaskReward;
    @BindView(R.id.task_detail_publisher_name)
    TextView mTaskPublisher;
    @BindView(R.id.task_detail_circle_image)
    SketchImageView mTaskAvatar;
    @BindView(R.id.task_detail_info)
    TextView mTaskDetailInfo;
    @BindView(R.id.task_detail_state)
    TextView mTaskTimeLimit;
    @BindView(R.id.task_detail_stars)
    TextView mTaskPublisherStars;
    @BindView(R.id.task_takers_list)
    RecyclerView mTaskTakersList;
    @BindView(R.id.task_detail_list_view)
    LinearLayout mDetailContainer;
    @BindView(R.id.details_scroll)
    ScrollView mScrollView;
    @BindView(R.id.task_takers_none)
    TextView mTakersNone;

    private ProgressDialog mProgress;

    private ExecutorService mThreadPool;
    private LayoutInflater mInflater;
    private List<UserInfo> mTakers = new ArrayList<>();
    private TakersAdapter mTakersAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REFRESH_SUCCESS) {
                mTask = (UTask) msg.obj;
                listViewInit();
                mapInit();
                final UserOperatorController uoc = UserOperatorController.getInstance();
                if (mTask.getAuthorID() == Integer.parseInt(uoc.getId())) {
                    mButtonRight.setText("编辑任务");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(TaskDetailsActivity.this, TaskEditActivity.class);
                            intent.putExtra("Task", mTask);
                            startActivityForResult(intent, 1);
                        }
                    });
                } else if (mTask.getStatus() != 0) {
                    mButtonRight.setEnabled(false);
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    switch (mTask.getStatus()) {
                        case 4:
                            mButtonRight.setText("已失效");
                            break;
                        default:
                            mButtonRight.setText("已被领取");
                            break;
                    }
                } else {
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        String s = ServerAccessApi.acceptTask(uoc.getId(), uoc.getPassport(), mTask.getPostID());
                                        if (s.equals("success")) {
                                            Message msg = new Message();
                                            msg.what = MSG_TAKE_SUCCESS;
                                            mHandler.sendMessage(msg);
                                        }
                                    } catch (UServerAccessException e) {
                                        e.printStackTrace();
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.obj = e.getMessage();
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            }.start();
                        }
                    });
                }
                initTakers(uoc);
            } else if (msg.what == MSG_TAKE_SUCCESS) {
                Toast.makeText(TaskDetailsActivity.this, "成功接受任务", Toast.LENGTH_SHORT).show();
                mTask.setStatus(1);
                mButtonRight.setText("取消抢单");
                mButtonRight.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Message msg = new Message();
                        msg.what = 0;
                        mButtonRight.setText("抢单");
                        mTask.setStatus(0);
                    }
                });


            } else if (msg.what == MSG_COMMENT_GET) {
                addCommentView(mDetailContainer, 2);
            } else if (msg.what == MSG_COMMENT_SUCCESS) {
                addCommentView((String) msg.obj, mDetailContainer, 2);
            } else if (msg.what == MSG_COMMENT_FAILED) {
                mProgress.dismiss();
                Toast.makeText(TaskDetailsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MSG_TAKERS_GET) {
                initTakersView();
            } else {
                Toast.makeText(TaskDetailsActivity.this, "错误：" + msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) return;
        if (resultCode == 1) {
            Message msg = new Message();
            msg.obj = intent.getSerializableExtra("Task");
            msg.what = 0;
            mHandler.sendMessage(msg);
        } else if (resultCode == 2) {
            finish();
        }
    }

    @OnClick(R.id.comment_bt)
    void comment() {
        showCommentPopup();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }




    public View getChatView(UserChatItem userChatItem) {
        View v;
        if (mTask.getAuthorUserName().equals(userChatItem.name)) {
            v = mInflater.inflate(R.layout.task_detail_chat_item_right, mDetailContainer, false);
        } else {
            v = mInflater.inflate(R.layout.task_detail_chat_item_left, mDetailContainer, false);
        }
        SketchImageView avatar = (SketchImageView) v.findViewById(R.id.task_detail_chat_item_circle);
        DisplayOptions options = new DisplayOptions().setImageShaper(new CircleImageShaper());
        avatar.setOptions(options);
        // for testing
        if (userChatItem.authorAvatar.equals("test")) {
            avatar.displayResourceImage(R.drawable.model1);
        } else {
            avatar.displayImage(userChatItem.authorAvatar);
        }
        TextView tv = (TextView) v.findViewById(R.id.say_what);
        tv.setText(userChatItem.sayWhat);
        tv = (TextView) v.findViewById(R.id.time_before);
        tv.setText(UPublicTool.timeBefore(userChatItem.postDate));
        tv = (TextView) v.findViewById(R.id.name_of_chatter);
        tv.setText(userChatItem.name);
        return v;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        mMapView.onStop();
        mThreadPool.shutdownNow();
        super.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }


    private void initTakers(final UserOperatorController uoc) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    String response = ServerAccessApi.getTakers(uoc.getId(),
                            uoc.getPassport(), mTask.getPostID());
                        mTakers.clear();
                        JSONArray data = new JSONArray(response);
                        int length = data.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject person = data.getJSONObject(i);
                            JSONObject personDetail = person.getJSONObject("taker_details");
                            UserInfo info = new UserInfo();
                            info.setAvatar("fake")
                                    .setId(person.getString("taker_id"))
                                    .setUserName(personDetail.getString("username"))
                                    .setSex(personDetail.getInt("sex"))
                                    .setBirthday(personDetail.getString("birthday"))
                                    .setStudentId(personDetail.getString("studentid"))
                                    .setRealName(personDetail.getString("realname"))
                                    .setPhone(personDetail.getString("phone"))
                                    .setSignature(personDetail.getString("signature"));
                            mTakers.add(info);
                        }
                        mHandler.sendEmptyMessage(MSG_TAKERS_GET);
                } catch (UServerAccessException | JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void initTakersView() {
        if (mTakers.size() > 0) {
            mTakersAdapter.setDatas(mTakers);
        } else {
            mTaskTakersList.setVisibility(View.GONE);
            mTakersNone.setVisibility(View.VISIBLE);
        }
//        mTaskTakersList.smoothScrollToPosition(0);
//        mTaskTakersList.requestFocus();
    }

    private void initComments(final UserOperatorController uoc) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = ServerAccessApi.getComment(uoc.getId(),
                            uoc.getPassport(), mTask.getPostID(), String.valueOf(0));
                    if (!response.equals("null")) {
                        setChatItems((List<UserChatItem>) new Gson().fromJson(response,
                                new TypeToken<List<UserChatItem>>() {
                                }.getType()));
                    } else {
                        getMUserChatItems().clear();
                    }
                    mHandler.sendEmptyMessage(MSG_COMMENT_GET);
                } catch (UServerAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initDetails(final UserOperatorController uoc) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject j = ServerAccessApi.getTaskPost(uoc.getId(), uoc.getPassport(), mTask.getPostID());
                    UTask task = new UTask()
                            .setPath(j.getString("path"))
                            .setTitle(j.getString("title"))
                            .setTag(j.getString("tag"))
                            .setPostDate(j.getLong("postdate"))
                            .setPrice(new BigDecimal(j.getString("price")))
                            .setAuthorID(j.getInt("author"))
                            .setDescription(j.getString("description"))
                            .setAuthorUserName(j.getString("authorUsername"))
                            .setAuthorCredit(j.getInt("authorCredit"))
                            .setPostID(mTask.getPostID())
                            .setActiveTime(j.getLong("activetime"))
                            .setStatus(j.getInt("status"));
                    String[] ps = j.getString("position").split(",");
                    task.setPosition(
                            new LatLng(Double.parseDouble(ps[0]), Double.parseDouble(ps[1]))
                    );
                    Message message = new Message();
                    message.what = MSG_REFRESH_SUCCESS;
                    message.obj = task;
                    mHandler.sendMessage(message);
                } catch (UServerAccessException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.obj = e.getMessage();
                    message.what = MSG_REFRESH_FAIL;
                    mHandler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }


    public void listViewInit() {
//        View fv = View.inflate(this.getApplicationContext(), R.layout.task_detail_chat_list_footer_view, mListView);
        String description = mTask.getDescription();
        if (description == null) {
            mTaskDetailInfo.setText("加载中...");
        } else if ("".equals(description)) {
            mTaskDetailInfo.setText("该用户什么都木有填写~");
        } else {
            mTaskDetailInfo.setText(description);
        }
        mTaskReward.setText(String.format(Locale.ENGLISH, "¥%.2f", mTask.getPrice()));
        mTaskTitle.setText(mTask.getTitle());
        DisplayOptions options = new DisplayOptions();
        options.setImageShaper(new CircleImageShaper());
        mTaskAvatar.setOptions(options);
        if (mTask.getAvatar() == null) {
            mTaskAvatar.displayResourceImage(R.drawable.ic_person_grey600_48dp);
        } else {
            mTaskAvatar.displayImage(mTask.getAvatar());
        }
        mTaskAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskDetailsActivity.this, SingleUserInfoActivity.class);
                intent.putExtra("userid", String.valueOf(mTask.getAuthorID()));
                startActivity(intent);
            }
        });
        mTaskPublisher.setText(mTask.getAuthorUserName());
        mTaskPublisherStars.setText(mTask.getStarString());
        if (mTask.getPosition() != null) {
            Point size = UPublicTool.getScreenSize(this.getApplicationContext(), 0.03, 0.03);
            SpannableStringBuilder str = UPublicTool.addICONtoString(this.getApplicationContext(), "#LO" + mTask.getToWhere(), "#LO", R.drawable.location, size.x, size.y);
            mTaskLocation.setText(str);
        }
        if (mTask.getStatus() == 0) {
            Date date = new Date((mTask.getPostDate() + mTask.getActiveTime()) * 1000);
            mTaskTimeLimit.setText("剩余时间" + UPublicTool.timeLeft(date));
        }
//        mTaskTakersList.setVisibility(View.GONE);
    }


//    private int getListScrollY() {//获取滚动距离
//        View c = mListView.getChildAt(0);
//        if (c == null) {
//            return 0;
//        }
//
//        int firstVisiblePosition = mListView.getFirstVisiblePosition();
//        int top = c.getTop();
//
//        int headerHeight = 0;
//        if (firstVisiblePosition >= 1) {
//            headerHeight = mListView.getHeight();
//        }
//        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
//    }


    public void init() {
        mHeadlineLayout.setText(mTask.getTitle());
        mInflater = LayoutInflater.from(this);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("发布中...");
        mProgress.setIndeterminate(true);
        mProgress.setCanceledOnTouchOutside(false);
        mTakersAdapter = new TakersAdapter(this, mTakers) {

            @Override
            protected void onItemClick(View v, int pos) {
                // TODO 选择接单人
            }
        };
        mTaskTakersList.setAdapter(mTakersAdapter);
        mTaskTakersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
    }

    public void mapInit() {
        mTencentMap.setZoom(18);
        mTencentMap.setCenter(mTask.getPosition());
        Marker marker = mTencentMap.addMarker(new MarkerOptions()
                .position(mTask.getPosition())
                .icon(BitmapDescriptorFactory.defaultMarker()).draggable(false)
        );
        marker.setTitle(mTask.getToWhere());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        setTitle("");
        Intent intent = getIntent();
        mTask = (UTask) intent.getSerializableExtra(EXTRA_UTASK);
        init();
        final UserOperatorController uoc = UserOperatorController.getInstance();
        mTencentMap = mMapView.getMap();
        mMapView.onCreate(savedInstanceState);

        if (!uoc.getIsLogined()) return;
        mThreadPool = Executors.newCachedThreadPool();
        initDetails(uoc);
        initComments(uoc);
        listViewInit();
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_task_details);
    }

    @Override
    public void onSubmitComment(@NotNull final String comment) {
        mProgress.show();
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                UserOperatorController uoc = UserOperatorController.getInstance();
                try {
                    String result = ServerAccessApi.postComment(uoc.getId(), uoc.getPassport(),
                            mTask.getPostID(), comment);
                    if (result.equals("success")) {
                        Message msg = Message.obtain();
                        msg.obj = comment;
                        msg.what = MSG_COMMENT_SUCCESS;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(MSG_COMMENT_FAILED);
                    }
                } catch (UServerAccessException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_COMMENT_FAILED);
                }
            }
        });
    }

    //测试代码
    public static class UserChatItem {
        @SerializedName("author")
        public int authorId;

        @SerializedName("body")
        public String sayWhat;

        @SerializedName("authorUsername")
        public String name = "";

        public String authorAvatar;
        public int commentID;
        public long postDate;

        public UserChatItem(int authorId, String sayWhat, String name, String authorAvatar,
                            int commentID, long postDate) {
            this.authorId = authorId;
            this.sayWhat = sayWhat;
            this.name = name;
            this.authorAvatar = authorAvatar;
            this.commentID = commentID;
            this.postDate = postDate;
        }
    }

}
