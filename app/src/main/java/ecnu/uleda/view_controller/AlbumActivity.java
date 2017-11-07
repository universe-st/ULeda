package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.view_controller.task.adapter.AlbumGridViewAdapter;

/**
 * Created by 63516 on 2017/9/21.
 */

public class AlbumActivity extends AppCompatActivity {
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    //完成按钮
    private Button okButton;
    // 返回按钮
    private Button back;
    // 取消按钮
    private Button cancel;
    private Intent intent;
    // 预览按钮
    private Button preview;
    private AlbumActivity mContext;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    public static Bitmap bitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        PublicWay.activityList.add(this);
        mContext = this;
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        Intent intent1 = new Intent("com.example.broadcasttest.MY_BROADCAST");
        sendBroadcast(intent1);
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        IntentFilter filter1 = new IntentFilter("com.example.broadcasttest.MY_BROADCAST");
        MyBroadcastReceiver myBroadcastReceiver1=new MyBroadcastReceiver();
//        registerReceiver(myBroadcastReceiver1, filter);
        registerReceiver(myBroadcastReceiver, filter);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        init();
        initListener();
        //这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    // 预览按钮的监听
    private class PreviewListener implements View.OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size() > 0) {
                intent.putExtra("position", "1");
                intent.setClass(AlbumActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        }

    }

    // 完成按钮的监听
    private class AlbumSendListener implements View.OnClickListener {
        public void onClick(View v) {
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            intent.setClass(mContext,ReleasedUcircleActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // 返回按钮监听
    private class BackListener implements View.OnClickListener {
        public void onClick(View v) {
            intent.setClass(AlbumActivity.this, ImageFile.class);
            startActivity(intent);
            finish();
        }
    }

    // 取消按钮的监听
    private class CancelListener implements View.OnClickListener {
        public void onClick(View v) {
            Bimp.tempSelectBitmap.clear();
            finish();
//            intent.setClass(mContext,ReleasedUcircleActivity.class);
//            startActivity(intent);
        }
    }

    public class MyBroadcastReceiver  extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
            Toast.makeText(context, "Album进来了", Toast.LENGTH_SHORT).show();
        }
    }


    // 初始化，给一些对象赋值
    private void init() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();

        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll( contentList.get(i).imageList );
        }

        back = (Button) findViewById(R.id.plu_back);
        cancel = (Button) findViewById(R.id.plu_cancel);
        cancel.setOnClickListener(new CancelListener());
        back.setOnClickListener(new BackListener());
        preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(new PreviewListener());
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                + "/"+ PublicWay.num+")");
    }

    private void initListener() {

        Log.d("AlbumActivity","成功调用initListener");
        gridImageAdapter
                .setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {


                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,Button chooseBt) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
                            toggleButton.setChecked(false);
                            chooseBt.setVisibility(View.GONE);
                            if (!removeOneData(dataList.get(position))) {
                                Toast.makeText(AlbumActivity.this, R.string.only_choose_num,
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        if (isChecked) {
                            chooseBt.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
                                    + "/"+ PublicWay.num+")");
                        } else {
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
                        }
                        isShowOkBt();
                    }
                });

        okButton.setOnClickListener(new AlbumSendListener());

    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
            preview.setPressed(true);
            okButton.setPressed(true);
            preview.setClickable(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
            preview.setTextColor(Color.WHITE);
        } else {
            okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
            preview.setPressed(false);
            preview.setClickable(false);
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
            preview.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            intent.setClass(AlbumActivity.this, ImageFile.class);
            startActivity(intent);
        }
        return false;

    }
    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
