package ecnu.uleda;

import org.json.JSONObject;

/**
 * Created by Shensheng on 2017/2/21.
 * 控制用户的操作
 */

public class UserOperatorController {
    private static UserOperatorController sUOC=null;
    public static UserOperatorController getInstance(){
        if(sUOC==null){
            sUOC=new UserOperatorController();
        }
        return sUOC;
    }

    private String mUserName;
    private String mPassword;
    private String mToken;
    private boolean isLogined=false;
    public UserOperatorController setUserName(String userName){
        mUserName=userName;
        return this;
    }

    public UserOperatorController setPassword(String password){
        mPassword=password;
        return this;
    }

    public void login(){
        //TODO:进行登陆
        try {
            String loginToken=ServerAccessApi.getLoginToken(mUserName);
            JSONObject json=ServerAccessApi.login( mUserName , getLoginPassport() );
            //TODO:处理登陆反返回数据
            //
            isLogined=true;
        }catch (UServerAccessException e){
            e.printStackTrace();
            switch (e.getStatus()){
                case UServerAccessException.PARAMS_ERROR:
                    //TODO:详细写明遇到异常的处理方法
            }
        }
    }
    private String getLoginPassport(){
        //TODO:计算登陆时使用的Passport
        return "";
    }

    private String getPassport(){
        //TODO:计算Passport
        return "";
    }
    private String encodePassword(){
        //TODO:对密码进行加密处理
        return mPassword;
    }
    private UserOperatorController(){
        //单例模式
    }
}
