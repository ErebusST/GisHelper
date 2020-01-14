/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

/**
 * 系统中使用的静态常量
 *
 * @author 司徒彬
 * @date 2018/8/25 19:04
 */
public class StaticValue {
    public final static String ALL = "0";
    public static final String TAB = "\t";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public final static String ENCODING = "UTF-8";// System.getProperty("file.encoding");
    public static final String FILE_SEPARATOR = "/";// System.getProperty("file.separator", "/");
    public static final int BUFFER_SIZE = 5 * 1024;
    public static final boolean IS_DEBUG = "true".equalsIgnoreCase(PropertiesFileUtils.apiConfig("isDebug"));
    public static final String TEMP_PATH = PropertiesFileUtils.apiConfig("tempPath");
    public static final String EMPLOYEE_FACE_PATH = PropertiesFileUtils.apiConfig("employeeFacePath");
    public static final String IMAGE_WEB_URL = PropertiesFileUtils.apiConfig("imageWebUrl");

    public static final String CHECK_URL = PropertiesFileUtils.apiConfig("checkUrl");
    public static final String CHECK_APP_ID = PropertiesFileUtils.apiConfig("checkAppId");
    public static final String CHECK_APP_KEY = PropertiesFileUtils.apiConfig("checkAppKey");
    public static final String CHECK_ORG_ID = PropertiesFileUtils.apiConfig("checkOrgId");
    public static final String CHECK_ORG_AUTH_KEY = PropertiesFileUtils.apiConfig("checkOrgAuthKey");
    public static final String CHECK_REGISTRY_CALLBACK_URL = PropertiesFileUtils.apiConfig("checkRegistryCallbackUrl");
}
