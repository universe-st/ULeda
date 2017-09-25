package ecnu.uleda.view_controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ecnu.uleda.R;

public class ReleasedUcircleActivity extends Activity implements AdapterView.OnItemClickListener{
    private TextView mBack;
    private TextView mRealsed;
    private ImageView mPhoto;

    public static final int TAKE_PHOTO=6;
    public static final int CHOOSE_PHOTO=7;
    private ImageView picture;
    private Uri imageUri;
    public static Bitmap bimap ;
    private GridAdapter adapter;
    private static final int TAKE_PHOTO_REQUEST_CODE=1;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private View parentView;
    private GridView gridView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_released_ucircle);
        parentView = getLayoutInflater().inflate(R.layout.activity_released_ucircle, null);
        setContentView(parentView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        init();
    }
    public void init()
    {
        mBack = (TextView) findViewById(R.id.back);
        mRealsed=(TextView)findViewById(R.id.ucircle_release);
//        mRealsed.setOnClickListener((View.OnClickListener) this);
//        mPhoto=(ImageView)findViewById(R.id.addPhoto);
//        mPhoto.setOnClickListener(this);
        gridView = (GridView) findViewById(R.id.pic_gridView);


        pop = new PopupWindow(ReleasedUcircleActivity.this);
        View view = getLayoutInflater().inflate(R.layout.activity_bottom_dialog, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.btn_open_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.btn_choose_img);
        Button bt3 = (Button) view
                .findViewById(R.id.btn_cancel);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                Bimp.max = 0;
                finish();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ReleasedUcircleActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ReleasedUcircleActivity.this,new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }else{
                            Intent intent1 = new Intent("data.broadcast.action");
                            sendBroadcast(intent1);
                            Intent intent2 = new Intent(ReleasedUcircleActivity.this, AlbumActivity.class);
                            startActivity(intent2);
                }
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });



        mRealsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(),"Submit this task",Toast.LENGTH_SHORT).show();
                ProgressDialog progressDialog = new ProgressDialog(ReleasedUcircleActivity.this);
                progressDialog.setTitle("正在提交");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(true);
                progressDialog.show();
            }
        });


        //生成动态数组，并且转入数据

        adapter = new GridAdapter(this);
        adapter.update();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        //添加消息处理
        //   gridview.setOnItemClickListener(new ItemClickListener());
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.add_pic);
        PublicWay.activityList.add(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position==Bimp.tempSelectBitmap.size()){
            ll_popup.startAnimation(AnimationUtils.loadAnimation(ReleasedUcircleActivity.this,R.anim.activity_translate_in));
            pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
        else{
            Intent intent = new Intent(ReleasedUcircleActivity.this,
                    GalleryActivity.class);
            intent.putExtra("position", "1");
            intent.putExtra("ID", position);
            startActivity(intent);
        }
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Bimp.tempSelectBitmap.clear();
                Bimp.max = 0;
                finish();
                break;
            case R.id.ucircle_release:

                break;
//            case R.id.addPhoto:
//
//
//                break;



        }
    }
    private void photo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST_CODE);
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                    adapter.update();
                }
                break;
        }
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }
}


