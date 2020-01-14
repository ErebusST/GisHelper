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
 * @date 2017/5/15 14:59
 */
public class QQUtils {
    @Test
    public void test() throws Exception {
        System.out.println("getUserInfo = " + getUserInfo("3B06CE2DC8D034030013855F6522C4BA"));
    }

    /**
     * 根据access_token返回用户信息
     *
     * @author：ErebusST
     * @date：2017/5/15 15:20
     * callback( {"client_id":"101402242","openid":"B9FBE6EEBBAE405C1626816FEC52726D"} );
     */
    public static JsonObject getUserInfo(String accessToken) throws Exception {
        String url = "https://graph.qq.com/oauth2.0/me?access_token=" + accessToken;
        String response = UrlTools.get(url, ContentTypeEnum.other);
        JsonObject tokenJson = new JsonParser().parse(response.replace("callback(", "").replace(");", "")).getAsJsonObject();
        url = "https://graph.qq.com/user/get_user_info?access_token=" + accessToken + "&oauth_consumer_key=" + tokenJson.get("client_id").getAsString() + "&openid=" +
                tokenJson.get("openid").getAsString();
        response = UrlTools.get(url, ContentTypeEnum.other);
        JsonObject userInfoJson = new JsonParser().parse(response).getAsJsonObject();
        userInfoJson.addProperty("openid", tokenJson.get("openid").getAsString());
        return userInfoJson;
    }
}
