/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;


import lombok.Data;

/**
 * Description:
 *
 * @author ErebusST 2017/4/5 下午3:35
 * @since JDK 1.8
 */
@Data
public class WeChatResultXml {
    private String returnCode;

    private String returnMessage;

    private String appId;

    private String mchId;

    private String nonceString;

    private String sign;

    private String resultCode;

    private String prepayId;

    private String tradeType;



    @Override
    public String toString() {
        return "WeChatResultXml{" +
                "return_code='" + returnCode + '\'' +
                ", return_msg='" + returnMessage + '\'' +
                ", appid='" + appId + '\'' +
                ", mch_id='" + mchId + '\'' +
                ", nonce_str='" + nonceString + '\'' +
                ", sign='" + sign + '\'' +
                ", result_code='" + resultCode + '\'' +
                ", prepay_id='" + prepayId + '\'' +
                ", trade_type='" + tradeType + '\'' +
                '}';
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static WeChatResultXml parse(String content) {
//        Serializer ser = new Persister();
//        try
//        {
//            WeChatResultXml obj = ser.read(WeChatResultXml.class, content);
//            return obj;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return null;
//        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        String xmlStr = "" +
//                "<xml>" +
//                "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
//                "   <return_msg><![CDATA[OK]]></return_msg>\n" +
//                "   <appid><![CDATA[wxd61d8fcb00c60612]]></appid>\n" +
//                "   <mch_id><![CDATA[1386792602]]></mch_id>\n" +
//                "   <nonce_str><![CDATA[LMUejsmWWv9K3ssr]]></nonce_str>\n" +
//                "   <sign><![CDATA[59A1E5D98D903623BC75F29F44683723]]></sign>\n" +
//                "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
//                "   <prepay_id><![CDATA[wx20170405144843846d1a19890721004663]]></prepay_id>\n" +
//                "   <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
//                "</xml>";
//        System.out.println(parse(xmlStr));
//    }
}
