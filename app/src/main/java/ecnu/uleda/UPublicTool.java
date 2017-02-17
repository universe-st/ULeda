package ecnu.uleda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shensheng on 2017/2/1.
 */

public abstract class UPublicTool {
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
    //当这个函数里表达式的值为false时，抛出断言异常，然后终止程序。
    public static void UAssert(boolean a){
        if(!a) {
            throw new AssertionError();
        }
    }
}
