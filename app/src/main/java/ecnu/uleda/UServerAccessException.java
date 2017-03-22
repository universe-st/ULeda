package ecnu.uleda;

/**
 * Created by Shensheng on 2017/2/10.
 * 用来处理服务器访问操作中的意外情况。
 */

public class UServerAccessException extends Exception {
    private int mStatus;
    //错误代码
    public static final int UN_LOGIN=-100; //未登录
    public static final int INTERNET_ERROR=500; //网络错误
    public static final int NO_PERMISSION=414;//未许可
    public static final int ERROR_DATA=-103; //错误的数据包
    public static final int PARAMS_ERROR=-104;//参数错误
    public static final int UNKNOWN=999;//未知错误
    public static final int NO_EXIST_USER=401;//不存在的用户
    public static final int FORBID_USER=402;//禁止登陆的用户
    public static final int WRONG_PASSPORT=405;//无效的Passport
    public static final int DATABASE_ERROR=406;//数据库错误
    public static final int NO_TOKEN=403;//无法获取Token
    public static final int LOGIN_PROTECT=407;//登陆错误次数太多
    public static final int CANNOT_FOLLOW_SELF=409;//不能关注自己
    public static final int CANNOT_UN_FOLLOW_SELF=411;//不能取消关注自己
    public static final int NOT_FOLLOWED=412;//未关注
    public static final int BAD_ID=408;//错误的ID
    public static final int WRONG_LOCATION=415;//坐标格式错误
    public static final int NO_EXIST_TASK=413;//不存在的任务
    public static final int TASK_CANNOT_ACCEPT=420;//任务已经被接受或者失效
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
                return "网络异常，请检查网络";
            case NO_PERMISSION:
                return "没有访问权限";
            case ERROR_DATA:
                return "无法解析的数据";
            case PARAMS_ERROR:
                return "参数错误";
            case NO_EXIST_USER:
                return "用户不存在";
            case FORBID_USER:
                return "用户被禁止登陆";
            case WRONG_PASSPORT:
                return "登陆验证错误";
            case DATABASE_ERROR:
                return "数据库错误";
            case NO_TOKEN:
                return "TOKEN获取异常";
            case LOGIN_PROTECT:
                return "多次登陆错误，请24小时后再次登陆";
            case CANNOT_FOLLOW_SELF:
                return "不能关注自己";
            case CANNOT_UN_FOLLOW_SELF:
                return "不能取消关注自己";
            case NOT_FOLLOWED:
                return "还没有关注";
            case BAD_ID:
                return "ID无效";
            case WRONG_LOCATION:
                return "地址参数错误";
            case NO_EXIST_TASK:
                return "不存在的任务";
            case TASK_CANNOT_ACCEPT:
                return "任务已经被接受或者失效";
            case UNKNOWN:
            default:
                return "未知异常";
        }
    }
}
