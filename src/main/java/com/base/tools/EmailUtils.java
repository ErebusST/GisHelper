/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 邮件服务工具类
 *
 * @author ErebusST
 * @date 2017/6/15 13:56
 */
public class EmailUtils {

    String from = "";
    String to = "";// 收件人
    String username = "";
    String password = "";
    String subject = "";// 邮件主题
    String content = "";// 邮件正文
    List<String> attachmentFiles = new ArrayList<>();// 附件文件集合


    /**
     * <br>
     * 方法说明：设置登录服务器校验密码 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setPassWord(String pwd) {
        this.password = pwd;
    }


    /**
     * <br>
     * 方法说明：设置登录服务器校验用户 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setUserName(String usn) {
        this.username = usn;
    }

    public String getUserName() {
        return this.username;
    }


    /**
     * <br>
     * 方法说明：设置邮件发送目的邮箱 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setTo(String to) {
        this.to = to;
    }


    /**
     * <br>
     * 方法说明：设置邮件发送源邮箱 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setFrom(String from) {
        this.from = from;
    }


    /**
     * <br>
     * 方法说明：设置邮件主题 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }


    /**
     * <br>
     * 方法说明：设置邮件内容 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <br>
     * 方法说明：往附件组合中添加附件 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    public void addAttachmentFile(String fname) {
        attachmentFiles.add(fname);
    }


    /**
     * <br>
     * 方法说明：乱码处理 <br>
     * 输入参数：String strText <br>
     * 返回类型：
     */
    public String transferChinese(String strText) throws UnsupportedEncodingException {
        try {
            return MimeUtility.encodeText(strText);
        } catch (Exception e) {
            throw e;
        }
    }


    public boolean sendMail() throws Exception {
        try {
            Properties props = PropertiesFileUtils.getProperties(PropertiesFileUtils.EMAIL_CONFIG);

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };

            Session session = Session.getInstance(props, authenticator);
            // 构造MimeMessage 并设定基本的值
            MimeMessage message = new MimeMessage(session);
            InternetAddress fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);
            InternetAddress[] toAddresses = InternetAddress.parse(to);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            message.setSubject(subject);
            // 构造Multipart
            Multipart multipart = new MimeMultipart();
            // 向Multipart添加正文
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html; charset=utf-8");

            // 向MimeMessage添加（Multipart代表正文）
            multipart.addBodyPart(mimeBodyPart);


            // 向Multipart添加附件
            for (String file : attachmentFiles) {
                MimeBodyPart mbpFile = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file);
                mbpFile.setDataHandler(new DataHandler(fileDataSource));
                String fileName = fileDataSource.getName();
                fileName = transferChinese(fileName);
                mbpFile.setFileName(fileName);
                // 向MimeMessage添加（Multipart代表附件）
                multipart.addBodyPart(mbpFile);
            }

            attachmentFiles.clear();
            // 向Multipart添加MimeMessage
            message.setContent(multipart);
            message.setSentDate(new Date());
            // 发送邮件
            Transport.send(message);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }


    /**
     * <br>
     * 方法说明：主方法，用于测试 <br>
     * 输入参数： <br>
     * 返回类型：
     */
    @Test
    public void testSendMail() {
        EmailUtils sendMail = new EmailUtils();
        sendMail.setUserName("dlc");// 您的邮箱用户名
        sendMail.setPassWord("Deeplearning-baidu");// 您的邮箱密码

        sendMail.setTo("47732885@qq.com");// 接收者
        sendMail.setFrom("dlc@baidu.com");// 发送者

        sendMail.setSubject("thisa订单东方闪电发大水公司给");
        //如果发送html内容
        sendMail.setContent("  <div>\n" +
                "\t <img src='http://mpic.tiankong.com/377/e7b/377e7bdf4a40f8d65a657741cdf2260d/640.jpg?x-oss-process=image/resize,m_lfit,h_600,w_600/watermark,image_cXVhbmppbmcucG5n,t_90,g_ne,x_5,y_5'/>\n" +
                "  <div>");
        //如果发送文字
        sendMail.setContent("sssasdas啥安抚");
        //如果添加附件
        sendMail.addAttachmentFile("D:\\经典SQL语句大全.doc");
        try {
            sendMail.sendMail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}