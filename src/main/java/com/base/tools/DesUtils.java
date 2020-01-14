
/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import org.codehaus.xfire.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;

/**
 * DES加密解密
 *
 * @author 司徒彬
 * @date 2014-4-15
 */
public class DesUtils {
    // 密钥
    private final static String KEY = PropertiesFileUtils.apiConfig("desKey");
    // 偏移量
    private final static String IV = PropertiesFileUtils.apiConfig("desIV");

    private final static String MODE = "DES";


    /**
     * 功能简介：加密
     *
     * @param encryptString the encrypt string
     * @return string
     * @throws Exception the exception
     * @author 司徒彬
     * @date 2016/12/21 10:35
     */
    public static String encrypt(String encryptString) throws Exception {
        if (StringUtils.isEmpty(encryptString)) {
            return "";
        }
        int length = DesUtils.KEY.length();
        if (length == 0 || length % 8 != 0) {
            throw new Exception("密钥长度不能为0且为8的整数倍!");
        }

        int count = length / 8;
        byte[] encryptStringByteArr = encryptString.getBytes(StaticValue.ENCODING);
        for (int i = 0; i < count; i++) {
            // 偏移量
            byte[] ivTemp = getKeyByStr(IV);
            IvParameterSpec iv = new IvParameterSpec(ivTemp);
            // 密钥
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MODE);
            String str = DesUtils.KEY.substring(8 * i, 8 * (i + 1));
            byte[] keyBytes = getKeyByStr(str);
            DESKeySpec dks = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(dks);
            // 加密
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            encryptStringByteArr = cipher.doFinal(encryptStringByteArr);
            encryptStringByteArr = Base64.encode(encryptStringByteArr).getBytes(StaticValue.ENCODING);
        }
        return Base64.encode(encryptStringByteArr);
    }

    /**
     * 功能简介：解密 <p>
     *
     * @param decryptString the decrypt string
     * @return string
     * @throws Exception the exception
     * @author 司徒彬
     * @date 2016 /12/21 10:35
     */
    public static String decrypt(String decryptString) throws Exception {
        if (StringUtils.isEmpty(decryptString)) {
            return "";
        }
        String decryptKey = DesUtils.KEY;
        int length = decryptKey.length();
        if (length == 0 || length % 8 != 0) {
            throw new Exception("密钥长度不能为0且为8的整数倍!");
        }

        int count = length / 8;

        decryptString = new String(Base64.decode(decryptString), StaticValue.ENCODING);
        for (int i = count - 1; i >= 0; i--) {
            byte[] decryptStringByteArr = Base64.decode(decryptString);
            // 偏移量
            byte[] ivTemp = getKeyByStr(IV);
            IvParameterSpec iv = new IvParameterSpec(ivTemp);

            // 密钥
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MODE);
            String str = decryptKey.substring(8 * i, 8 * (i + 1));
            byte[] keyBytes = getKeyByStr(str);
            DESKeySpec dks = new DESKeySpec(keyBytes);
            SecretKey key = keyFactory.generateSecret(dks);

            // 解密
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            decryptStringByteArr = cipher.doFinal(decryptStringByteArr);
            decryptString = new String(decryptStringByteArr, StaticValue.ENCODING);
        }
        return new String(decryptString);
    }

    /**
     * 输入密码的字符形式，返回字节数组形式。 如输入字符串：AD67EA2F3BE6E5AD 返回字节数组：{173,103,234,47,59,230,229,173}
     *
     * @throws UnsupportedEncodingException
     */
    private static byte[] getKeyByStr(String str) throws UnsupportedEncodingException {
        return str.getBytes(StaticValue.ENCODING);

    }

}