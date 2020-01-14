/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 压缩解压缩Util
 *
 * @author 司徒彬
 * @date 2017-01-17 15:51
 */
public class ZipUtils {


    @Test
    public void testZip() {
        try {
            Files.deleteIfExists(Paths.get("D:\\a\\2222\\a.zip"));
            zip("D:\\a\\2222\\a\\", "D:\\a\\2222\\a.zip");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean zip(String inputPath, String zipFilePath) throws Exception {

        ZipOutputStream out = null;
        try {
            inputPath = FileUtils.formatFilePath(inputPath);
            out = new ZipOutputStream(new FileOutputStream(zipFilePath), Charset.forName(StaticValue.ENCODING));

            List<Path> files = FileUtils.listFiles(inputPath);


            for (Path path : files) {

                String fileName = FileUtils.formatFilePath(path.toString());
                fileName = StringUtils.replace(fileName, inputPath, "");
                if (!Files.isDirectory(path)) {
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    out.putNextEntry(zipEntry);
                    InputStream is = null;
                    try {
                        is = new FileInputStream(path.toString());
                        byte[] buf = new byte[StaticValue.BUFFER_SIZE];
                        int len;
                        while ((len = is.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        if (out != null) {
                            out.closeEntry();
                        }
                        if (is != null) {
                            is.close();
                        }

                    }
                } else {
                    if (files.stream().filter(file -> !Files.isDirectory(file) && file.toString().startsWith(path.toString())).count() == 0) {
                        fileName = fileName + "//";
                        ZipEntry zipEntry = new ZipEntry(fileName);

                        out.putNextEntry(zipEntry);
                        out.closeEntry();
                    }

                }

            }

            return true;
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    public static boolean upZip(String filePath) {
        try {
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 解压缩zip文件到指定的路径,但是不能解决文件名中文乱码问题.
     *
     * @param zipFile the zip file
     * @param desPath the des path
     * @throws IOException the io exception
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static void upZip(File zipFile, String desPath) throws IOException {
        desPath = FileUtils.formatDirectoryPath(desPath);
        // 新建文件夹，如果存在，则不创建；
        FileUtils.createDirectory(desPath);
        // 建立输出流，用于将从压缩文件中读出的文件流写入到磁盘
        OutputStream out = null;
        // 建立输入流，用于从压缩文件中读出文件
        ZipInputStream is = null;
        try {
            is = new ZipInputStream(new FileInputStream(zipFile), Charset.forName(StaticValue.ENCODING));
            ZipEntry entry = null;
            while ((entry = is.getNextEntry()) != null) {
                File f = new File(desPath + entry.getName());
                if (entry.isDirectory()) {
                    // System.out.println("新建目录：" + f.getByValue());
                    f.mkdir();
                } else {
                    // System.out.println("新增文件：" + f.getByValue());
                    // 根据压缩文件中读出的文件名称新建文件
                    out = new FileOutputStream(f);
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                }
            }
            is.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    public static boolean tar(String filePath) {
        try {
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static boolean unTar(String filePath) {
        try {
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
