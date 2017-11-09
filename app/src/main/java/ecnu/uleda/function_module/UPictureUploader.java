package ecnu.uleda.function_module;
import android.util.Log;
import android.util.Pair;

import java.util.*;
import java.io.*;
import java.net.*;
/*
 * Created by Shensheng on 2017/8/28.
 * 将图片文件上传到服务器的类
 * 可携带多个文件。
 * 调用方法示例：
 *
    public static void test(){
        UPictureUploader uploader = UPictureUploader.create("http://www.xxx.com");
        int retCode = uploader.withService("XXX.UploadPicture")
                            .withFiles("filename",null)
                            .withFiles("filename2",null)
                            .withParams("title","helloworld")
                            .withParams("content","xxxxxxxxx")
                            .upload();
                if(retCode == 200){
                    String ret = uploader.getRet();
                    //...........................................
                }else{
                    //失败
                }
    }
 */

public class UPictureUploader {
    private static final String TAG = "UPictureUploader";
    private static final int TIME_OUT = 10*10000000; //≥¨ ± ±º‰
    private static final String CHARSET = "utf-8"; //…Ë÷√±‡¬Î
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private HashMap<String,File> mFiles = null;
    private HashMap<String,String> mParams = null;
    private String mHost = null;
    private String mRet = null;
    /*
    * ¥¥Ω®“ª∏ˆÕº∆¨…œ¥´∆˜µƒ µ¿˝
    * */
    public static UPictureUploader create(String host){
        return new UPictureUploader(host);
    }
    /*
    * ππ‘Ï∫Ø ˝
    * */
    private UPictureUploader(String host){
        mFiles = new HashMap<>();
        mParams = new HashMap<>();
        mHost = host;
    }
    /*
    * ¡Ó∆‰–Ø¥¯“ª∏ˆ≤Œ ˝
    * */
    public UPictureUploader withParams(String key,String value){
        mParams.put(key,value);
        return this;
    }
    /*
    * «Â≥˝∆‰–Ø¥¯µƒ≤Œ ˝£¨“ª∞„≤ª”√
    * */
    public UPictureUploader clearParams(){
        mParams.clear();
        return this;
    }
    /*
    * –Ø¥¯“ª∏ˆŒƒº˛
    * */
    public UPictureUploader withFiles(String key, File file){
        mFiles.put(key,file);
        return this;
    }
    public UPictureUploader clearFiles(){
        mFiles.clear();
        return this;
    }
    /*∑˛ŒÒ∆˜…œΩ”ø⁄∑˛ŒÒµƒ√˚≥∆*/
    public UPictureUploader withService(String service){
        mParams.put("service",service);
        return this;
    }
    public String getRet(){
        return mRet;
    }
    public int upload(){

        Pair<Integer,String> pair = upload(mHost,mParams,mFiles);
        if(pair == null){
            return -1;
        }
        mRet = pair.second;
        return pair.first;
    }
    private static Pair<Integer,String> upload(String host,Map<String,String> params,Map<String,File> files){
        final String BOUNDARY = UUID.randomUUID().toString(); //±ﬂΩÁ±Í ∂ ÀÊª˙…˙≥… String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //ƒ⁄»›¿‡–Õ
        try {
            URL url = new URL(host);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("POST"); //«Î«Û∑Ω Ω
            conn.setRequestProperty("Charset", CHARSET);//…Ë÷√±‡¬Î
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setDoInput(true); //‘ –Ì ‰»Î¡˜
            conn.setDoOutput(true); //‘ –Ì ‰≥ˆ¡˜
            conn.setUseCaches(false); //≤ª‘ –Ì π”√ª∫¥Ê
            if(files!=null) {
                /* *
                 µ±Œƒº˛≤ªŒ™ø’£¨∞—Œƒº˛∞¸◊∞≤¢«“…œ¥´ */
                OutputStream outputStream=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);
                StringBuffer sb = new StringBuffer();
                sb.append(LINE_END);
                if(params!=null){//∏˘æ›∏Ò Ω£¨ø™ º∆¥Ω”Œƒ±æ≤Œ ˝
                    for(Map.Entry<String,String> entry:params.entrySet()){
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);//∑÷ΩÁ∑˚
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                        sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                        sb.append(LINE_END);
                        sb.append(entry.getValue());
                        sb.append(LINE_END);//ªª––£°
                    }
                }
                dos.write(sb.toString().getBytes());
                for(Map.Entry<String,File> entry:files.entrySet()){
                    StringBuilder isb = new StringBuilder();
                    isb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    isb.append("Content-Disposition: form-data; name=\"");
                    isb.append(entry.getKey());
                    isb.append("\"; filename=\""+entry.getValue().getName()+"\""+LINE_END);
                    String fileName = entry.getValue().getName();
                    String fileType = fileName.contains(".") && fileName.charAt(fileName.length()-1)!='.'?
                            fileName.substring(fileName.lastIndexOf(".")+1,fileName.length())
                            :
                            "jpg";
                    isb.append("Content-Type: image/"+fileType+"; charset="+CHARSET+LINE_END);
                    isb.append(LINE_END);
                    dos.write(isb.toString().getBytes());
                    InputStream iis = new FileInputStream(entry.getValue());
                    byte[] bytes = new byte[1024];
                    long totalBytes = entry.getValue().length();
                    int len;
                    while ((len = iis.read(bytes))!=-1){
                        dos.write(bytes,0,len);
                    }
                    iis.close();
                    dos.write(LINE_END.getBytes());
                }
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int code = conn.getResponseCode();
                if(code != 200){
                    return new Pair<>(code,null);
                }
                sb.setLength(0);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
                return new Pair<>(200,sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}