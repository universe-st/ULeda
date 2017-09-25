package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import ecnu.uleda.R;

/**
 * Created by 63516 on 2017/9/20.
 */

public class ImageFile extends Activity {
    private FolderAdapter folderAdapter;
    private Button choose_bt_cancel;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_image_file);
        PublicWay.activityList.add(this);
        mContext = this;
        choose_bt_cancel = (Button) findViewById(R.id.choose_cancel);
        choose_bt_cancel.setOnClickListener(new CancelListener());
        GridView gridView = (GridView) findViewById(R.id.fileGridView);
        TextView textView = (TextView) findViewById(R.id.headerTitle);
        textView.setText(R.string.photo);
        folderAdapter = new FolderAdapter(this);
        gridView.setAdapter(folderAdapter);
    }

    private class CancelListener implements View.OnClickListener {// 取消按钮的监听
        public void onClick(View v) {
            //清空选择的图片
            Bimp.tempSelectBitmap.clear();
            finish();
//            Intent intent = new Intent();
//            intent.setClass(mContext, ReleasedUcircleActivity.class);
//            startActivity(intent);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Intent intent = new Intent();
        intent.setClass(mContext,ReleasedUcircleActivity.class);
        startActivity(intent);this.finish();

        return true;
    }

}