package com.yk.encrypt.tool.rsa;

import com.yk.encrypt.tool.exception.Constants;
import com.yk.encrypt.tool.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class CommonConfig {

    private Logger logger = LoggerFactory.getLogger("rsa");

    private Map<Key, String> pwd = new HashMap<>();

    private CommonConfig() {

    }

    public static CommonConfig getInstance() {
        return CommonConfigHolder.instance;
    }

    public synchronized String getPasskey() {
        return aesDecrypt(this.pwd.entrySet().iterator().next().getKey(),
                this.pwd.entrySet().iterator().next().getValue());
    }

    public synchronized void setPasskey(String passkey) {
        KeyGenerator keyGenerator = null;
        SecretKeySpec key = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES/ECB/PKCS5Padding");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            key = new SecretKeySpec(keyBytes, "AES/ECB/PKCS5Padding");
            this.pwd.put(key, aesEncrypt(secretKey, passkey));
        } catch (Exception e) {
            logger.error("setPasskey Exception", e);
            throw new GlobalException(Constants.SET_PASSKEY_EXCEPTION, "setPasskey Exception");
        }
    }

    private static class CommonConfigHolder {
        public static CommonConfig instance = new CommonConfig();
    }

    private String aesEncrypt(Key key, String pass) {
        try {
            //加密
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(pass.getBytes());
            return new BigInteger(1, result).toString(16);
        } catch (Exception e) {
            logger.error("aesEncrypt Exception", e);
            throw new GlobalException(Constants.AES_ENCRYPT_EXCEPTION, "aesEncrypt Exception");
        }
    }

    private String aesDecrypt(Key key, String passHex) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(new BigInteger(passHex, 16).toByteArray());
            return new String(result, 0, result.length);
        } catch (Exception e) {
            logger.error("aesDecrypt Exception", e);
            throw new GlobalException(Constants.AES_DECRYPT_EXCEPTION, "aesDecrypt Exception");
        }
    }
}
