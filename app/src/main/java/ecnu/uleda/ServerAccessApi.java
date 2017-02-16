package ecnu.uleda;
import android.util.Log;

import net.phalapi.sdk.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shensheng on 2017/2/10.
 */

public class ServerAccessApi {

    private static String getPassport()throws UServerAccessException{
        //TODO:返回当前的Passport
        return null;
    }
    public static String getLoginToken(String userName)throws UServerAccessException{
        //断言，保证传入参数的正确性，在DEBUG模式下才启用。
        if(BuildConfig.DEBUG){
            UPublicTool.UAssert( userName.length()>=4 && userName.length()<=25 );
            //当这个函数里表达式的值为false时，抛出断言异常，然后终止程序。
            //这么做是为了保证调用者进行了参数检查。
        }
        PhalApiClient client = createClient();
        PhalApiClientResponse response=client
                .withService("User.GetLoginToken")
                .withParams("username",userName)
                .withTimeout(500)
                .request();
        if(response.getRet()==200){
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


    public static void login(String username,String password) throws UServerAccessException{
        //登陆
    }


    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("https://api.uleda.top/Public/mobile/");
    }
    private ServerAccessApi(){
        //该类不生成实例
    }
}
