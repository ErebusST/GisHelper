/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常处理类
 * <p>
 * 只在Controller层中调用，用于记录日志，其他层一律向上抛错误
 */
public class MyException extends Exception {


    private static final String BLANK = "      ";

    private static String pageMessageDefault = "系统异常，请联系系统管理员！";
    private static String pageMessage = "系统异常，请联系系统管理员！";

    public JsonObject getPageMessage() {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("result", "failed");
        jObject.addProperty("message", pageMessage);
        return jObject;
    }

    /**
     * 自定义错误类的构造函数，写进错误日志
     *
     * @param exception 错误信息
     * @param clazz     错误发生地点
     * @param msgString 自己定义的错误提示，方便log4错误日志查找
     */
    public MyException(Exception exception, Class clazz, String msgString, Object... params) {
        if (exception.getClass().equals(RollbackException.class)) {
            pageMessage = exception.getMessage();
            return;
        }
        Logger logger = AppLogger.getAppLogger(clazz);
        StringBuffer error = new StringBuffer();
        msgString = msgString.trim().length() == 0 ? clazz.getName() + "发生异常" : msgString;
        error.append("=======================================================================");
        error.append(StaticValue.LINE_SEPARATOR).append(" 异常业务描述信息:").append(msgString).append(StaticValue.LINE_SEPARATOR)
                .append(StaticValue.LINE_SEPARATOR);
        error.append(" 错误类名:").append(clazz.getName()).append(StaticValue.LINE_SEPARATOR).append(StaticValue.LINE_SEPARATOR);

        String stackTrace = ExceptionUtils.getStackTrace(exception);
        error.append(" 异常信息:").append(stackTrace).append(StaticValue.LINE_SEPARATOR).append(StaticValue.LINE_SEPARATOR);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Object> paramList = Arrays.asList(params);

        List<String> indexList = new ArrayList<>();

        paramList.stream().forEach(param ->
        {
            int index = indexList.size();
            error.append(" 参数").append((index + 1)).append(":").append(StaticValue.LINE_SEPARATOR).append(StaticValue.LINE_SEPARATOR);
            String json;
            if (param == null) {
                json = "该参数为null";
            } else {
                json = gson.toJson(param);
            }

            json = StringUtils.replace(json, "\n", StaticValue.LINE_SEPARATOR);
            String[] strings = StringUtils.split(json, StaticValue.LINE_SEPARATOR);
            Arrays.stream(strings).forEach(str -> error.append(BLANK).append(str).append(StaticValue.LINE_SEPARATOR));
            error.append(StaticValue.LINE_SEPARATOR).append(StaticValue.LINE_SEPARATOR);
            indexList.add("");
        });
        indexList.clear();
        if (params.length == 0) {
            error.append(" 无参数");
        }
        error.append("=======================================================================").append(StaticValue.LINE_SEPARATOR)
                .append(StaticValue.LINE_SEPARATOR).append(StaticValue.LINE_SEPARATOR);
        logger.error(error);

        if (StaticValue.IS_DEBUG) {
            pageMessage = exception.toString();

        } else {
            pageMessage = pageMessageDefault;
        }
        exception.printStackTrace();
    }

}
