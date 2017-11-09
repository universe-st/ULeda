package ecnu.uleda.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shensheng on 2017/2/1.
 */

public class UPublicTool {

    public static final String SERVICE_PHONE_NUMBER = "10086";
    public static final String BASE_URL_AVATAR = "http://118.89.156.167/uploads/avatars/";
    public static final String BASE_URL_PICTURE = "http://118.89.156.167/uploads/pictures/";

    public static String parseTime(long second) {
        int mi = 60;
        int hh = mi * 60;
        int dd = hh * 24;
        long day = second / dd;
        long hour = (second - day * dd) / hh;
        long minute = (second - day * dd - hour * hh) / mi;
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            if (day <= 7) {
                return sb.append("剩余").append(day).append("天").append(hour).append("小时").toString();
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, (int) second);
                return sb.append("截止").append(calendar.get(Calendar.YEAR)).append("年").append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日").toString();
            }
        }
        sb.append("剩余");
        if (hour > 0) {
            return sb.append(hour).append("小时").append(minute).append("分钟").toString();
        }
        if (minute >= 0) {
            return sb.append(minute).append("分钟").toString();
        }
        return sb.append(second).append("秒").toString();
    }

    public static String timeLeft(Date date){
        Date now = new Date();
        long timeLeft=(date.getTime()-now.getTime())/1000;
        if (timeLeft < 0) {
            return "已失效";
        } else if(timeLeft<60){
            return timeLeft+"秒";
        }
        if(timeLeft/60 < 60){
            return timeLeft/60+"分钟"+timeLeft%60+"秒";
        }
        if(timeLeft/3600<24){
            return timeLeft/3600+"小时"+timeLeft%3600/60+"分钟"+timeLeft%60+"秒";
        }
        return timeLeft/(3600*24)+"天";
    }

    public static String timeBefore(long timeInSecond) {
        return dateToTimeBefore(new Date(timeInSecond * 1000));
    }
    /*
    * 将某日期转化为距离现在的时间，用于在各种场合显示
    * 如果距离现在时间不到一分钟，返回 X秒前
    * 如果距离现在时间不到一小时，返回 X分钟前
    * 如果距离现在时间不到一天，返回X小时前
    * 如果和现在的时间在同一年，返回X月Y日
    * 如果和现在的时间不在同一年，返回X年Y月Z日
    * */
    public static String dateToTimeBefore(Date date){
        Date now=new Date();
        long timeBefore=(now.getTime()-date.getTime())/1000;
        if(timeBefore<60){
            return timeBefore+"秒前";
        }
        timeBefore/=60;
        if(timeBefore<60){
            return timeBefore+"分钟前";
        }
        timeBefore/=60;
        if(timeBefore<24){
            return timeBefore+"小时前";
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        Calendar nowCalendar=Calendar.getInstance();
        if(calendar.get(Calendar.YEAR)==nowCalendar.get(Calendar.YEAR)){
            return (calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        }else{
            return calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        }
    }
    /*
    * 将图标添加进String，返回一个SpannableStringBuilder
    * tag是替换掉的字符串
    * imageId是图片的ID
    * width和height是大小
    * */
    public static SpannableStringBuilder addICONtoString(Context context,String str, String tag, int imageId,int width,int height){
        SpannableStringBuilder ssb=new SpannableStringBuilder(str);
        int tagLength=tag.length();
        int index=-tagLength;
        while((index=str.indexOf(tag,index+tagLength))>=0){
            Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),imageId);
            ImageSpan is=new ImageSpan(context.getApplicationContext(),bitmap);
            is.getDrawable().setBounds(0,0,width,height);
            ssb.setSpan(is,index,index+tag.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }
    public static SpannableStringBuilder addICONtoString(Context context,SpannableStringBuilder ssb,String tag,int imageId,int width,int height){
        int tagLength=tag.length();
        int index=-tagLength;
        String str=ssb.toString();
        while((index=str.indexOf(tag,index+tagLength))>=0){
            Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),imageId);
            ImageSpan is=new ImageSpan(context.getApplicationContext(),bitmap);
            is.getDrawable().setBounds(0,0,width,height);
            ssb.setSpan(is,index,index+tag.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }
    /*返回一个Point对象
    * 表示屏幕大小的x,y倍
    * */
    public static Point getScreenSize(Context context,double x,double y){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point=new Point();
        wm.getDefaultDisplay().getSize(point);
        point.x*=x;
        point.y*=y;
        return point;
    }
    //返回一个字符串的缩略形式
    public static String forShort(String str,int n){
        if(str.length()<=n){
            return str;
        }else{
            str=str.substring(0,n)+"…";
            return str;
        }
    }
    //当这个函数里表达式的值为false时，抛出断言异常，然后终止程序。
    public static void UAssert(boolean a){
        if(!a) {
            throw new AssertionError();
        }
    }
    public static int byteCount(String str){
        return str.getBytes().length;
    }
    private UPublicTool(){

    }

    public static float sp2px(Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static float dp2px(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isTextEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isTextEmpty(String text, String ignore) {
        return text == null || text.length() == 0 || text.equals(ignore);
    }

    public static boolean isTextLegal(String text, int minLen, int maxLen) {
        return isTextEmpty(text) && text.length() >= minLen && text.length() <= maxLen;
    }

}
