/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;

/**
 * Linux 远程调用类
 *
 * @author 司徒彬
 * @date 2016/11/9 14:46
 */
public class SshUtils {
    private Connection connection;
    private String ipAddress;
    private String userName;
    private String passWord;
    private Integer port = 22;
    private static final int TIME_OUT = 1000 * 10;

    public SshUtils() {

    }

    public SshUtils(@Nonnull String ipAddress, @Nonnull String userName, @Nonnull String passWord) {
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.passWord = passWord;
    }

    public SshUtils(@Nonnull String ipAddress, @Nonnull String userName, @Nonnull String passWord, @Nonnull int port) {
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.passWord = passWord;
        this.port = port;
    }

    /**
     * 登录远程Linux主机
     *
     * @return
     * @throws IOException
     */
    public boolean login() throws IOException {
        connection = new Connection(ipAddress, port);
        connection.connect(); // 连接
        return connection.authenticateWithPassword(userName, passWord); // 认证
    }

    /**
     * 执行Shell脚本或命令，返回String，通过 outErr 判断是否成功 ErebusST 2016年11月27日17:39:29
     * <p>
     * 需要手动登陆
     *
     * @param cmds 命令行序列
     * @return
     */
    public String executeCmd(String cmds) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        try {
            Session session = connection.openSession(); // 打开一个会话
            session.execCommand(cmds);

            stdOut = new StreamGobbler(session.getStdout());
            String outStr = processStdout(stdOut, StaticValue.ENCODING);

            stdErr = new StreamGobbler(session.getStderr());
            String outErr = processStdout(stdErr, StaticValue.ENCODING);

            session.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,
                    TIME_OUT);
            int exitStatus = session.getExitStatus();
            return exitStatus == 0 ? outStr : outErr;
            //return StringUtils.isEmpty(outStr) ? outErr : outStr;

        } catch (IOException ex) {
            throw ex;
        } finally {
            if (stdErr != null) {
                stdErr.close();
            }
            if (stdOut != null) {
                stdOut.close();
            }
        }

    }

    public boolean closeConn() {
        connection.close();
        return true;
    }

    /**
     * 执行Shell脚本或命令，返回String，通过 outErr 判断是否成功 ErebusST 2016年11月27日17:39:29
     *
     * @param cmds 命令行序列
     * @return
     */
    public String execute(String cmds) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        try {
            if (this.login()) {
                return executeCmd(cmds);
            } else {
                throw new Exception("登录远程机器失败" + ipAddress);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (stdErr != null) {
                stdErr.close();
            }
            if (stdOut != null) {
                stdOut.close();
            }
            if (connection != null) {
                closeConn();
            }
        }
    }

    public List<String> getListFromResult(String result) {
        return Arrays.asList(StringUtils.split(result, StaticValue.LINE_SEPARATOR));
    }

    /**
     * 解析流获取字符串信息
     *
     * @param in      输入流对象
     * @param charset 字符集
     * @return
     */
    private String processStdout(InputStream in, String charset) throws IOException {
        LineNumberReader input = new LineNumberReader(new InputStreamReader(in, charset));
        return StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, input.lines());
    }

}
