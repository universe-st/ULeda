package ecnu.uleda;

import android.util.Base64;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
/**
 * Created by Shensheng on 2016/10/17.
 */

public abstract class SecretTool {
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    public static String encode(String key,String data) throws Exception
    {
        return encode(key, Base64.encode(data.getBytes(), Base64.DEFAULT));
    }

    public static String decodeValue(String key,String data)
    {
        byte[] datas;
        String value = null;
        try {

            datas = decode(key, Base64.decode(data.getBytes(), Base64.DEFAULT));

            value = new String(Base64.decode(datas, Base64.DEFAULT));
        } catch (Exception e) {
            value = "";
        }
        return value;
    }


    public static String encode(String key,byte[] data) throws Exception
    {
        try
        {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);
            byte[] bytes = cipher.doFinal(data);


            return new String(Base64.encode(bytes, Base64.DEFAULT));
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }


    public static byte[] decode(String key,byte[] data) throws Exception
    {
        try
        {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey,paramSpec);
            return cipher.doFinal(data);
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }
    private SecretTool(){
    }
}
