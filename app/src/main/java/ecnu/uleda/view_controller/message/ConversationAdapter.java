package ecnu.uleda.view_controller.message;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.Conversation;
import ecnu.uleda.model.Friend;

/**
 * Created by zhaoning on 2017/10/17.
 */

public class ConversationAdapter extends ArrayAdapter<Object> {

    private static final String ACCEPT_INVITE_SUCCESS = "success";
    private static final String ACCEPT_INVITE_FAILED = "failed";
    private int resourceId1;
    private int resourceId2;

    private static final String TAG = "ConversationAdapter";

    private OnAddedFriendListener mOnAddedFriendListener = null;

    public interface OnAddedFriendListener {
        void onAdded(Invites i);
    }
    public ConversationAdapter (Context context, int textViewResourceId1,int textViewResourceId2, List<Object> objects)
    {
        super(context,textViewResourceId1,objects);
        resourceId1 = textViewResourceId1;
        resourceId2 = textViewResourceId2;
    }
    public void setOnAddedFriendListener(OnAddedFriendListener listener){
        mOnAddedFriendListener = listener;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Object obj = getItem(position);
        View view;

        if(obj instanceof Conversation)
        {
            Conversation conversation = (Conversation)obj;

            ConversationAdapter.ViewHolder1 viewHolder;

            if(convertView==null || !(convertView.getTag() instanceof ViewHolder1))
            {
                view = LayoutInflater.from(getContext()).inflate(resourceId1,parent,false);
                viewHolder = new ConversationAdapter.ViewHolder1();
                viewHolder.conversationImage = (CircleImageView)view.findViewById(R.id.conversation_image);
                viewHolder.conversationName = (TextView)view.findViewById(R.id.conversation_name);
                viewHolder.contactsContent = (TextView)view.findViewById(R.id.contacts_content);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ConversationAdapter.ViewHolder1)view.getTag();
            }


            Glide.with(getContext())
                    .load(conversation.getImageUrl())
                    .into(viewHolder.conversationImage);
            Log.e(TAG, "Conversation getImageUrl" +conversation.getImageUrl());
//        viewHolder.Image.setImageURI(mURI);
            viewHolder.conversationName.setText(conversation.getConversationName());
            viewHolder.contactsContent.setText(conversation.getContent());

        }
        else
        {
            final ConversationAdapter.ViewHolder2 viewHolder;

            final Invites invites = (Invites)obj;

            if(convertView==null || !(convertView.getTag() instanceof ViewHolder2))
            {
                view = LayoutInflater.from(getContext()).inflate(resourceId2,parent,false);
                viewHolder = new ConversationAdapter.ViewHolder2();
                viewHolder.invitesImage = (CircleImageView)view.findViewById(R.id.invites_image);
                viewHolder.invitesName = (TextView)view.findViewById(R.id.invites_name);
                viewHolder.invitesContent = (TextView)view.findViewById(R.id.invites_content);
                viewHolder.verifyButton = (Button)view.findViewById(R.id.button_invites);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ConversationAdapter.ViewHolder2)view.getTag();
            }


            viewHolder.verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View iv = v;
                    new Thread(){
                        public void run(){
                            //TODO:
                            try{
                                if(ACCEPT_INVITE_SUCCESS.equals(onAcceptInvite(invites.getInvitesId())))
                                {
//                                    Toast.makeText(getContext(),"接收好友请求成功！",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
//                                    Toast.makeText(getContext(),"接收好友请求失败！",Toast.LENGTH_SHORT).show();
                                }
                            }catch (UServerAccessException e)
                            {
                                e.printStackTrace();
                                System.exit(1);
                            }

                            iv.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mOnAddedFriendListener !=null){
                                        mOnAddedFriendListener.onAdded(invites);
//                                        viewHolder.verifyButton.setBackgroundColor(Color.GRAY);
//                                        viewHolder.verifyButton.setEnabled(false);
//                                        Toast.makeText(getContext(),"接收好友请求成功！",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }.start();
                }
            });
            Glide.with(getContext())
                    .load(invites.getImageUrl())
                    .into(viewHolder.invitesImage);
            Log.e(TAG, "Invites getImageUrl" +invites.getImageUrl());
//        viewHolder.Image.setImageURI(mURI);
            viewHolder.invitesName.setText(invites.getInvitesName());
//            viewHolder.invitesContent.setText(invites.getContent());

        }

        return view;
    }

    class ViewHolder1
    {
        CircleImageView conversationImage;
        TextView conversationName;
        TextView contactsContent;
    }
    class ViewHolder2
    {
        CircleImageView invitesImage;
        TextView invitesName;
        TextView invitesContent;
        Button verifyButton;
    }

    String onAcceptInvite (String inviteId) throws UServerAccessException
    {
        UserOperatorController user = UserOperatorController.getInstance();
        String id = user.getId();
        id = UrlEncode(id);
        String passport = user.getPassport();
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response=client
                .withService("User.AcceptInvite")//接口的名称
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("fromID",inviteId)
                .request();
        if(response.getRet()==200) {
            try{
                String data=new String(response.getData());
                return ACCEPT_INVITE_SUCCESS;// "success"
            }catch (Exception e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }
        else
            return ACCEPT_INVITE_FAILED;
    }

    private static String UrlEncode(String str)throws UServerAccessException{
        try{
            if(str==null)return null;
            return URLEncoder.encode(str,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("http://118.89.156.167/mobile/");
    }



}
