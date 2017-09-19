package ecnu.uleda.function_module;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.imsdk.protocol.im_common;

import net.phalapi.sdk.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import ecnu.uleda.BuildConfig;

import ecnu.uleda.tool.AESUtils;
import ecnu.uleda.tool.MD5Utils;

import ecnu.uleda.model.UActivity;

import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.exception.UServerAccessException;


public class ServerAccessApi {
    private static final int SET_TIME_OUT = 9999;

    public static String getMainKey()throws UServerAccessException {
        PhalApiClient client=createClient();
        PhalApiClientResponse response=client
                .withService("Default.GetMainKey")
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("mainKey");
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }
        throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
    }

    public static int Register(@NonNull String username,@NonNull String password,@NonNull String pcode,
    @NonNull String phone)throws UServerAccessException
    {
        String aes_key = MD5Utils.MD5(getMainKey()).substring(0,16);
        String aes = AESUtils.encrypt(password,aes_key);
        String md5 = MD5Utils.MD5(aes);
        username = UrlEncode(username);
        md5 = UrlEncode(md5);
        pcode = UrlEncode(pcode);
        phone = UrlEncode(phone);
        PhalApiClient client=createClient();
        PhalApiClientResponse response = client
            .withService("User.Register")
            .withParams("username",username)
            .withParams("password",md5)
            .withParams("pcode",pcode)
            .withParams("phone",phone)
            .withTimeout(SET_TIME_OUT)
            .request();
        if(response.getRet() == 200)
        {
           return 200;
        }
        else
        {
            throw new UServerAccessException(response.getRet());
        }
    }
    public static JSONArray getUserTask(@NonNull int page,@NonNull int flag)throws UServerAccessException
    {
            UserOperatorController user = UserOperatorController.getInstance();
            String id = user.getId();
            id = UrlEncode(id);
            String passport = user.getPassport();
            passport = UrlEncode(passport);
            String Page = String.valueOf(page);
            Page = UrlEncode(Page);
            String Flag = String.valueOf(flag);
            Flag = UrlEncode(Flag);
        PhalApiClient client=createClient();
        PhalApiClientResponse response = client
                .withService("Task.GetUserTasks")
            .withParams("id",id)
            .withParams("passport",passport)
            .withParams("page",Page)
            .withParams("flag",Flag)
            .withTimeout(SET_TIME_OUT)
            .request();
        if(response.getRet() == 200)
        {
            try {
                JSONArray info = new JSONArray(response.getData());
                return  info;
            }catch (JSONException e)
            {
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }

        }
        else
        {
            throw new UServerAccessException(response.getRet());
        }
    }
    public static JSONArray getUCicleList() throws UServerAccessException
    {
        UserOperatorController user = UserOperatorController.getInstance();
        String Id = user.getId();
        String passport = user.getPassport();
        Id = UrlEncode(Id);
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response = client
                        .withService("UCircle.GetList")
                .withParams("id",Id)
                .withParams("passport",passport)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet() == 200)
        {
            try {
                JSONArray info = new JSONArray(response.getData());
                return  info;
            }catch (JSONException e)
            {
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }
        else
        {
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String getLoginToken(@NonNull String userName)throws UServerAccessException{
        //断言，保证传入参数的正确性，在DEBUG模式下才启用。
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( byteCount(userName)>=4 && byteCount(userName)<=25 );
            //当这个函数里表达式的值为false时，抛出断言异常，然后终止程序。
            //这么做是为了保证调用者进行了参数检查。参数的范围在文档里提到过。
        }
        userName=UrlEncode(userName);//对参数进行UrlEncode处理，才能POST出去
        //这个处理非常重要
        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("User.GetLoginToken")//接口的名称
                .withParams("username",userName)//插入一个参数对
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200) {
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("loginToken");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else {
            throw new UServerAccessException(response.getRet());
        }
    }


    //这个需要返回一个数据包，所以返回类型是JSONObject
    public static JSONObject getTaskPost(@NonNull String id,@NonNull String passport,@NonNull String postID) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetPost")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("postID",postID)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try {
                return new JSONObject(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }


    public static String cancelTask(@NonNull String id,@NonNull String passport,@NonNull String postID)throws UServerAccessException{

        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("Task.Cancel")//接口的名称
                .withParams("id",id)//插入一个参数对
                .withParams("passport",passport)//插入一个参数对
                .withParams("postID",postID)//插入一个参数对
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
            return response.getData();
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }



    public static String delComment(@NonNull String id,@NonNull String passport,@NonNull String commentID)throws UServerAccessException{

        id=UrlEncode(id);
        passport=UrlEncode(passport);
        commentID=UrlEncode(commentID);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("Task.DelComment")//接口的名称
                .withParams("id",id)//插入一个参数对
                .withParams("passport",passport)//插入一个参数对
                .withParams("commentID",commentID)//插入一个参数对
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
                try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("success");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }



    public static String editTask(@NonNull String id,@NonNull String passport,@NonNull String postID,String title,
                                  String tag,String description,String price,String path,
                                  String activeTime,String position)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            if(title!=null)
            UPublicTool.UAssert( byteCount(title)>=5 && byteCount(title)<=30 );
            if(tag!=null)
            UPublicTool.UAssert( tag.length()<=30 );
            if(description!=null)
            UPublicTool.UAssert( description.length()<=450);
            if(path!=null)
            UPublicTool.UAssert( path.length()<=400 );
        }

        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        title=UrlEncode(title);
        tag=UrlEncode(tag);
        description=UrlEncode(description);
        price=UrlEncode(price);
        path=UrlEncode(path);
        activeTime=UrlEncode(activeTime);
        position=UrlEncode(position);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("Task.Edit")//接口的名称
                .withParams("id",id)//插入一个参数对
                .withParams("passport",passport)
                .withParams("postID",postID)
                .withParams("title",title)
                .withParams("tag",tag)
                .withParams("description",description)
                .withParams("price",price)
                .withParams("path",path)
                .withParams("activetime",activeTime)
                .withParams("position",position)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
            return response.getData();
        }else{ 
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static JSONArray getUserTasks(@NonNull String id,@NonNull String passport,int page,int flag)throws UServerAccessException{
        id=UrlEncode(id);
        passport = UrlEncode(passport);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetUserTasks")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("page",String.valueOf(page))
                .withParams("flag",String.valueOf(flag))
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try {
                return new JSONArray(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String getComment(@NonNull String id,@NonNull String passport,@NonNull String postID,
                                        String start) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        start=UrlEncode(start);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetComment")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("postID",postID)
                .withParams("start",start)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            return response.getData();
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }

    public static String getTakers(@NonNull String id,@NonNull String passport,
                                   @NonNull String postID) throws UServerAccessException{
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetTakers")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("task_id",postID)
                .withTimeout(SET_TIME_OUT)
                .request();
//        Log.e("haha", "response: " + response.getData());
        if(response.getRet()==200) {
            return response.getData();
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }



    public static JSONArray getTaskList(@NonNull String id,@NonNull String passport,
            @NonNull String orderBy,@NonNull String start,
            @NonNull String num,@NonNull String tag,
            @NonNull String position) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        orderBy=UrlEncode(orderBy);
        start=UrlEncode(start);
        num=UrlEncode(num);
        tag=UrlEncode(tag);
        position=UrlEncode(position);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetList")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("orderBy",orderBy)
                .withParams("start",start)
                .withParams("num",num)
                .withParams("tag",tag)
                .withParams("position",position)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try {
                return new JSONArray(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }

    public static String postTask(@NonNull String id,@NonNull String passport,@NonNull String title,
                                    @NonNull String tag,String description,@NonNull String price,String path,
                                    @NonNull String activeTime,@NonNull String position)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( byteCount(title)>=5 && byteCount(title)<=30 );
            UPublicTool.UAssert( byteCount(title)<=30 );
            if(description!=null)
                UPublicTool.UAssert( byteCount(description)<=450);
            if(path!=null)
                UPublicTool.UAssert( byteCount(path)<=400 );
        }

        id=UrlEncode(id);
        passport=UrlEncode(passport);
        title=UrlEncode(title);
        tag=UrlEncode(tag);
        description=UrlEncode(description);
        price=UrlEncode(price);
        path=UrlEncode(path);
        activeTime=UrlEncode(activeTime);
        position=UrlEncode(position);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("Task.Post")//接口的名称
                .withParams("id",id)//插入一个参数对
                .withParams("passport",passport)
                .withParams("title",title)
                .withParams("tag",tag)
                .withParams("description",description)
                .withParams("price",price)
                .withParams("path",path)
                .withParams("activeTime",activeTime)
                .withParams("position",position)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("postID");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static String postComment(@NonNull String id,@NonNull String passport,
                                     @NonNull String postID,@NonNull String comment)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( byteCount(comment)<=300 );
        }
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        comment=UrlEncode(comment);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("Task.PostComment")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("postID",postID)
                .withParams("comment",comment)
                .withTimeout(SET_TIME_OUT)
                .request();
        Log.e("haha", "status: " + response.getRet() + ", data: " + response.getData() + ", msg: " + response.getMsg());
        if(response.getRet()==200){//200的意思是正常返回
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("success");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static JSONArray getActivities(@NonNull String id, @NonNull String passport, @NonNull String tag,
                                                int from, int count) throws UServerAccessException {
        id = UrlEncode(id);
        passport = UrlEncode(passport);
        tag = UrlEncode(tag);

        PhalApiClientResponse response = createClient()
                .withService("Activity.GetActivityList")
                .withParams("id", id)
                .withParams("passport", passport)
//                .withParams("tag", tag)
                .withParams("from", String.valueOf(from))
                .withParams("count", String.valueOf(count))
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet() == 200) {
            Log.e("haha", UrlDecode(response.getData()));
            try{
                JSONArray data = new JSONArray(response.getData());
                return data;
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static String postActivity(@NonNull String id,@NonNull String passport,@NonNull String title,
                                  @NonNull String tag,String description,@NonNull String activeTime,
                                  double latitude, double longitude, @NonNull String takerCountLimit, @NonNull String location)
            throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert(byteCount(title) >= 5 && byteCount(title) <= 30 && latitude > 0 && longitude > 0);
            if(description!=null)
                UPublicTool.UAssert(byteCount(description) <= 450);
        }

        String position = latitude + "," + longitude;
        id = UrlEncode(id);
        passport = UrlEncode(passport);
        title = UrlEncode(title);
        tag = UrlEncode(tag);
        description = UrlEncode(description);
        activeTime = UrlEncode(activeTime);
        position = UrlEncode(position);
        takerCountLimit = UrlEncode(takerCountLimit);
        location = UrlEncode(location);

        PhalApiClientResponse response=createClient()
                .withService("Activity.Post")//接口的名称
                .withParams("id",id)//插入一个参数对
                .withParams("passport",passport)
                .withParams("title",title)
                .withParams("tag",tag)
                .withParams("description",description)
                .withParams("activeTime",activeTime)
                .withParams("position",position)
                .withParams("takerCountLimit", takerCountLimit)
                .withParams("location",location)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet() == 200) {
            Log.e("haha", UrlDecode(response.getData()));
            try{
                JSONObject data = new JSONObject(response.getData());
                return data.getString("act_id");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static String followUser(@NonNull String id,@NonNull String passport,@NonNull String followByID)throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        followByID=UrlEncode(followByID);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("User.Follow")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("followByID",followByID)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("success");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }

    public static JSONObject getBasicInfo(@NonNull String id,@NonNull String passport,@NonNull String getByID) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        String k=getByID;
        getByID= UrlEncode(getByID);
        PhalApiClientResponse response=createClient()
                .withService("User.GetBasicInfo")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("getByID",getByID)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try {
                return new JSONObject(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }

    public static JSONObject login(@NonNull String username,@NonNull String passport) throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert(byteCount(username)>=4 && byteCount(username)<=25 );
        }
        username=UrlEncode(username);
        passport=UrlEncode(passport);
        PhalApiClientResponse response=createClient()
                .withService("User.Login")
                .withParams("username",username)
                .withParams("passport",passport)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            try {
                return new JSONObject(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String acceptTask(@NonNull String id,@NonNull String passport,@NonNull String postID)throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        postID=UrlEncode(postID);
        PhalApiClient client=createClient();
        PhalApiClientResponse response = client
                .withService("Task.Accept")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("postID",postID)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){
            return "success";
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String getPicture(@NonNull String id,@NonNull String passport,int picId)throws UServerAccessException{
        PhalApiClient client = createClient();
        PhalApiClientResponse response = client
                .withService("Picture.GetPicture")
                .withParams("picId",String.valueOf(picId))
                .withParams("id",id)
                .withParams("passport",passport)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet() == 200){
            return response.getData();
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String uploadAvatar(@NonNull String id,@NonNull String passport,@NonNull String picture)throws UServerAccessException{
        id=UrlEncode(id);
        passport = UrlEncode(passport);
        picture = UrlEncode(picture);
        PhalApiClient client = createClient();
        PhalApiClientResponse response= client.withService("Picture.UploadAvatar")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("pic",picture)
                .request();
        if(response.getRet() == 200){
            return response.getData();
        }else{
            throw new UServerAccessException(response.getRet());
        }
    }
    public static String unfollowUser(@NonNull String id,@NonNull String passport,@NonNull String unfollowByID)throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        unfollowByID=UrlEncode(unfollowByID);

        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("User.Unfollow")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("followByID",unfollowByID)
                .withTimeout(SET_TIME_OUT)
                .request();
        if(response.getRet()==200){//200的意思是正常返回
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("success");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(response.getRet());
        }
    }


    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("http://118.89.156.167/mobile/");
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

    private static String UrlDecode(String str)throws UServerAccessException{
        try{
            if(str==null)return null;
            return URLDecoder.decode(str, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private static int byteCount(String s){
        return s.getBytes().length;
    }
    private ServerAccessApi(){
        //该类不生成实例
    }
}
