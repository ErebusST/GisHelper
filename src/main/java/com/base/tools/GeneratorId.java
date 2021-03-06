/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库主键创建类
 *
 * @author 司徒彬
 * @date 2017-08-06 19:53
 */
public class GeneratorId {
    // ==============================Fields===========================================
    /**
     * 开始时间截 系统启动时间减去一天
     */
    private static long startTime;

    static {
        try {
            startTime = DateUtils.getDate("2017-12-21 00:00:00").getTime();
        } catch (ParseException e) {
            new MyException(e, GeneratorId.class, "");
        }
    }

    /**
     * 机器id所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 数据标识id所占的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);

    /**
     * 序列在id中占的位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID向左移12位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    /**
     * 工作机器ID(0~31)
     */
    private static final long WORKER_ID = DataSwitch.convertObjectToLong(PropertiesFileUtils.apiConfig("workerId"));

    /**
     * 数据中心ID(0~31)
     */
    private static final long DATA_CENTER_ID = DataSwitch.convertObjectToLong(PropertiesFileUtils.apiConfig("dataCenterId"));

    /**
     * 毫秒内序列(0~4095)
     */
    private static long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * Check parameters boolean.
     * <p>
     * 工作ID (0~31)
     * <p>
     * 数据中心ID (0~31)
     *
     * @return the boolean
     */
    private static boolean checkParameters() {
        if (WORKER_ID > MAX_WORKER_ID || WORKER_ID < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        } else if (DATA_CENTER_ID > MAX_DATA_CENTER_ID || DATA_CENTER_ID < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        } else {
            return true;
        }
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public static synchronized long nextId() {
        checkParameters();

        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - startTime) << TIMESTAMP_LEFT_SHIFT) //
                | (DATA_CENTER_ID << DATA_CENTER_ID_SHIFT) //
                | (WORKER_ID << WORKER_ID_SHIFT) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }

    //==============================Test=============================================

    /**
     * 测试
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static void main(String[] args) {
        List<Long> content = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            long id = GeneratorId.nextId();
            if (content.contains(id)) {
                System.out.println("+++++++++++++++++++++++++++:" + id);
                break;
            } else {
                content.add(id);
            }
            //System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
