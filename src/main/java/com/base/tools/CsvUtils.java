/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * CSV 操作类
 *
 * @author 司徒彬
 * @date 2016/10/17 1:04
 */

public class CsvUtils {


    /**
     * 读取CSV
     *
     * @param filePath the file path
     * @return the list
     * @throws Exception the exception
     */
    public static List<String[]> readCSV(@Nonnull String filePath) throws Exception {
        try {
            return readCSV(filePath, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 读取CSV
     *
     * @param filePath the file path
     * @param skipNum  the skip num
     * @return the list
     * @throws Exception the exception
     */
    public static List<String[]> readCSV(@Nonnull String filePath, @Nonnull int skipNum) throws Exception {
        try {
            return readCSV(filePath, skipNum, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 读取CSV
     *
     * @param filePath the file path
     * @param endStr   the end str
     * @return the list
     * @throws Exception the exception
     */
    public static List<String[]> readCSV(@Nonnull String filePath, String endStr) throws Exception {
        try {
            try {
                return readCSV(filePath, 0, endStr);
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 读取CSV
     *
     * @param filePath the file path
     * @param skipNum  the skip num
     * @param endStr   the end str
     * @return the list
     * @throws Exception the exception
     */
    public static List<String[]> readCSV(@Nonnull String filePath, @Nonnull int skipNum, String endStr) throws Exception {
        try {
            return readCSV(filePath, skipNum, endStr, StaticValue.ENCODING);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Read csv list.
     *
     * @param filePath the file path
     * @param skipNum  the skip num
     * @param endStr   the end str
     * @param encoding the encoding
     * @return the list
     * @throws Exception the exception
     */
    public static List<String[]> readCSV(@Nonnull String filePath, @Nonnull int skipNum, String endStr, @Nonnull String encoding) throws Exception {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new Exception("文件[" + filePath + "]不存在!");
            }
            List<String[]> contentList = Files.lines(Paths.get(filePath), Charset.forName(encoding)).skip(skipNum).filter(str -> StringUtils.isNotEmpty(str))
                    .map(str -> StringUtils.split(str, StaticValue.TAB)).collect(Collectors.toList());
            int limit = contentList.indexOf(contentList.stream()
                    .filter(strings -> (endStr != null && StringUtils.equals(strings[0], endStr)) || endStr == null && true).findFirst().orElse(null));
            limit = limit == -1 ? contentList.size() : limit;
            contentList = contentList.stream().limit(limit).collect(Collectors.toList());
            return contentList;
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * 写CSV，默认使用UTF-8
     *
     * @param path        the path
     * @param contentList the content list
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean writeToCsv(@Nonnull String path, @Nonnull List<String[]> contentList) throws Exception {
        try {
            return writeToCsv(path, contentList, StaticValue.ENCODING);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 写CSV
     *
     * @param filePath    the path
     * @param contentList the content list
     * @param encoding    the encoding
     * @return the boolean
     * @throws Exception the exception
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static boolean writeToCsv(@Nonnull String filePath, @Nonnull List<String[]> contentList, @Nonnull String encoding) throws Exception {
        try {
            Path path = Paths.get(filePath);
            FileUtils.createDirectory(filePath);
            List<String> tempList = contentList.stream().map(strings -> StringUtils.getCombineString(StaticValue.TAB, strings)).collect(Collectors.toList());
            //String bom = new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            byte[] bytes = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            Files.write(path, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            //tempList.add(0, bom);
            Files.write(path, tempList, Charset.forName(encoding), StandardOpenOption.APPEND);
            return true;
        } catch (Exception ex) {
            throw ex;
        }

    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Test
    public void test() {
        try {
            Properties s = System.getProperties();
            Date d1 = new Date();
            System.out.println(d1);
            String path = "D:\\a\\abc.txt";
            List<String[]> contents = new ArrayList<>();
            String[] strings = new String[]{"1", "撒旦法发", "sadasdasf", "的是否是否"};
            contents.add(strings);
            contents.add(strings);
            contents.add(strings);
            contents.add(strings);
            contents.add(strings);

//			List<String[]> list = readCSV(path, 2, "  ");
//			list.forEach(strings -> {
//				Arrays.stream(strings).forEach(str -> {
//					System.out.print(str + split);
//				});
//				System.out.print(lineSeparator);
//			});
            writeToCsv("d:/a/32/ss/eee/asdad/asdasd/ssss.csv", contents, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
