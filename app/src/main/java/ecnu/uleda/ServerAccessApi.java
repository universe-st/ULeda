package ecnu.uleda;
import android.util.Log;

import net.phalapi.sdk.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
