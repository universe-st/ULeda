package ecnu.uleda;

/**
 * Created by Shensheng on 2017/2/10.
 */

public class UServerAccessException extends Exception {
    private int mStatus;
    public static final int UN_LOGIN=100;
    public static final int INTERNET_ERROR=101;
    public static final int NO_PERMISSION=102;
    public static final int UNKNOWN=999;

    public UServerAccessException(int status){
        super(getStatusInString(status));
        mStatus=status;
    }

    public int getStatus(){
        return mStatus;
    }

    private static String getStatusInString(int status){
        switch (status){
            case UN_LOGIN:
                return "你还没有登陆";
            case INTERNET_ERROR:
                return "网络异常";
            case NO_PERMISSION:
                return "没有访问权限";
            case UNKNOWN:
            default:
                return "未知异常";
        }
    }
}
