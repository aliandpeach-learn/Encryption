package com.yk.encrypt.tool.rsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过java keytool生成私钥(root.jks)和公钥(root.crt), 私钥放入keystore文件中，使用密码保护起来
 */
public class RSA2048Util {
    private static Logger logger = LoggerFactory.getLogger("rsa");

    private static Map<String, byte[]> keys = new ConcurrentHashMap<>();

    public static synchronized byte[] getPrivateKey() {
        if (keys.containsKey("private") && null != keys.get("private")) {
            return keys.get("private");
        }

        InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.jks");
        try {
            KeyStore privateKeyStore = KeyStore.getInstance("JKS");
            privateKeyStore.load(inputStream, CommonConfig.getInstance().getPasskey().toCharArray());
            Certificate certificate = privateKeyStore.getCertificate("crazy");
            Key key = privateKeyStore.getKey("crazy", CommonConfig.getInstance().getPasskey().toCharArray());
            keys.put("private", key.getEncoded());
            return key.getEncoded();
        } catch (KeyStoreException e) {
            logger.error("KeyStoreException getPrivateKey error", e);
        } catch (CertificateException e) {
            logger.error("CertificateException getPrivateKey error", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException getPrivateKey error", e);
        } catch (IOException e) {
            logger.error("IOException getPrivateKey error", e);
        } catch (UnrecoverableKeyException e) {
            logger.error("UnrecoverableKeyException getPrivateKey error", e);
        }
        return null;
    }

    public static synchronized byte[] getPublicKey() {
        if (keys.containsKey("public") && null != keys.get("public")) {
            return keys.get("public");
        }
        InputStream inputStream = RSA2048Util.class.getClassLoader().getResourceAsStream("root.crt");
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            Certificate crt = certificateFactory.generateCertificate(inputStream);
            keys.put("public", crt.getPublicKey().getEncoded());
            return crt.getPublicKey().getEncoded();
        } catch (CertificateException e) {
            logger.error("CertificateException getPublicKey error", e);
        }
        return null;
    }

    public static String decrypt(String str) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getPrivateKey());
        try {
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String outStr = new String(cipher.doFinal(Base64.getDecoder().decode(str.getBytes("UTF-8"))));
            return outStr;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String str) {

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getPublicKey());
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
            return outStr;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KeyPair generateRSA2048(Map<Integer, int[]> mouses) {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecureRandom random = new SecureRandom();

        int value = 1;
        for (Map.Entry<Integer, int[]> entry : mouses.entrySet()) {
            value = value + entry.getValue()[0] + entry.getValue()[1];
        }
        byte[] bytes = random.generateSeed(value);
        random.setSeed(bytes);
        keyPairGenerator.initialize(2048, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
}
