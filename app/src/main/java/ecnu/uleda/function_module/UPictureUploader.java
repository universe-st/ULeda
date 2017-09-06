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
    private static final int TIME_OUT = 10*10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private HashMap<String,File> mFiles = null;
    private HashMap<String,String> mParams = null;
    private String mHost = null;
    private String mRet = null;
    /*
    * 创建一个图片上传器的实例
    * */
    public static UPictureUploader create(String host){
        return new UPictureUploader(host);
    }
    /*
    * 构造函数
    * */
    private UPictureUploader(String host){
        mFiles = new HashMap<>();
        mParams = new HashMap<>();
        mHost = host;
    }
    /*
    * 令其携带一个参数
    * */
    public UPictureUploader withParams(String key,String value){
        mParams.put(key,value);
        return this;
    }
    /*
    * 清除其携带的参数，一般不用
    * */
    public UPictureUploader clearParams(){
        mParams.clear();
        return this;
    }
    /*
    * 携带一个文件
    * */
    public UPictureUploader withFiles(String key, File file){
        mFiles.put(key,file);
        return this;
    }

    public UPictureUploader clearFiles(){
        mFiles.clear();
        return this;
    }
    /*服务器上接口服务的名称*/
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
        final String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            URL url = new URL(host);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            if(files!=null) {
                /* *
                 当文件不为空，把文件包装并且上传 */
                OutputStream outputStream=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);
                StringBuffer sb = new StringBuffer();
                sb.append(LINE_END);
                if(params!=null){//根据格式，开始拼接文本参数
                    for(Map.Entry<String,String> entry:params.entrySet()){
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);//分界符
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                        sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                        sb.append(LINE_END);
                        sb.append(entry.getValue());
                        sb.append(LINE_END);//换行！
                    }
                }
                dos.write(sb.toString().getBytes());
                for(Map.Entry<String,File> entry:files.entrySet()){
                    StringBuilder isb = new StringBuilder();
                    isb.append("Content-Disposition: form-data; name=\"");
                    isb.append(entry.getKey());
                    isb.append("\"; filename=\""+entry.getValue().getName()+"\""+LINE_END);
                    isb.append("Content-Type: image/png; charset="+CHARSET+LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
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
