package ecnu.uleda.function_module;

import android.content.Context;
import android.util.Log;

import com.tencent.mapsdk.raster.model.LatLng;

import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.tool.UPublicTool;

/**
 * Created by Shensheng on 2017/1/19.
 * 任务管理者
 * 管理列表和地图上的任务
 */

public class UTaskManager {
    /*
    * 该类遵循单例模式。
    * 持有两个列表，一个表示任务列表中显示的任务
    * 一个表示地图上显示的任务
    * */
    private static final String TASK_IN_LIST_FILE = "taskInList.json";
    private static UTaskManager sInstance = null;
    private long mLastRefreshTime = 0;
    private UserOperatorController mUOC;
    private ArrayList<UTask> mTasksInList;
    private ArrayList<UTask> mTasksInMap;
    public static final String TIME_LAST = "time";
    public static final String PRICE_DES = "priceDes";
    public static final String PRICE_ASC = "priceAsc";
    public static final String DISTANCE = "distance";
    public static final String TAG_ALL = "全部";
    public static final String TAG_PROJECT = "项目找人";
    private String mSortBy = TIME_LAST;
    private String mTag = "全部";
    private String mLocation = "31.2296,121.403";
    private Semaphore mSemaphoreReadTaskList = new Semaphore(1);

    public static UTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new UTaskManager();
        }
        return sInstance;
    }

    public void setLocation(String loc) {
        mLocation = loc;
    }

    public void setSortBy(String sortBy) {
        mSortBy = sortBy;
    }

    public UTaskManager setTag(String tag) {
        mTag = tag;
        return this;
    }

    public ArrayList<UTask> getTasksInList() {
        return mTasksInList;
    }

    private UTaskManager() {
        mTasksInList = new ArrayList<>();
        mTasksInMap = new ArrayList<>();
    }

//    public ListAdapter setListView(ListView listView, Context context) {
//        //将一个ListView的内容设置为我们的任务
//        ListAdapter la = new TaskListAdapter(context, mTasksInList);
//        listView.setAdapter(la);
//        return la;
//    }

    public void refreshTaskInList(Context context) throws UServerAccessException {
        /*
        * TODO:访问服务器，更新任务列表。
        * */
        mUOC = UserOperatorController.getInstance();
        if (!mUOC.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            try {
                mSemaphoreReadTaskList.acquire();
                JSONArray array = ServerAccessApi.getTaskList(
                        mUOC.getId(),
                        mUOC.getPassport(),
                        mSortBy,
                        "0",
                        "10",
                        mTag,
                        "31.2296,121.403");// 便于家中测试
                mTasksInList.clear();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject j = array.getJSONObject(i);
                    UTask task = new UTask()
                            .setPath(j.getString("path"))
                            .setTitle(j.getString("title"))
                            .setTag(j.getString("tag"))
                            .setPostDate(j.getLong("postdate"))
                            .setPrice(new BigDecimal(j.getString("price")))
                            .setAuthorID(j.getInt("author"))
                            .setAuthorUserName(j.getString("authorUsername"))
                            .setAuthorCredit(j.getInt("authorCredit"))
                            .setPostID(j.getString("postID"))
                            .setActiveTime(j.getLong("activetime"))
                            .setTakersCount(j.getInt("taker_count"));
                    try {
                        task.setAvatar(UPublicTool.BASE_URL_AVATAR + j.getString("authorAvatar"));
                    } catch (JSONException e) {
                        task.setAvatar("xiaohong.jpg");
                    }
                    mTasksInList.add(task);
                }
                writeTaskInListToFile(context, array.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (UServerAccessException e) {
                if (e.getStatus() == 416) {
                    e.printStackTrace();
                    mTasksInList.clear();
                } else {
                    throw e;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mSemaphoreReadTaskList.release();
            }
        }
    }

    public void writeTaskInListToFile(Context context, String jsonArray) {
        if (mTasksInList == null || mTasksInList.size() == 0) return;
        BufferedWriter w = null;
        try {
            FileOutputStream output = context.openFileOutput(TASK_IN_LIST_FILE, Context.MODE_PRIVATE);
            w = new BufferedWriter(new OutputStreamWriter(output));
            w.write(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void refreshTaskInListFromFile(Context context) {
        if (mTasksInList != null && mTasksInList.size() > 0) return;
        BufferedReader reader = null;
        try {
            mSemaphoreReadTaskList.acquire();
            if (mTasksInList != null && mTasksInList.size() > 0)
                throw new IllegalStateException("taskInList already loaded from server");
            FileInputStream fis = context.openFileInput(TASK_IN_LIST_FILE);
            reader = new BufferedReader(new InputStreamReader(fis));
            String content = reader.readLine();
            JSONArray array = new JSONArray(content);
            mTasksInList.clear();
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject j = array.getJSONObject(i);
                UTask task = new UTask()
                        .setPath(j.getString("path"))
                        .setTitle(j.getString("title"))
                        .setTag(j.getString("tag"))
                        .setPostDate(j.getLong("postdate"))
                        .setPrice(new BigDecimal(j.getString("price")))
                        .setAuthorID(j.getInt("author"))
                        .setAuthorUserName(j.getString("authorUsername"))
                        .setAuthorCredit(j.getInt("authorCredit"))
                        .setPostID(j.getString("postID"))
                        .setActiveTime(j.getLong("activetime"))
                        .setTakersCount(j.getInt("taker_count"));
                try {
                    task.setAvatar(UPublicTool.BASE_URL_AVATAR + j.getString("authorAvatar"));
                } catch (JSONException e) {
                    task.setAvatar("xiaohong.jpg");
                }
                mTasksInList.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mSemaphoreReadTaskList.release();
        }
    }

    /**
     * @param n 从目前任务列表的最后一项开始向后从服务器获取n个任务项
     * @return 返回true表示至少有一个新item
     * @throws UServerAccessException
     */
    public boolean loadMoreTaskInList(int n) throws UServerAccessException {
        mUOC = UserOperatorController.getInstance();
        String start = mTasksInList.get(mTasksInList.size() - 1).getPostID();
        if (!mUOC.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            try {
                JSONArray array = ServerAccessApi.getTaskList(
                        mUOC.getId(),
                        mUOC.getPassport(),
                        mSortBy,
                        start,
                        String.valueOf(n),
                        mTag,
                        mLocation);
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject j = array.getJSONObject(i);
                    UTask task = new UTask()
                            .setPath(j.getString("path"))
                            .setTitle(j.getString("title"))
                            .setTag(j.getString("tag"))
                            .setPostDate(j.getLong("postdate"))
                            .setPrice(new BigDecimal(j.getString("price")))
                            .setAuthorID(j.getInt("author"))
                            .setAuthorUserName(j.getString("authorUsername"))
                            .setAuthorCredit(j.getInt("authorCredit"))
                            .setPostID(j.getString("postID"))
                            .setActiveTime(j.getLong("activetime"))
                            .setTakersCount(j.getInt("taker_count"));
                    try {
                        task.setAvatar(UPublicTool.BASE_URL_AVATAR + j.getString("authorAvatar"));
                    } catch (JSONException e) {
                        task.setAvatar("xiaohong.jpg");
                    }
                    mTasksInList.add(task);
                }
                return length == n;
            } catch (JSONException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return false;
    }

//    public void init() throws UServerAccessException {
//        refreshTaskInList();
//    }

    public void refreshTasksInMap() throws UServerAccessException {
        mTasksInMap = new ArrayList<>();
        if (mUOC == null) {
            mUOC = UserOperatorController.getInstance();
        }
        JSONArray array = ServerAccessApi.getTaskList(
                mUOC.getId(),
                mUOC.getPassport(),
                TIME_LAST,
                String.valueOf(0),
                String.valueOf(100),
                "全部",
                mLocation);
        int size = array.length();
        try {
            for (int i = 0; i < size; i++) {
                JSONObject j = array.getJSONObject(i);
                UTask task = new UTask()
                        .setPath(j.getString("path"))
                        .setTitle(j.getString("title"))
                        .setTag(j.getString("tag"))
                        .setPostDate(j.getLong("postdate"))
                        .setPrice(new BigDecimal(j.getString("price")))
                        .setAuthorID(j.getInt("author"))
                        .setAuthorUserName(j.getString("authorUsername"))
                        .setAuthorCredit(j.getInt("authorCredit"))
                        .setPostID(j.getString("postID"))
                        .setActiveTime(j.getLong("activetime"));
                String[] pos = j.getString("position").split(",");
                task.setPosition(new LatLng(Double.parseDouble(pos[0]), Double.parseDouble(pos[1])));
                mTasksInMap.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean getTakersInfo(int postId) throws UServerAccessException {
        mUOC = UserOperatorController.getInstance();
        if (!mUOC.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            String result = ServerAccessApi.getTakers(
                    mUOC.getId(),
                    mUOC.getPassport(),
                    String.valueOf(postId));

        }
        return false;
    }


    public void waitRefreshTasksInMap() throws UServerAccessException {
        long time = System.currentTimeMillis();
        if (time - mLastRefreshTime > 5000 || mTasksInMap.size() == 0) {
            mLastRefreshTime = time;
            refreshTasksInMap();
        }
    }

    public static final int RECOMMEND = 0;
    public static final int HELP_EACH_OTHER = 1;
    public static final int U_ACTIVITY = 2;
    public static final int FOLLOW = 3;

    public ArrayList<UTask> getTasksInMap(int flag) {
        ArrayList<UTask> tasks = new ArrayList<>();
        switch (flag) {
            case RECOMMEND:
                return mTasksInMap;
            case HELP_EACH_OTHER:
                for (UTask task : mTasksInMap) {
                    String tag = task.getTag();
                    if (tag.equals("跑腿代步") || tag.equals("生活服务") || tag.equals("学习帮助") || tag.equals("技术难题") || tag.equals("寻物启示")) {
                        tasks.add(task);
                    }
                }
                return tasks;
            case U_ACTIVITY:
                for (UTask task : mTasksInMap) {
                    String tag = task.getTag();
                    if (tag.equals("活动相关")) {
                        tasks.add(task);
                    }
                }
                return tasks;
            case FOLLOW:
                return tasks;
            default:
                return tasks;
        }
    }

    public PhalApiClientResponse verifyTaker(String postID, String verifyID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            return ServerAccessApi.verifyTaker(
                    uoc.getId(),
                    uoc.getPassport(),
                    postID,
                    verifyID);
        }
    }

    public PhalApiClientResponse giveUpTask(String postID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            return ServerAccessApi.giveUpTask(
                    uoc.getId(),
                    uoc.getPassport(),
                    postID);
        }
    }

    public PhalApiClientResponse forceGiveUpTask(String postID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            return ServerAccessApi.forceGiveUpTask(
                    uoc.getId(),
                    uoc.getPassport(),
                    postID);
        }
    }

    public PhalApiClientResponse cancelTask(String postID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            return ServerAccessApi.cancelTask(postID);
        }
    }

    public PhalApiClientResponse verifyFinish(String postID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            return ServerAccessApi.verifyFinish(uoc.getId(), uoc.getPassport(), postID);
        }
    }

    public PhalApiClientResponse finishTask(String taskID) throws UServerAccessException {
        UserOperatorController uoc = UserOperatorController.getInstance();
        if (!uoc.getIsLogined()) {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        } else {
            Log.e("TaskDetailsActivity", "id = " + uoc.getId() + ", passport = " + uoc.getPassport() + ", taskID = " + taskID);
            return ServerAccessApi.finishTask(uoc.getId(), uoc.getPassport(), taskID);
        }
    }
}
