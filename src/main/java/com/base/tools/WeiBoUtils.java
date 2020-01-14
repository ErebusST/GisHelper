/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.base.model.enumeration.publicenum.ContentTypeEnum;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

/**
 * @author ErebusST
 * @date 2017年5月15日 10:05:44
 */
public class WeiBoUtils {
    private static final String APP_KEY = "46856054";
    private static final String APP_SECRET = "18d64c136afb975fa0407dc96412c56f";

    @Test
    public void test() throws Exception {
        String code = "e78859b455810853fcd66aea9207d516";
        System.out.print(getUserInfo(code).toString());
    }

    /**
     * 功能简介：获取新浪微博的Token
     * 创建作者：ErebusST
     * 创建时间：2017年5月15日 10:05:39
     *
     * @param
     * @return {"access_token":"2.00M8QUoG0o5bKDb65d3dc12dmT3hxC","remind_in":"157679999","expires_in":157679999,"uid":"6242826822"}
     */
    public static JsonObject getToken(String code) throws Exception {
        String returnRul = "http://h5.mimiyouxi.com/games/weBoLoginRedirecturi.html";
        String url = "https://api.weibo.com/oauth2/access_token?client_id=" + APP_KEY + "&client_secret=" + APP_SECRET +
                "&grant_type=authorization_code&redirect_uri=" + returnRul + "&code=" + code;
        String response = UrlTools.get(url);
        JsonObject tokenJson = new JsonParser().parse(response).getAsJsonObject();
        return tokenJson;
    }

    /**
     * 根据用户的code返回用户信息
     *
     * @author：ErebusST
     * @date：2017/5/15 10:27
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static JsonObject getUserInfo(String code) throws Exception {
        JsonObject tokenJson = getToken(code);
        String assessToken = tokenJson.get("access_token").getAsString();//"2.00haF8BC0o5bKD33b615b46ekKaBlB";//"2.00M8QUoG0o5bKDb65d3dc12dmT3hxC";// tokenJson.get("access_token").getAsString();
        String uid = tokenJson.get("uid").getAsString();// "1853498351";//"6242826822";// tokenJson.get("uid").getAsString();
        String url = "https://api.weibo.com/2/users/show.json?access_token=" + assessToken + "&uid=" + uid;

        String response = UrlTools.get(url, ContentTypeEnum.other);
        JsonObject userInfo = new JsonParser().parse(response).getAsJsonObject();
        return userInfo;

    }
}
