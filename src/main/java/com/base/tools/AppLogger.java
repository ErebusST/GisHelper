/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The type App logger.
 * ErebusST 2017年04月06日16:15:41
 * 获取log4j2方法
 */
public class AppLogger {
    /**
     * Gets app logger (调试环境，打印内容设置).
     *
     * @return the app logger
     */
    public static Logger getAppLogger() {
        return LogManager.getLogger("app");
    }

    public static Logger getAppLogger(Class clazz) {
        return LogManager.getLogger(clazz.getName());
    }

    /**
     * Gets buzz logger （记录业务逻辑日志使用）.
     *
     * @return the buzz logger
     */
    public static Logger getBuzzLogger() {
        return LogManager.getLogger("buzz");
    }

    public static Logger getMoDianLogger() {
        return LogManager.getLogger("modian");
    }

    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
    }
}
