package ecnu.uleda.view_controller.task.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import net.phalapi.sdk.PhalApiClientResponse;

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
import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UTask;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.RecyclerViewTouchListener;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.MyTaskInFo;
import ecnu.uleda.view_controller.SingleUserInfoActivity;
import ecnu.uleda.view_controller.TaskEditActivity;
import ecnu.uleda.view_controller.task.adapter.TakersAdapter;
import ecnu.uleda.view_controller.task.fragment.TaskMissionFragment;
import ecnu.uleda.view_controller.widgets.BubblePopup;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public class TaskDetailsActivity extends BaseDetailsActivity {

    public static final String EXTRA_UTASK = "UTask";

    private static final int REQUEST_EDIT = 1;

    private static final int MSG_REFRESH_SUCCESS = 0;
    private static final int MSG_REFRESH_FAIL = 1;
    private static final int MSG_COMMENT_GET = 2;
    private static final int MSG_TAKE_SUCCESS = 3;
    private static final int MSG_COMMENT_SUCCESS = 4;
    private static final int MSG_COMMENT_FAILED = 5;
    private static final int MSG_TAKERS_GET = 6;
    private static final int MSG_TASK_SUCCESS = 7;
    private static final int MSG_CANCEL_TAKE = 8;

    private static final int MENU_ITEM_DELETE = 100;
    private static final int MENU_ITEM_DISPUTE = 101;
    private static final int MENU_ITEM_REQUEST_CANCEL = 102;

    public static final String TAG = "TaskDetailsActivity";
    private UTask mTask;
    private TencentMap mTencentMap;
    private boolean isTakenByUser = false;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @BindView(R.id.head_line_layout)
    Toolbar mToolbar;
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
    private BubblePopup mBubblePopup;

    private int mFromMyTaskPosition;
    private String mDisputeDesc;

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
                initTaskData();
                initTakers(UserOperatorController.getInstance());
                invalidateOptionsMenu();
            } else if (msg.what == MSG_COMMENT_GET) {
                addCommentView(mDetailContainer, 2);
            } else if (msg.what == MSG_COMMENT_SUCCESS) {
                mProgress.dismiss();
                Toast.makeText(TaskDetailsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                addCommentView((String) msg.obj, mDetailContainer, 2);
            } else if (msg.what == MSG_COMMENT_FAILED) {
                mProgress.dismiss();
                Toast.makeText(TaskDetailsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MSG_TAKERS_GET) {
                checkIsRelatedToMe();
                initTakersView();
            } else {
                Toast.makeText(TaskDetailsActivity.this, "错误：" + msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initTaskData() {
        final UserOperatorController uoc = UserOperatorController.getInstance();
        if (mTask.getAuthorID() == Integer.parseInt(uoc.getId())) {
            if (mTask.getTag().equals(UTaskManager.TAG_PROJECT)) {
                mButtonRight.setText("编辑项目");
                return;
            }
            switch (mTask.getStatus()) {
                case 0:
                    mButtonRight.setText("编辑任务");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(TaskDetailsActivity.this, TaskEditActivity.class);
                            intent.putExtra("Task", mTask);
                            startActivityForResult(intent, REQUEST_EDIT);
                        }
                    });
                    break;
                case 1:
                case 2:
                    mButtonRight.setText("确认完成");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            completeTaskByAuthor();
                        }
                    });
                    break;
                case 3:
                    mButtonRight.setText("已结束");
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    break;
                case 4:
                    mButtonRight.setText("纠纷中");
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    break;
                case 5:
                    showConfirmCancelDialog();
                    mButtonRight.setText("同意取消");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            agreeCancelTask();
                        }
                    });
                    break;
            }
        } else if (mTask.getStatus() != 0) {
            if (mTask.getTag().equals(UTaskManager.TAG_PROJECT)) {
                mButtonRight.setText("参加");
                return;
            }
            switch (mTask.getStatus()) {
                case 1:
                    mButtonRight.setText("确认完成");
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            completeTaskByTaker();
                        }
                    });
                    break;
                case 2:
                    mButtonRight.setText("待确认");
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    showBubble("等待发布者确认任务完成");
                    break;
                case 3:
                    mButtonRight.setText("已结束");
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    break;
                case 4:
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    mButtonRight.setText("纠纷中");
                    break;
                default:
                    mButtonRight.setText("已被领取");
                    mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                    mButtonRight.setEnabled(false);
                    break;
            }
        } else {
            if (mTask.getTag().equals(UTaskManager.TAG_PROJECT)) {
                mButtonRight.setText("参加");
                return;
            }
            mButtonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptTask(uoc);
                }
            });
        }
    }

    private void agreeCancelTask() {

    }

    private void showConfirmCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("取消请求")
                .setMessage("对方请求取消任务，您可以同意取消或发起纠纷")
                .setNeutralButton("知道了", null)
                .create()
                .show();
    }

    private void completeTaskByTaker() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance().finishTask(mTask.getPostID()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse phalApiClientResponse) throws Exception {
                        if (phalApiClientResponse.getRet() == 200 && phalApiClientResponse.getData().equals("success")) {
                            Toast.makeText(TaskDetailsActivity.this, "完成订单", Toast.LENGTH_SHORT).show();
                            mButtonRight.setText("待确认");
                            mButtonRight.setEnabled(false);
                            mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this,
                                    android.R.color.darker_gray));
                        } else {
                            Log.e("TaskDetailsActivity", "msg: " + phalApiClientResponse.getMsg());
                            Toast.makeText(TaskDetailsActivity.this, "确认失败：" + phalApiClientResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(TaskDetailsActivity.this, "确认失败：未登录或网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void completeTaskByAuthor() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance().verifyFinish(mTask.getPostID()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse phalApiClientResponse) throws Exception {
                        if (phalApiClientResponse.getRet() == 200 && phalApiClientResponse.getData().equals("success")) {
                            Toast.makeText(TaskDetailsActivity.this, "完成订单", Toast.LENGTH_SHORT).show();
                            mButtonRight.setText("已结束");
                            mButtonRight.setEnabled(false);
                            mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this,
                                    android.R.color.darker_gray));
                            if (mFromMyTaskPosition >= 0) {
                                Intent intent = new Intent(MyTaskInFo.ACTION_REFRESH);
                                intent.putExtra(MyTaskInFo.EXTRA_TASK_POS, mFromMyTaskPosition);
                                sendBroadcast(intent);
                            }
                        } else {
                            Log.e("TaskDetailsActivity", "msg: " + phalApiClientResponse.getMsg());
                            Toast.makeText(TaskDetailsActivity.this, "确认失败：" + phalApiClientResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(TaskDetailsActivity.this, "确认失败：未登录或网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIsRelatedToMe() {
        // 未被领取，所有人可访问
        if (mTask.getStatus() == 0) return;
        // 是发布人本人，可以访问自己发布的任务
        int id = Integer.parseInt(UserOperatorController.getInstance().getId());
        if (mTask.getAuthorID() == id) return;
        // 参与过接单，可以访问
        for (UserInfo taker: mTakers) {
            if (Integer.parseInt(taker.getId()) == id)
                return;
        }
        // 当前任务对该用户不可访问，返回
        setResult(RESULT_CANCELED);
        finish();
    }

    private void acceptTask(final UserOperatorController uoc) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(ServerAccessApi.acceptTask(uoc.getId(), uoc.getPassport(), mTask.getPostID()));
                e.onComplete();
            }
        });
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposables.add(d);
            }

            @Override
            public void onNext(@NonNull String s) {
                if ("success".equals(s)) {
                    Toast.makeText(TaskDetailsActivity.this, "成功接受任务", Toast.LENGTH_SHORT).show();
                    mButtonRight.setText("取消抢单");
                    isTakenByUser = true;
                    initTakers(UserOperatorController.getInstance());
                    mButtonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cancelTake();
                        }
                    });
                } else {
                    Toast.makeText(TaskDetailsActivity.this, "接单失败：" + s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                Toast.makeText(TaskDetailsActivity.this, "接单失败：网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        };
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            UserOperatorController uoc = UserOperatorController.getInstance();
            if (mThreadPool.isShutdown()) {
                mThreadPool = Executors.newCachedThreadPool();
            }
            initDetails(uoc);
            initComments(uoc);
            listViewInit();
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
            avatar.displayImage(UPublicTool.BASE_URL_AVATAR + userChatItem.authorAvatar);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void showBubble(String content) {
        mBubblePopup.setPromptText(content);
        mBubblePopup.showAsDropDown(mButtonRight, 0, -(int)UPublicTool.dp2px(this, 8));
    }

    @Override
    public void onStop() {
        mMapView.onStop();
        mDisposables.clear();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTask.getAuthorID() == Integer.parseInt(UserOperatorController.getInstance().getId())) {
            switch (mTask.getStatus()) {
                case 0:
                    MenuItem deleteTaskMenu = menu.add(Menu.NONE, MENU_ITEM_DELETE, 100, "删除");
                    deleteTaskMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    return true;
                case 1:
                case 2:
                    MenuItem disputeMenu = menu.add(Menu.NONE, MENU_ITEM_DISPUTE, 100, "发起纠纷");
                    disputeMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    return true;
            }
            return true;
        } else {
            switch (mTask.getStatus()) {
                case 1:
                    MenuItem forceCancelMenu = menu.add(Menu.NONE, MENU_ITEM_REQUEST_CANCEL, 101, "取消任务");
                    forceCancelMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                case 2:
                    MenuItem disputeMenu = menu.add(Menu.NONE, MENU_ITEM_DISPUTE, 102, "发起纠纷");
                    disputeMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    return true;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void initTakers(final UserOperatorController uoc) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Thread.sleep(500);
                e.onNext(ServerAccessApi.getTakers(uoc.getId(),
                            uoc.getPassport(), mTask.getPostID()));
            }
        })
                .map(new Function<String, JSONArray>() {
                    @Override
                    public JSONArray apply(@NonNull String s) throws Exception {
                        return new JSONArray(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONArray>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull JSONArray data) {
                        try {
                            mTakers.clear();
                            int length = data.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject person = data.getJSONObject(i);
                                JSONObject personDetail = person.getJSONObject("taker_details");
                                UserInfo info = new UserInfo();
                                if (uoc.getId().equals(person.getString("taker_id"))) {
                                    if (mTask.getStatus() == 0) {
                                        mButtonRight.setText("取消抢单");
                                        mButtonRight.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                cancelTake();
                                            }
                                        });
                                    }
                                    isTakenByUser = true;
                                }
                                if (mTask.getStatus() > 0 && Integer.parseInt(person.getString("taker_id")) != mTask.getTaker()) continue;
                                info.setAvatar(UPublicTool.BASE_URL_AVATAR + personDetail.getString("avatar"))
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(TaskDetailsActivity.this, "获取接单人列表失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void cancelTake() {
        // TODO 取消接单接口
        Toast.makeText(TaskDetailsActivity.this, "取消接口还没有", Toast.LENGTH_SHORT).show();
        mButtonRight.setText("抢单");
        mTask.setStatus(0);
    }

    private void initTakersView() {
        if (mTakers.size() > 0) {
            mTaskTakersList.setVisibility(View.VISIBLE);
            mTakersNone.setVisibility(View.GONE);
            mTakersAdapter.setDatas(mTakers);
            if (mTask.getStatus() != 0 && mTakers.size() == 1) {
                mTakersAdapter.setVerifiedTaker(true);
            }
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
                            .setStatus(j.getInt("status"))
                            .setTaker(j.getInt("taker"));
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
        setTitle(mTask.getTitle());
        mInflater = LayoutInflater.from(this);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("发布中...");
        mProgress.setIndeterminate(true);
        mProgress.setCanceledOnTouchOutside(false);
        mTakersAdapter = new TakersAdapter(this, mTakers);
        mTaskTakersList.setAdapter(mTakersAdapter);
        mTaskTakersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
        mTaskTakersList.addOnItemTouchListener(new RecyclerViewTouchListener(mTaskTakersList) {
            @Override
            public void onItemClick(final int position, RecyclerView.ViewHolder viewHolder) {
                if (mTask.getAuthorID() != Integer.valueOf(UserOperatorController.getInstance().getId())
                        || mTakersAdapter.isVerifiedTaker()) return;
                new AlertDialog.Builder(TaskDetailsActivity.this)
                        .setMessage("是否选择" + mTakers.get(position).getUserName() + "作为接单人？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                verifyTaker(position);
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder) {

            }
        });
        mBubblePopup = new BubblePopup(this);
    }

    private void verifyTaker(final int position) {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance()
                        .verifyTaker(mTask.getPostID(), mTakers.get(position).getId()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PhalApiClientResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull PhalApiClientResponse s) {
                        if (s.getRet() == 200) {
                            String data = s.getData();
                            if (data.equals("success")) {
                                Toast.makeText(TaskDetailsActivity.this, "选择接单人成功", Toast.LENGTH_SHORT).show();
                                mTask.setStatus(1);
                                invalidateOptionsMenu();
                                takerChosen(position);
                            } else {
                                Toast.makeText(TaskDetailsActivity.this, "选择接单人失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaskDetailsActivity.this, "选择接单人失败: " + s.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(TaskDetailsActivity.this, "网络异常，选择接单人失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void takerChosen(int position) {
        int oldSize = mTakers.size();
        List<UserInfo> deletingUsers = new ArrayList<>();
        for (int i = 0; i < mTakers.size(); i++) {
            if (i != position) deletingUsers.add(mTakers.get(i));
        }
        if (mTakers.removeAll(deletingUsers)) {
            if (position != 0) mTakersAdapter.notifyItemRangeRemoved(0, position);
            if (position != oldSize - 1)
                mTakersAdapter.notifyItemRangeRemoved(1, oldSize - position - 1);
        }
        mTakersAdapter.setVerifiedTaker(true);
        mButtonRight.setText("确认完成");
        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTaskByAuthor();
            }
        });
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
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE:
                cancelTask();
                break;
            case MENU_ITEM_REQUEST_CANCEL:
                requestGiveUpTask();
                break;
            case MENU_ITEM_DISPUTE:
                showDisputeDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDisputeDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_edit_dispute);
        dialog.setCanceledOnTouchOutside(false);
        Window dialogView = dialog.getWindow();
        dialogView.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final EditText disputeDescEdt = (EditText) dialogView.findViewById(R.id.id_dispute_desc);
        Button cancelDispute = (Button) dialogView.findViewById(R.id.id_dispute_cancel);
        final Button releaseDispute = (Button) dialogView.findViewById(R.id.id_dispute_release);
        cancelDispute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisputeDesc = disputeDescEdt.getText().toString();
                dialog.dismiss();
            }
        });
        releaseDispute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisputeDesc = disputeDescEdt.getText().toString();
                dialog.dismiss();
                releaseDispute();
            }
        });
        if (!TextUtils.isEmpty(mDisputeDesc)) {
            disputeDescEdt.setText(mDisputeDesc);
        }
    }

    private void releaseDispute() {
        new AlertDialog.Builder(this)
                .setTitle("确认发起")
                .setMessage("一旦提交纠纷，就不能修改陈述了哦，是否确认？")
                .setPositiveButton("是，发布", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 真正发布纠纷
                    }
                })
                .setNegativeButton("返回编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDisputeDialog();
                    }
                })
                .create()
                .show();
    }

    private void requestGiveUpTask() {
        new AlertDialog.Builder(this)
                .setTitle("确认操作")
                .setMessage("请求取消需要发布者确认（不扣除信用分），强制取消无需确认，但将扣除您一定信用分，是否继续？")
                .setNeutralButton("请求取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        friendlyGiveUp();
                    }
                })
                .setNeutralButton("强制取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forceGiveUp();
                    }
                })
                .setNegativeButton("不取消了", null)
                .create()
                .show();
    }

    private void forceGiveUp() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance().forceGiveUpTask(mTask.getPostID()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PhalApiClientResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull PhalApiClientResponse response) {
                        if (response.getRet() == 200 && response.getData().equals("success")) {
                            Toast.makeText(TaskDetailsActivity.this, "强制取消成功", Toast.LENGTH_SHORT).show();
                            mButtonRight.setText("已结束");
                            mButtonRight.setEnabled(false);
                            mButtonRight.setBackgroundColor(ContextCompat.getColor(TaskDetailsActivity.this, android.R.color.darker_gray));
                        } else {
                            Toast.makeText(TaskDetailsActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(TaskDetailsActivity.this, "申请取消失败：网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void friendlyGiveUp() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance().giveUpTask(mTask.getPostID()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PhalApiClientResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull PhalApiClientResponse response) {
                        if (response.getRet() == 200 && response.getData().equals("success")) {
                            Toast.makeText(TaskDetailsActivity.this, "申请取消成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskDetailsActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(TaskDetailsActivity.this, "申请取消失败：网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK);
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void cancelTask() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                e.onNext(UTaskManager.getInstance().cancelTask(mTask.getPostID()));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PhalApiClientResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull PhalApiClientResponse phalApiClientResponse) {
                        if (phalApiClientResponse.getRet() == 200) {
                            String data = phalApiClientResponse.getData();
                            if ("success".equals(data)) {
                                Toast.makeText(TaskDetailsActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK);
                                        finish();
                                        sendBroadcast(new Intent(TaskMissionFragment.ACTION_REFRESH));
                                    }
                                }, 500);
                            } else {
                                Toast.makeText(TaskDetailsActivity.this, "取消失败：" + phalApiClientResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaskDetailsActivity.this, "取消失败：" + phalApiClientResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(TaskDetailsActivity.this, "取消失败：网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void initActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        setTitle("任务详情");
        Intent intent = getIntent();
        mTask = (UTask) intent.getSerializableExtra(EXTRA_UTASK);
        mFromMyTaskPosition = intent.getIntExtra("position", -1);
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

    public static void startActivityFromMyTask(Context context, int position, int flag) {
        UTask uTask = new UTask();
        int index = 0;
        int temp = 0;
        if(position == 0) {
            index = 0;
        } else {
            if(position % 10 == 0) {
                index = position / 10 - 1;
            } else {
                index = position / 10;
            }
        }
        temp = position - 10 * index;
        startFromUTask(context, index, temp, flag);
    }

    private static void startFromUTask(final Context context, final int index, final int temp, final int flag) {
        Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                e.onNext(ServerAccessApi.getUserTask(index, flag));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<JSONArray>() {
                    @Override
                    public void accept(JSONArray jsonArray) throws Exception {
                        JSONObject json = jsonArray.getJSONObject(temp);
                        UTask uTask = new UTask();
                        uTask.setTitle(json.getString("title"))
                                .setStatus(Integer.parseInt(json.getString("status")))
                                .setAuthorID(Integer.parseInt(json.getString("author")))
                                .setAuthorAvatar(json.getString("authorAvatar"))
                                .setAuthorUserName(json.getString("authorUsername"))
                                .setAuthorCredit(Integer.parseInt(json.getString("authorCredit")))
                                .setTag(json.getString("tag"))
                                .setDescription(json.getString("description"))
                                .setPostDate(Long.parseLong(json.getString("postdate")))
                                .setActiveTime(Long.parseLong(json.getString("activetime")))
                                .setPath(json.getString("path"))
                                .setPrice(BigDecimal.valueOf(Double.parseDouble(json.getString("price"))))
                                .setPostID(json.getString("postID"))
                                .setTakersCount(Integer.parseInt(json.getString("taker")));
                        Intent intent = new Intent(context, TaskDetailsActivity.class);
                        intent.putExtra("UTask", uTask);
                        intent.putExtra("position", 10 * index + temp);
                        context.startActivity(intent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

}
