package ecnu.uleda.view_controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.znq.zbarcode.CaptureActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.AddOptions;
import ecnu.uleda.tool.UPublicTool;
import kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy;

import static android.app.Activity.RESULT_OK;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class UserInfoFragment extends Fragment
        implements View.OnClickListener {
    public static final int CHOOSE_PHOTO = 2;
    private static final int REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 26;
    final  HashMap<String,String> map = new HashMap<String,String>();
    private ImageButton setting;
    private TextView mMyInfo;
    private TextView mMyMoneyBag;
    private TextView mMyQRCode;
    private TextView mMyAskServer;
    private CircleImageView mCircleImageView;
    private ImageButton add;
    private PopupWindow mPopupWindow;
    private TextView userId;
    private Uri imgUri;    //用来引用拍照存盘的 Uri 对象
    private ImageView imv;
    private LinearLayout T1;
    private LinearLayout T2;
    private LinearLayout T3;
    private LinearLayout T4;
    private LinearLayout T5;
    private final int REQUEST_CODE = 1;

    private boolean isHasSurface = false;
    private boolean isOpenCamera = false;
    ImageView p1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                putInformation();
            } else {
//                tryGetUserInfo();
            }
        }
    };
    public String[] options = {"选项1", "选项2", "选项3", "选项4", "选项5"};
    private List<AddOptions> OptionList;
    private OptionListAdapter adapter;
    private ListView OptionListview;

    private UserInfo mUserInfo;
    private UserOperatorController mUserOperatorController;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {
        View v = inflater.inflate(R.layout.user_info_fragment, parent, false);
        setting = (ImageButton) v.findViewById(R.id.setting);
        mMyInfo = (TextView) v.findViewById(R.id.my_info);
        mMyMoneyBag = (TextView) v.findViewById(R.id.my_money_bag);
        mMyQRCode = (TextView) v.findViewById(R.id.my_qr_code);
        mMyAskServer = (TextView) v.findViewById(R.id.ask_server);
        mCircleImageView = (CircleImageView) v.findViewById(R.id.icon);

        add = (ImageButton) v.findViewById(R.id.add);
        userId = (TextView) v.findViewById(R.id.id);
        T1 = (LinearLayout) v.findViewById(R.id.T1);
        T2 = (LinearLayout) v.findViewById(R.id.T2);
        T3 = (LinearLayout) v.findViewById(R.id.T3);
        T4 = (LinearLayout) v.findViewById(R.id.T4);
        T5 = (LinearLayout) v.findViewById(R.id.T5);
        p1 = (ImageView) v.findViewById(R.id.icon);


        setting.setOnClickListener(this);
        mMyInfo.setOnClickListener(this);
        mMyMoneyBag.setOnClickListener(this);
        mMyQRCode.setOnClickListener(this);
        mMyAskServer.setOnClickListener(this);
        mCircleImageView.setOnClickListener(this);
        add.setOnClickListener(this);
        T1.setOnClickListener(this);
        T2.setOnClickListener(this);
        T3.setOnClickListener(this);
        T4.setOnClickListener(this);
        T5.setOnClickListener(this);

        mUserOperatorController = UserOperatorController.getInstance();
        new Thread() {
            @Override
            public void run() {
                try {
                    if (mUserOperatorController.getIsLogined()) {
                        mUserInfo = UserOperatorController.getInstance().getMyInfo();
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                } catch (UServerAccessException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = e;
                    mHandler.sendMessage(message);
                }
            }
        }.start();

        return v;

    }

    private void tryGetUserInfo() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mUserInfo = UserOperatorController.getInstance().getMyInfo();
                    if (mUserOperatorController.getIsLogined()) {
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                } catch (UServerAccessException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = e;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void putInformation() {
        //TODO:将用户信息显示在屏幕上
        userId.setText(mUserInfo.getRealName());
        Glide.with(getContext())
                .load("http://118.89.156.167/uploads/avatars/"+mUserInfo.getAvatar())
                .into(mCircleImageView);
    }

    @Override
    public void onClick(View v) {
        int data;
        Intent intent;
        switch (v.getId()) {
            case R.id.setting:
                Intent it = new Intent(getActivity().getBaseContext(), SettingActivity.class);
                startActivity(it);
                break;
            case R.id.my_info:
                UserOperatorController uoc = UserOperatorController.getInstance();
//                if(!uoc.getIsLogined()){
//                    Toast.makeText(this.getActivity(),"请先登陆！",Toast.LENGTH_SHORT).show();
//                    break;
//                }
                intent = new Intent(getActivity().getBaseContext(), SingleUserInfoActivity.class);
                intent.putExtra("userid", UserOperatorController.getInstance().getId());
                startActivity(intent);
                break;
            case R.id.my_money_bag:
                startActivity(new Intent(getActivity().getBaseContext(), MyWalletActivity.class));
                break;
            case R.id.my_qr_code:
                startActivity(new Intent(getActivity().getBaseContext(), MyQrActivity.class));
                break;
            case R.id.icon:
                showPopMenu();
                break;
            case R.id.btn_open_camera:
                if (Build.VERSION.SDK_INT >= 24 && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            0);
                } else {
                    takePhotoToAvatar();
                }
                break;
            case R.id.btn_choose_img:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    Log.d("King1", "h");
                }
                Log.d("4", "h");
                openAlbum();
                break;
            case R.id.btn_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.add:
                showaddPopMenu();
                break;
            case R.id.T1:
                data = 1;
                Intent i = new Intent(getActivity().getBaseContext(), MyTaskInFo.class);
                i.putExtra("data", String.valueOf(data));
                startActivity(i);
                break;
            case R.id.T2:
                data = 2;
                intent= new Intent(getActivity().getBaseContext(), MyTaskInFo.class);
                intent.putExtra("data", String.valueOf(data));
                startActivity(intent);
                break;
            case R.id.T3:
                data = 3;
                intent = new Intent(getActivity().getBaseContext(), MyTaskInFo.class);
                intent.putExtra("data", String.valueOf(data));
                startActivity(intent);
                break;
            case R.id.T4:
                data = 4;
                intent = new Intent(getActivity().getBaseContext(), MyTaskInFo.class);
                intent.putExtra("data", String.valueOf(data));
                startActivity(intent);
                break;
            case R.id.T5:
                data = 5;
                intent = new Intent(getActivity().getBaseContext(), MyTaskInFo.class);
                intent.putExtra("data", String.valueOf(data));
                startActivity(intent);
                break;
            case R.id.ask_server:
                intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + UPublicTool.SERVICE_PHONE_NUMBER));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void takePhotoToAvatar() {
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imgUri = Uri.fromFile(outputImage);
        } else {
            imgUri = FileProvider.getUriForFile(getActivity(), "com.example.cameraalbumtest.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        Log.d("2", "h");
        this.startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
        Log.d("3", "h");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else {
                    Toast.makeText(getContext(),"抱歉,你的相机权限没有打开,无法正常服务",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imgUri));
                        Drawable drawable = new BitmapDrawable(toRoundBitmap(bitmap));
                        mCircleImageView.setImageDrawable(drawable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CHOOSE_PHOTO:
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                    break;
                case REQUEST_CODE:
                    String result = data.getStringExtra(CaptureActivity.EXTRA_STRING);
                    Toast.makeText(UserInfoFragment.this.getActivity(), result + "", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片

    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mCircleImageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        // 取最短边做边长
        if (width < height) {
            r = width;
        } else {
            r = height;
        }
        // 构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);
        Paint p = new Paint();
        // 设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        // 通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        // 且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, p);
        // 设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }


    Uri convertUri(Uri uri) {
        if (uri.toString().substring(0, 7).equals("content")) {  //如果是以 "content" 开头
            String[] colName = {MediaStore.MediaColumns.DATA};    //声明要查询的字段
            Cursor cursor = getActivity().getContentResolver().query(uri, colName,  //以 imgUri 进行查询
                    null, null, null);
            cursor.moveToFirst();      //移到查询结果的第一个记录
            uri = Uri.parse("file://" + cursor.getString(0)); //将路径转为 Uri
            cursor.close();     //关闭查询结果
        }
        return uri;   //返回 Uri 对象
    }

    void showImg() {
        int iw, ih, vw, vh;
        boolean needRotate;  //用来存储是否需要旋转

        BitmapFactory.Options option = new BitmapFactory.Options(); //创建选项对象
        option.inJustDecodeBounds = true;      //设置选项：只读取图像文件信息而不加载图像文件
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //读取图像文件信息存入 Option 中
        iw = option.outWidth;   //从 option 中读出图像宽度
        ih = option.outHeight;  //从 option 中读出图像高度
        vw = imv.getWidth();    //获取 ImageView 的宽度
        vh = imv.getHeight();   //获取 ImageView 的高度

        int scaleFactor;
        if (iw < ih) {    //如果图片的宽度小于高度
            needRotate = false;                 //不需要旋转
            scaleFactor = Math.min(iw / vw, ih / vh);   // 计算缩小比率
        } else {
            needRotate = true;                  //需要旋转
            scaleFactor = Math.min(iw / vh, ih / vw);   // 将 ImageView 的宽、高互换来计算缩小比率
        }

        option.inJustDecodeBounds = false;  //关闭只加载图像文件信息的选项
        option.inSampleSize = scaleFactor;  //设置缩小比例, 例如 2 则长宽都将缩小为原来的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //载入图像

        if (needRotate) { //如果需要旋转
            Matrix matrix = new Matrix();  //创建 Matrix 对象
            matrix.postRotate(90);         //设置旋转角度
            bmp = Bitmap.createBitmap(bmp, //用原来的 Bitmap 产生一个新的 Bitmap
                    0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        mCircleImageView.setImageBitmap(bmp);
    }


    private void showPopMenu() {
        View view = View.inflate(getActivity().getApplicationContext(), R.layout.activity_bottom_dialog, null);

        Button btn_open_camera = (Button) view.findViewById(R.id.btn_open_camera);
        Button btn_choose_img = (Button) view.findViewById(R.id.btn_choose_img);
        Button bt_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_open_camera.setOnClickListener(this);
        btn_choose_img.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        view.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.push_bottom_in));

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(getActivity());
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }

        mPopupWindow.setContentView(view);
        mPopupWindow.showAtLocation(mCircleImageView, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
    }


    private void showaddPopMenu() {
        View popupView = View.inflate(getActivity().getApplicationContext(), R.layout.activity_popupwindow, null);

        OptionList = createOptions();//初始化消息列表
        adapter = new OptionListAdapter(this.getActivity().getApplicationContext()
                , R.layout.options_list_item, OptionList);

        ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
        lsvMore.setAdapter(adapter);
        lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserInfoFragment.this.getActivity(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        // 创建PopupWindow对象，指定宽度和高度
        PopupWindow window = new PopupWindow(popupView, 270, WRAP_CONTENT);
        // 设置动画
        window.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        // 设置可以获取焦点
        window.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // 更新popupwindow的状态
        window.update();
        // 以下拉的方式显示，并且可以设置显示的位置
        window.showAsDropDown(add, 0, 20);
    }


    private List<AddOptions> createOptions() {
        List<AddOptions> AList = new ArrayList<>();

        AddOptions o1 = new AddOptions("加好友", R.drawable.o_addf);
        AList.add(o1);
        AddOptions o2 = new AddOptions("扫一扫", R.drawable.o_scan);
        AList.add(o2);
        return AList;

    }

}





