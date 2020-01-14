/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;


import com.base.model.enumeration.publicenum.MessageDigestEnum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * MessageDigest计算工具类
 *
 * @author 司徒彬
 */
public class MessageDigestUtils {

    /**
     * 获取MD5签名1
     *
     * @param signParameters the sign parameters
     * @return the signature by md 5
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String getSignatureByMd5(Map<String, Object> signParameters)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            return getSignature(signParameters, MessageDigestEnum.MD5);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets signature by sha 1.
     *
     * @param signParameters the sign parameters
     * @return the signature by sha 1
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String getSignatureBySha1(Map<String, Object> signParameters)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            return getSignature(signParameters, MessageDigestEnum.SHA1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets signature.
     *
     * @param signParameters    the sign parameters
     * @param messageDigestEnum the message digest enum
     * @return the signature
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String getSignature(Map<String, Object> signParameters, MessageDigestEnum messageDigestEnum)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return getSignature(signParameters, messageDigestEnum, true);
    }

    /**
     * 获取签名
     *
     * @param signParameters     the sign parameters
     * @param messageDigestEnum  the message digest enum
     * @param isContainSplitChar the is contain split char
     * @return the signature
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String getSignature(Map<String, Object> signParameters, MessageDigestEnum messageDigestEnum, boolean isContainSplitChar)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            StringBuilder signBuilder = new StringBuilder();
            signParameters.keySet().stream().sorted().forEach(key ->
            {
                Object parameter = signParameters.get(key);
                if (isContainSplitChar == true) {
                    signBuilder.append("&").append(key).append("=").append(parameter);
                } else {
                    signBuilder.append(key).append("=").append(parameter);
                }
            });

            String signStr =
                    signBuilder.charAt(0) != '&' ? signBuilder.toString() : signBuilder.deleteCharAt(0).toString();
            signStr = messageDigest(signStr, messageDigestEnum);
            return signStr;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 把字符串MD5签名
     *
     * @param str the str
     * @return the string
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return messageDigest(str, MessageDigestEnum.MD5);
    }

    /**
     * 把字符串sha1签名
     *
     * @param str the str
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String sha1(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return messageDigest(str, MessageDigestEnum.SHA1);
    }

    /**
     * Message digest string.
     *
     * @param str               the str
     * @param messageDigestEnum the message digest enum
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    public static String messageDigest(String str, MessageDigestEnum messageDigestEnum)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            if (StringUtils.isEmpty(str)) {
                return "";
            }
            MessageDigest messageDigest = createMessageDigest(messageDigestEnum);
            byte[] bytes = messageDigest.digest(str.getBytes(StaticValue.ENCODING));
            StringBuilder sb = new StringBuilder(bytes.length << 1);
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Character.forDigit((bytes[i] >> 4) & 0xf, 16));
                sb.append(Character.forDigit(bytes[i] & 0xf, 16));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static MessageDigest createMessageDigest(MessageDigestEnum messageDigestEnum) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(messageDigestEnum.getValue());
            return messageDigest;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 计算文件的MD5
     *
     * @param inputFile the input file
     * @return the string
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static String countFileMD5(String inputFile) throws IOException, NoSuchAlgorithmException {
        // 缓冲区大小（这个可以抽出一个参数）
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = createMessageDigest(MessageDigestEnum.MD5);
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[StaticValue.BUFFER_SIZE];
            while (digestInputStream.read(buffer) > 0) {
            }
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            throw e;
        } finally {
            if (digestInputStream != null) {
                digestInputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    /**
     * 下面这个函数用于将字节数组换成成16进制的字符串
     *
     * @param byteArray the byte array
     * @return the string
     */
    private static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String temp;
        for (int n = 0; n < byteArray.length; n++) {
            temp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (temp.length() == 1) {
                hs = hs + "0" + temp;
            } else {
                hs = hs + temp;
            }
            if (n < byteArray.length - 1) {
                hs = hs + "";
            }
        }
        return hs;
    }


}
