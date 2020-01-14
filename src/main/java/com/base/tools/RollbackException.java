/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

/**
 * 用于回滚事务，不记录日志
 *
 * @author 司徒彬
 * @date 2017-10-09 16:54
 */
public class RollbackException extends Exception {
    public RollbackException() {

    }

    public RollbackException(String message) {
        super(message);
        this.errorMessage = message;
    }

    private String errorMessage;

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
