package ecnu.uleda.function_module;
import android.util.Log;
import android.util.Pair;

import java.util.*;
import java.io.*;
import java.net.*;
/**
 * Created by Shensheng on 2017/8/28.
 * 将图片文件上传到服务器的类
 * 仅仅支持一个文件
 */

public class UPictureUploader {
    private static final String TAG = "UPictureUploader";
    private static final int TIME_OUT = 10*10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";

    private HashMap<String,String> mParams = null;
    private String mFileKey = null;
    private File mFile = null;
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
    public UPictureUploader withFile(String key, File file){
        mFile = file;
        mFileKey = key;
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
        Pair<Integer,String> pair = upload(mHost,mFile,mFileKey,mParams);
        if(pair == null){
            return -1;
        }
        mRet = pair.second;
        return pair.first;
    }

    private static Pair<Integer,String> upload(String host,File file,String fileKey,Map<String,String> params){
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
            if(file!=null) {
                /* *
                 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
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
                sb.append(PREFIX);//开始拼接文件参数
                sb.append(BOUNDARY); sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"");
                sb.append(fileKey);
                sb.append("\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: image/png; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                //写入文件数据
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                long totalBytes = file.length();
                long curbytes = 0;
                Log.i(TAG,"total="+totalBytes);
                int len;
                while((len=is.read(bytes))!=-1){
                    curbytes += len;
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
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
