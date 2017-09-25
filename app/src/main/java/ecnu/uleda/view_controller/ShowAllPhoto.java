package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import ecnu.uleda.R;
import ecnu.uleda.view_controller.task.adapter.AlbumGridViewAdapter;

/**
 * Created by 63516 on 2017/9/22.
 */

public class ShowAllPhoto extends Activity {
    private GridView gridView;
    private ProgressBar progressBar;
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private Button okButton;
    // 预览按钮
    private Button preview;
    // 返回按钮
    private Button back;
    // 取消按钮
    private Button allphoto_cancel;
    // 标题
    private TextView headTitle;
    private Intent intent;
    private Context mContext;
    public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_show_all_photo);
        PublicWay.activityList.add(this);
        mContext = this;
        back = (Button) findViewById(R.id.showallphoto_back);
        allphoto_cancel = (Button) findViewById(R.id.show_allphoto_cancel);
        preview = (Button) findViewById(R.id.showallphoto_preview);
        okButton = (Button) findViewById(R.id.showallphoto_ok_button);
        headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
        this.intent = getIntent();
        String folderName = intent.getStringExtra("folderName");
        if (folderName.length() > 8) {
            folderName = folderName.substring(0, 9) + "...";
        }
        headTitle.setText(folderName);
        allphoto_cancel.setOnClickListener(new CancelListener());
        back.setOnClickListener(new BackListener(intent));
        preview.setOnClickListener(new PreviewListener());
        init();
        initListener();
        isShowOkBt();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    private class PreviewListener implements View.OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size() > 0) {
                intent.putExtra("position", "2");
                intent.setClass(ShowAllPhoto.this, GalleryActivity.class);
                startActivity(intent);
            }
        }

    }
    public class MyBroadcastReceiver  extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            gridImageAdapter.notifyDataSetChanged();
            Toast.makeText(context, "showallphoto进来了", Toast.LENGTH_SHORT).show();
        }
    }

    private class BackListener implements View.OnClickListener {// 返回按钮监听
        Intent intent;

        public BackListener(Intent intent) {
            this.intent = intent;
        }

        public void onClick(View v) {
            intent.setClass(ShowAllPhoto.this, ImageFile.class);
            startActivity(intent);
        }

    }

    private class CancelListener implements View.OnClickListener {// 取消按钮的监听
        public void onClick(View v) {
            //清空选择的图片
            Bimp.tempSelectBitmap.clear();
            finish();
//            intent.setClass(mContext, ReleasedUcircleActivity.class);
//            startActivity(intent);
        }
    }

    private void init() {
        Intent intent1 = new Intent("com.example.broadcasttest.MY_BROADCAST");
        sendBroadcast(intent1);
        MyBroadcastReceiver myBroadcastReceiver1=new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        IntentFilter filter1 = new IntentFilter("com.example.broadcasttest.MY_BROADCAST");
        registerReceiver(broadcastReceiver, filter);
        registerReceiver(myBroadcastReceiver1, filter1);
        progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
                Bimp.tempSelectBitmap);
        gridView.setAdapter((ListAdapter) gridImageAdapter);
        okButton = (Button) findViewById(R.id.showallphoto_ok_button);
    }

    private void initListener() {

        gridImageAdapter
                .setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,
                                            Button button) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num&&isChecked) {
                            button.setVisibility(View.GONE);
                            toggleButton.setChecked(false);
                            Toast.makeText(ShowAllPhoto.this, R.string.only_choose_num, Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        if (isChecked) {
                            button.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            okButton.setText(R.string.finish+"(" + Bimp.tempSelectBitmap.size()
                                    + "/"+ PublicWay.num+")");
                        } else {
                            button.setVisibility(View.GONE);
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            okButton.setText(R.string.finish+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
                        }
                        isShowOkBt();
                    }
                });

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                okButton.setClickable(false);
//				if (PublicWay.photoService != null) {
//					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
//					Bimp.tempSelectBitmap.clear();
//					PublicWay.photoService.onActivityResult(0, -2,
//							intent);
//				}
                intent.setClass(mContext, ReleasedUcircleActivity.class);
                startActivity(intent);
                // Intent intent = new Intent();
                // Bundle bundle = new Bundle();
                // bundle.putStringArrayList("selectedDataList",
                // selectedDataList);
                // intent.putExtras(bundle);
                // intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
                // startActivity(intent);
                finish();

            }
        });

    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText(R.string.finish+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
            preview.setPressed(true);
            okButton.setPressed(true);
            preview.setClickable(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
            preview.setTextColor(Color.WHITE);
        } else {
            okButton.setText(R.string.finish+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
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

            intent.setClass(this, ImageFile.class);
            startActivity(intent);this.finish();
        }

        return true;

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        isShowOkBt();
        super.onRestart();
    }




}