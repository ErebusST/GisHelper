/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties文件中的属性读取
 *
 * @author situ
 */
public class PropertiesFileUtils {

    private static String rootPath = ReflectionUtils.getRootPath(PropertiesFileUtils.class);

    static {
        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), "window")) {
            rootPath = rootPath.substring(1);
        }
    }


    private final static String API_CONFIG = rootPath + "/systemconfig/apiconfig.properties";
    private final static String BOS_CONFIG = rootPath + "/systemconfig/bosconfig.properties";


    private final static String WEI_XIN_CONFIG = rootPath + "/systemconfig/weixin.properties";


    public final static String EMAIL_CONFIG = rootPath + "/systemconfig/emailconfig.properties";

    public final static String JDBC_CONFIG = rootPath + "/systemconfig/jdbc.properties";

    public final static String REDIS_CONFIG = rootPath + "/systemconfig/redis.properties";

    public static String apiConfig(String key) {
        return getSetting(API_CONFIG, key);
    }

    public static String bosConfig(String key) {
        return getSetting(BOS_CONFIG, key);
    }

    public static String weChatConfig(String key) {
        return getSetting(WEI_XIN_CONFIG, key);
    }



    public static Properties getProperties(String type) throws IOException {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(type));
            return properties;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 属性文件中读取配置信息 司徒彬 2016年10月16日18:48:23
     *
     * @param type    the path
     * @param keyName the key name
     * @return the key
     */
    private static String getSetting(String type, String keyName) {
        try {
            Map<String, String> settings = SessionMap.getValue(type);
            if (settings == null || settings.size() == 0) {
                settings = new ConcurrentHashMap<>();
                Properties properties = getProperties(type);
                Map<String, String> finalSettings = settings;
                properties.stringPropertyNames().forEach(key ->
                {
                    String setting = DataSwitch.convertObjectToString(properties.get(key));
                    finalSettings.put(key, setting);
                });
                SessionMap.put(type, settings);
            }

            return settings.get(keyName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }


}
