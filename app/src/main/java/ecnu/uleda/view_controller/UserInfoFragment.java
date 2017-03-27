package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.AddOptions;

import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class UserInfoFragment extends Fragment
implements View.OnClickListener{

    ImageButton setting;
    LinearLayout mMyInfo;
    LinearLayout mMyMoneyBag;
    LinearLayout mMyQRCode;
    CircleImageView icon;
    ImageButton add;
    PopupWindow mPopupWindow;
    TextView userId;
    Uri imgUri ;    //用来引用拍照存盘的 Uri 对象
    ImageView imv;
    LinearLayout T1;
    LinearLayout T2;
    LinearLayout T3;
    LinearLayout T4;
    LinearLayout T5;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                putInformation();
            }else{
                tryGetUserInfo();
            }
        }
    };
    public String[] options = {"选项1", "选项2", "选项3", "选项4", "选项5"};
    private List<AddOptions> OptionList ;
    private OptionListAdapter adapter;
    private ListView OptionListview;

    private UserInfo mUserInfo;
    private UserOperatorController mUserOperatorController;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b){
        View v=inflater.inflate(R.layout.user_info_fragment,parent,false);
        setting =(ImageButton)v.findViewById(R.id.setting);
        mMyInfo=(LinearLayout)v.findViewById(R.id.my_info);
        mMyMoneyBag=(LinearLayout)v.findViewById(R.id.my_money_bag);
        mMyQRCode=(LinearLayout)v.findViewById(R.id.my_qr_code);
        icon=(CircleImageView)v.findViewById(R.id.icon);
        add=(ImageButton)v.findViewById(R.id.add);
        userId=(TextView)v.findViewById(R.id.id) ;
        T1=(LinearLayout)v.findViewById(R.id.T1);
        T2=(LinearLayout)v.findViewById(R.id.T2);
        T3=(LinearLayout)v.findViewById(R.id.T3);
        T4=(LinearLayout)v.findViewById(R.id.T4);
        T5=(LinearLayout)v.findViewById(R.id.T5);



        setting.setOnClickListener(this);
        mMyInfo.setOnClickListener(this);
        mMyMoneyBag.setOnClickListener(this);
        mMyQRCode.setOnClickListener(this);
        icon.setOnClickListener(this);
        add.setOnClickListener(this);
        T1.setOnClickListener(this);
        T2.setOnClickListener(this);
        T3.setOnClickListener(this);
        T4.setOnClickListener(this);
        T5.setOnClickListener(this);

        mUserOperatorController=UserOperatorController.getInstance();
        new Thread() {
            @Override
            public void run() {
                try {
                    if(mUserOperatorController.getIsLogined()) {
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
        }.start();}

    private void putInformation(){
        //TODO:将用户信息显示在屏幕上
        userId.setText(mUserInfo.getRealName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting:{
                Intent it = new Intent(getActivity().getBaseContext(), SettingActivity.class);
                startActivity(it);
                break;
            }
            case R.id.my_info:{
                UserOperatorController uoc=UserOperatorController.getInstance();
//                if(!uoc.getIsLogined()){
//                    Toast.makeText(this.getActivity(),"请先登陆！",Toast.LENGTH_SHORT).show();
//                    break;
//                }
                Intent it = new Intent(getActivity().getBaseContext(),SingleUserInfoActivity.class);
                it.putExtra("userid",UserOperatorController.getInstance().getId());
                startActivity(it);
                break;
            }
            case R.id.my_money_bag:{
                Intent it = new Intent(getActivity().getBaseContext(), MyWalletActivity.class);
                startActivity(it);
                break;
            }
            case R.id.my_qr_code:{
                Intent it = new Intent(getActivity().getBaseContext(), MyQrActivity.class);
                startActivity(it);
                break;
            }
            case R.id.icon:
                showPopMenu();
                break;
            case R.id.btn_open_camera: {

//                String dir = Environment.getExternalStoragePublicDirectory(       //获取系统的公用图像文件路径
//                        Environment.DIRECTORY_PICTURES).toString();
//                String fname = "p" + System.currentTimeMillis() + ".jpg";         //利用当前时间组合出一个不会重复的文件名
//                imgUri = Uri.parse("file://" + dir + "/" + fname);                //按照前面的路径和文件名创建 Uri 对象

                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);                     //将 uri 加到拍照 Intent 的额外数据中
                startActivityForResult(it, 100);
                break;
            }
            case R.id.btn_choose_img: {
                Intent it = new Intent(Intent.ACTION_GET_CONTENT);
                it.setType("image/*");
                startActivityForResult(it, 101);
                break;
            }
            case R.id.btn_cancel:
                mPopupWindow.dismiss();
                break;
            default:
                break;
            case R.id.add:
            showaddPopMenu();
                break;
            case R.id.T1:{
                int data=1;
                Intent i = new Intent(getActivity().getBaseContext(),MyTaskInFo.class);
                i.putExtra("data",String.valueOf(data));
                startActivity(i);
                break;
            }
            case R.id.T2:{
                int data=2;
                Intent i = new Intent(getActivity().getBaseContext(),MyTaskInFo.class);
                i.putExtra("data",String.valueOf(data));
                startActivity(i);
                break;
            }
            case R.id.T3:{
                int data=3;
                Intent i = new Intent(getActivity().getBaseContext(),MyTaskInFo.class);
                i.putExtra("data",String.valueOf(data));
                startActivity(i);
                break;
            }
            case R.id.T4:{
                int data=4;
                Intent i = new Intent(getActivity().getBaseContext(),MyTaskInFo.class);
                i.putExtra("data",String.valueOf(data));
                startActivity(i);
                break;
            }
            case R.id.T5:{
                int data=5;
                Intent i = new Intent(getActivity().getBaseContext(),MyTaskInFo.class);
                i.putExtra("data",String.valueOf(data));
                startActivity(i);
                break;
            }
        }
    }


    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {   //要求的意图成功了
            switch(requestCode) {
                case 100: //拍照
                    Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);//设为系统共享媒体文件
                    getActivity().sendBroadcast(it);
                    break;
                case 101: //选取相片
                    imgUri = convertUri(data.getData());  //获取选取相片的 Uri 并进行 Uri 格式转换
                    break;
            }
            showImg();  //显示 imgUri 所指明的相片
        }
        else {
            Toast.makeText(getActivity(), "没有拍到照片", Toast.LENGTH_LONG).show();
        }
    }


    Uri convertUri(Uri uri) {
        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以 "content" 开头
            String[] colName = { MediaStore.MediaColumns.DATA };    //声明要查询的字段
            Cursor cursor =getActivity(). getContentResolver().query(uri, colName,  //以 imgUri 进行查询
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
        if(iw<ih) {    //如果图片的宽度小于高度
            needRotate = false;                 //不需要旋转
            scaleFactor = Math.min(iw/vw, ih/vh);   // 计算缩小比率
        }
        else {
            needRotate = true;                  //需要旋转
            scaleFactor = Math.min(iw/vh, ih/vw);   // 将 ImageView 的宽、高互换来计算缩小比率
        }

        option.inJustDecodeBounds = false;  //关闭只加载图像文件信息的选项
        option.inSampleSize = scaleFactor;  //设置缩小比例, 例如 2 则长宽都将缩小为原来的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //载入图像

        if(needRotate) { //如果需要旋转
            Matrix matrix = new Matrix();  //创建 Matrix 对象
            matrix.postRotate(90);         //设置旋转角度
            bmp = Bitmap.createBitmap(bmp , //用原来的 Bitmap 产生一个新的 Bitmap
                    0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        icon.setImageBitmap(bmp);
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

        if(mPopupWindow==null){
            mPopupWindow = new PopupWindow(getActivity());
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }

        mPopupWindow.setContentView(view);
        mPopupWindow.showAtLocation(icon, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
    }


    private void showaddPopMenu() {
        View popupView = View.inflate(getActivity().getApplicationContext(), R.layout.activity_popupwindow, null);

        OptionList=createOptions();//初始化消息列表
        adapter=new OptionListAdapter(this.getActivity().getApplicationContext()
                ,R.layout.options_list_item,OptionList);

        ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
        lsvMore.setAdapter(adapter);

        // 创建PopupWindow对象，指定宽度和高度
        PopupWindow window = new PopupWindow(popupView,270,WRAP_CONTENT);
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


    private List<AddOptions>createOptions(){
        List<AddOptions> AList = new ArrayList<>();

        AddOptions o1=new AddOptions("加好友",R.drawable.o_addf);
        AList.add(o1);
        AddOptions o2=new AddOptions("扫一扫",R.drawable.o_scan);
        AList.add(o2);
        AddOptions o3=new AddOptions("付款",R.drawable.o_pay);
        AList.add(o3);
        AddOptions o4=new AddOptions("拍摄",R.drawable.o_camera);
        AList.add(o4);

        return AList;

    }


}





