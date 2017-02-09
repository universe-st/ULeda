package ecnu.uleda;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 胡楠 on 2017/1/24.
 */

public class UCircle {

    public  int PHOTOId;
    public  String NAME = "publisher_name";
    public  String TITLE = "Title";
    public  String ARTICLE = "article";
    public  String TIME = "publish_time";
    public  String GET = "Get_zan";
    public  int DYNAMIC_PHOTO;
    public UCircle(int PHOTOId,String NAME,String TITLE,String ARTICLE,String TIME,String GET,int DYNAMIC_PHOTO)
    {
        this.PHOTOId = PHOTOId;
        this.NAME = NAME;
        this.TITLE = TITLE;
        this.ARTICLE = ARTICLE;
        this.TIME = TIME;
        this.GET = GET;
        this.DYNAMIC_PHOTO = DYNAMIC_PHOTO;
    }
    public int getDYNAMIC_PHOTO()
    {
        return DYNAMIC_PHOTO;
    }

    public int getPHOTOId()
    {
        return PHOTOId;
    }
    public String getNAME()
    {
        return NAME;
    }

    public String getTITLE()
    {
        return TITLE;
    }
    public String getARTICLE()
    {
        return ARTICLE;
    }
    public String getTIME()
    {
        return TIME;
    }
    public String getGET()
    {
        return GET;
    }
}
