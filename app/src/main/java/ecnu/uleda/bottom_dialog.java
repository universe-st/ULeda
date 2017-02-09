package ecnu.uleda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class bottom_dialog extends AppCompatActivity
implements View.OnClickListener{

    Button btn_open_camera;
    Button btn_choose_img ;
    Button bt_cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_dialog);

         btn_open_camera = (Button) findViewById(R.id.btn_open_camera);
         btn_choose_img = (Button)findViewById(R.id.btn_choose_img);
         bt_cancle = (Button)findViewById(R.id.btn_cancel);

        btn_open_camera.setOnClickListener(this);
        btn_choose_img.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_camera) {
            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(it, 100);
        }
    }


    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode== Activity.RESULT_OK&&requestCode==100){
            Bundle extras=data.getExtras();
            Bitmap bmp=(Bitmap)extras.get("data");

        }
        else {
            Toast.makeText(this,"没有拍到照片",Toast.LENGTH_LONG).show();
        }
    }
}
