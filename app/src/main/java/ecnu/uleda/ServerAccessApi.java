package ecnu.uleda;
import android.util.Log;

import net.phalapi.sdk.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Shensheng on 2017/2/10.
 */

public class ServerAccessApi {
    private static final int SET_TIME_OUT=500;
    private static String getPassport()throws UServerAccessException{
        //TODO:返回当前的Passport
        return null;
    }



    public static String getLoginToken(String userName)throws UServerAccessException{
        //断言，保证传入参数的正确性，在DEBUG模式下才启用。
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( userName.length()>=4 && userName.length()<=25 );
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
        if(response.getRet()==200){//200的意思是正常返回
            try{
                JSONObject data=new JSONObject(response.getData());
                return data.getString("loginToken");
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            //网络访问失败，抛出一个网络异常
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }


    //这个需要返回一个数据包，所以返回类型是JSONObject
    public static JSONObject getTaskPost(String id,String passport,String postID) throws UServerAccessException{
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }


    public static String cancelTask(String id,String passport,String postID)throws UServerAccessException{

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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }



    public static String delComment(String id,String passport,String commentID)throws UServerAccessException{

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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }



    public static String editTask(String id,String passport,String postID,String title,
                                  String tag,String description,String price,String path,
                                  String activeTime,String position)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( title.length()>=5 && title.length()<=30 );
            UPublicTool.UAssert( tag.length()<=30 );
            UPublicTool.UAssert( description.length()<=450);
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
                .withParams("activeTime",activeTime)
                .withParams("position",position)
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }



    public static JSONObject getComment(String id,String passport,String postID,String start) throws UServerAccessException{
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
            try {
                return new JSONObject(response.getData());
            }catch (JSONException e){
                e.printStackTrace();
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }else{
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }



    public static JSONObject getList(String id,String passport,String orderBy,String start) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
        orderBy=UrlEncode(orderBy);
        start=UrlEncode(start);
        PhalApiClientResponse response=createClient()
                .withService("Task.GetList")
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("orderBy",orderBy)
                .withParams("start",start)
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static String postTask(String id,String passport,String title,
                                  String tag,String description,String price,String path,
                                  String activeTime,String position)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( title.length()>=5 && title.length()<=30 );
            UPublicTool.UAssert( tag.length()<=30 );
            UPublicTool.UAssert( description.length()<=450);
            UPublicTool.UAssert( path.length()<=400 );
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static String postComment(String id,String passport,String postID,String comment)throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( comment.length()<=300 );
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static String followUser(String id,String passport,String followByID)throws UServerAccessException{
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static JSONObject getBasicInfo(String id,String passport,String getByID) throws UServerAccessException{
        id=UrlEncode(id);
        passport=UrlEncode(passport);
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static JSONObject Login(String username,String passport) throws UServerAccessException{
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert(username.length()>=4&&username.length()<=300 );
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }

    public static String unfollowUser(String id,String passport,String unfollowByID)throws UServerAccessException{
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
            throw new UServerAccessException(UServerAccessException.INTERNET_ERROR);
        }
    }




    public static JSONObject login(String username,String passport) throws UServerAccessException{
        //TODO:登陆
        return null;
    }


    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("https://api.uleda.top/Public/mobile/");
    }

    private static String UrlEncode(String str)throws UServerAccessException{
        try{
            return URLEncoder.encode(str,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private ServerAccessApi(){
        //该类不生成实例
    }
}
