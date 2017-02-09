package ecnu.uleda;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Shensheng on 2016/11/18.
 */

public class ServerAccessController {

    private static class ParamValue{

        String mParamName;
        String mValue;
        public ParamValue(String paramName,String value){
            mParamName=paramName;
            mValue=value;
        }

    }
    private ArrayList<ParamValue> mParamValues;
    private URL mUrl;
    private String mResult;
    public ServerAccessController(String url){
        try {
            mUrl = new URL(url);
        }catch (Exception e){
            Log.d("DEBUG",e.toString());
        }
        mParamValues=new ArrayList<>();
    }
    public void setUrl(String url){
        try {
            mUrl = new URL(url);
        }catch (Exception e){
            Log.d("DEBUG",e.toString());
        }
    }
    public ServerAccessController addParam(String name,String value){
        mParamValues.add(new ParamValue(name,value));
        return this;
    }
    public void resetParam(){
        mParamValues.clear();
    }

    public void POSTMethod() throws IOException{
        StringBuffer buffer=new StringBuffer();
        boolean first=true;
        for(ParamValue p :mParamValues){
            String t=p.mParamName+"="+p.mValue;
            if(!first){
                buffer.append("&");
            }else{
                first=false;
            }
            buffer.append(t);
        }
        HttpURLConnection connection=(HttpURLConnection)mUrl.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.connect();
        DataOutputStream out=new DataOutputStream(connection.getOutputStream());
        out.writeBytes(buffer.toString());
        out.flush();
        out.close();
        InputStreamReader in=new InputStreamReader(connection.getInputStream());
        BufferedReader reader=new BufferedReader(in);
        StringBuffer result = new StringBuffer();
        String readLine=null;
        while ((readLine=reader.readLine())!=null){
            result.append(readLine);
        }
        in.close();
        connection.disconnect();
        mResult=result.toString();
    }

    public void GETMethod() throws IOException{
        String sUrl = mUrl.toString();
        boolean first=true;
        for(ParamValue p :mParamValues){
            String t=p.mParamName+"="+p.mValue;
            if(!first){
                sUrl=sUrl+"&";
            }else{
                first=false;
            }
            sUrl=sUrl+t;
        }
        URL url=new URL(sUrl);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        InputStreamReader in = new InputStreamReader(connection.getInputStream());
        BufferedReader reader=new BufferedReader(in);
        StringBuffer result=new StringBuffer();
        String readLine=null;
        while((readLine=reader.readLine())!=null){
            result.append(readLine);
        }
        in.close();
        connection.disconnect();
        mResult=result.toString();
    }
    public String getResult(){
        return mResult;
    }
}
