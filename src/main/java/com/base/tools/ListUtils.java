/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ${description}
 *
 * @author 司徒彬
 * @date 2018-04-16 10:25
 */
public class ListUtils extends org.apache.commons.collections.ListUtils {
    public static <T> List<T> unionAll(List<T>... lists) {
        List<T> result = new ArrayList<>();
        Arrays.stream(lists).forEach(list ->
        {
            if (ObjectUtils.isNotEmpty(list)) {
                result.addAll(list);
            }

        });
        return result;
    }
}
