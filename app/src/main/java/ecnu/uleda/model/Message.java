package ecnu.uleda.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.function_module.App;
import ecnu.uleda.view_controller.message.ChatAdapter;

/**
 * Created by zhaoning on 2017/9/15.
 */

public class Message {
    protected final String TAG = "Message";

    TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;

    public Message(TIMMessage message)
    {
        this.message = message;
    }

    public TIMMessage getMessage() {
        return message;
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context)
    {
        clearView(viewHolder);
        boolean hasText = false;
        TextView tv = new TextView(App.getContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(App.getContext().getResources().getColor(isSelf() ? android.R.color.white : android.R.color.black));
        List<TIMElem> elems = new ArrayList<>();
        for (int i = 0; i < message.getElementCount(); ++i){
            elems.add(message.getElement(i));
            if (message.getElement(i).getType() == TIMElemType.Text){
                hasText = true;
            }
        }
        SpannableStringBuilder stringBuilder = getString(elems, context);
        if (!hasText){
            stringBuilder.insert(0," ");
        }
        tv.setText(stringBuilder);
        getBubbleView(viewHolder).addView(tv);
        showStatus(viewHolder);
    }

    protected void clearView(ChatAdapter.ViewHolder viewHolder) {
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);
    }


    private static int getNumLength(int n){
        return String.valueOf(n).length();
    }

    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i<elems.size(); ++i){
            switch (elems.get(i).getType()){
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    try{
                        AssetManager am = context.getAssets();
                        InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
                        if (is == null) continue;
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        matrix.postScale(2, 2);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                        stringBuilder.append(String.valueOf(faceElem.getIndex()));
                        stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        is.close();
                    }catch (IOException e){

                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        return stringBuilder;
    }

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(ChatAdapter.ViewHolder viewHolder){
        viewHolder.systemMessage.setVisibility(hasTime? View.VISIBLE:View.GONE);
//        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));
        showDesc(viewHolder);
        if (message.isSelf()){
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            return viewHolder.rightMessage;
        }else{
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
//            //群聊显示名称，群名片>个人昵称>identify
//            if (message.getConversation().getType() == TIMConversationType.Group){
//                viewHolder.sender.setVisibility(View.VISIBLE);
//                String name = "";
//                if (message.getSenderGroupMemberProfile()!=null) name = message.getSenderGroupMemberProfile().getNameCard();
//                if (name.equals("")&&message.getSenderProfile()!=null) name = message.getSenderProfile().getNickName();
//                if (name.equals("")) name = message.getSender();
//                viewHolder.sender.setText(name);
//            }else{
                viewHolder.sender.setVisibility(View.GONE);
//            }
            return viewHolder.leftMessage;
        }

    }


    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder){
        switch (message.status()){
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断是否是自己发的
     *
     */
    public boolean isSelf(){
        return message.isSelf();
    }


    private void showDesc(ChatAdapter.ViewHolder viewHolder){

        if (desc == null || desc.equals("")){
            viewHolder.rightDesc.setVisibility(View.GONE);
        }else{
            viewHolder.rightDesc.setVisibility(View.VISIBLE);
            viewHolder.rightDesc.setText(desc);
        }
    }

    /**
     * 是否需要显示时间获取
     *
     */
    public boolean getHasTime() {
        return hasTime;
    }


    /**
     * 是否需要显示时间设置
     *
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message){
        if (message == null){
            hasTime = true;
            return;
        }
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }
}
