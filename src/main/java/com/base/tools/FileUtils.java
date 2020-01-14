/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 操作文件常用工具类
 *
 * @author 司徒彬
 * @date 2017年1月16日10:42:16
 */
public class FileUtils {

    @Test
    public void test() {
        try {
            String file = "D:\\QQDownload";

            System.out.println("isDirectory(file) = " + isDirectory(file));

            File file1 = new File(file);
            System.out.println("file1.length() = " + file1.length());
            System.out.println("countDirectorySize(file) = " + countFileSizeInDirectory(file));
            System.out.println("countFileNumInDir(file) = " + countFileNumberInDirectory(file));
            file = "D:\\a\\a";
            String target = "d:/a/a1/";
            copyFile(file, target);

            deleteFile(target);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static boolean isExist(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 如果传入是文件夹，则直接创建文件夹 如果传入的文件，则创建该文件的父文件夹 <p> 文件夹必须以 / 或者 \\ 结尾
     *
     * @param filePath the path
     */
    public static void createDirectory(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        Path parentRootPath = file;
        if (!isDirectory(filePath)) {
            parentRootPath = file.getParent();
        }
        if (!Files.exists(parentRootPath)) {
            Files.createDirectories(parentRootPath);
        }
    }

    @Test
    public void test12() {
        String fileName = "D:/iisweb/imageWeb/ueditor/ueditor/upload/image/201718041/122121/";
        System.out.println("getParentDirPath(fileName) = " + getParentDirPath(fileName));
    }

    /**
     * Gets parent dir path.
     *
     * @param file the file
     * @return the parent dir path
     */
    public static String getParentDirPath(String file) {
        Path path = Paths.get(file);
        String dirPath = path.getParent().toString();
        dirPath = formatDirectoryPath(dirPath);
        return dirPath;
    }

    /**
     * Create file.
     *
     * @param filePath the file path
     */
    public static void createFile(String filePath) throws IOException {
        try {
            createDirectory(filePath);
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Is directory boolean.
     *
     * @param filePath the file path
     * @return the boolean
     */
    public static boolean isDirectory(@Nonnull String filePath) {
        try {
            return Files.isDirectory(Paths.get(filePath)) || filePath.endsWith(StaticValue.FILE_SEPARATOR);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获得文件或文件夹的占用空间的大小，单位字节
     *
     * @param dirPath the dir path
     * @return the long
     * @throws Exception the exception
     */
    public static long countFileSizeInDirectory(String dirPath) throws Exception {
        long size;
        File directory = new File(dirPath);
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            throw new Exception("路径 [" + dirPath + "] 不存在!");
        }

        if (isDirectory(dirPath)) {
            File[] files = directory.listFiles();
            size = Arrays.stream(files).filter(File::isFile).mapToLong(File::length).sum();
            List<String> errorList = new ArrayList<>();
            Stream<String> dirList = Arrays.stream(files).filter(File::isDirectory).map(File::getAbsolutePath);
            size += dirList.mapToLong(dir ->
            {
                try {
                    return countFileSizeInDirectory(dir);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(e.getMessage());
                    return 0;
                }
            }).sum();
            if (errorList.size() > 0) {
                throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
            }
        } else {
            size = directory.length();
        }
        return size;
    }


    /**
     * 递归求取目录文件个数
     *
     * @param dirPath the dir path
     * @return the file count in dir
     * @throws Exception the exception
     */
    public static long countFileNumberInDirectory(String dirPath) throws Exception {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            throw new Exception("路径 [" + dirPath + "] 不存在!");
        }
        long count;
        if (isDirectory(dirPath)) {
            File[] files = directory.listFiles();
            count = Arrays.stream(files).filter(File::isFile).count();
            List<String> errorList = new ArrayList<>();
            count += Arrays.stream(files).filter(File::isDirectory).map(File::getAbsolutePath).mapToLong(file ->
            {
                try {
                    return countFileNumberInDirectory(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorList.add(e.getMessage());
                    return 0;
                }
            }).sum();
            if (errorList.size() > 0) {
                throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
            }
        } else {
            count = 1;
        }
        return count;
    }

    /**
     * 列出某文件夹下所有文件列表，不包含传入的pathStr
     *
     * @param pathStr the path str
     * @return the list
     * @throws IOException the io exception
     */
    public static List<Path> listFiles(String pathStr) throws IOException {
        try {
            List<Path> fileList = listFilesContainSelf(pathStr);
            fileList.remove(Paths.get(pathStr));
            return fileList;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获得某文件夹下所有文件列表，包含传去的pathStr
     *
     * @param pathStr the path str
     * @return the list
     * @throws IOException the io exception
     */
    public static List<Path> listFilesContainSelf(String pathStr) throws IOException {
        try {
            List<Path> fileList = new ArrayList<>();

            Path path = Paths.get(pathStr);
            if (isDirectory(path.toString())) {
                List<Path> dirInDirPath = Files.list(path).collect(Collectors.toList());
                fileList.add(path);
                for (Path pathInDir : dirInDirPath) {
                    fileList.addAll(listFilesContainSelf(pathInDir.toString()));

                }
            } else {
                fileList.add(path);
            }


            return fileList;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size the size
     * @return the string
     */
    public static String formatSize(long size) {
        // 转换文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        if (size < 1024) {
            return df.format((double) size) + "B";
        } else if (size < 1048576) {
            return df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            return df.format((double) size / 1048576) + "MB";
        } else {
            return df.format((double) size / 1073741824) + "GB";
        }
    }

    /**
     * 复制文件到指定目录 <p> 需指定文件名
     *
     * @param sourceFilePath the source file path 源路径
     * @param targetFilePath the target file path 目标路径
     * @param fileName       the file name 文件名
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean copyFile(String sourceFilePath, String targetFilePath, String fileName) throws Exception {
        try {
            return copyFile(sourceFilePath, targetFilePath + StaticValue.FILE_SEPARATOR + fileName);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 复制文件或文件夹到指定目录 <p> 如果复制文件，则 sourceFilePath和targetFilePath都是具体的文件路径 <p> 如果复制文件夹，则 sourceFilePath和targetFilePath都是具体的文件夹路径
     *
     * @param sourceFilePath the source file path
     * @param targetFilePath the target file path
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean copyFile(String sourceFilePath, String targetFilePath) throws Exception {
        try {
            if (targetFilePath.trim().equals(sourceFilePath)) {
                return true;
            }
            Path sourceFile = Paths.get(sourceFilePath);
            Path targetFile = Paths.get(targetFilePath);
            if (isDirectory(sourceFilePath)) {
                List<String> errorList = new ArrayList<>();
                if (!isDirectory(targetFilePath)) {
                    throw new Exception("源路径 [" + sourceFilePath + "] 是一个文件夹，目标路径 [" + targetFilePath + "] 必须也是文件夹!");
                }
                File[] files = sourceFile.toFile().listFiles();
                Arrays.stream(files).map(File::getAbsolutePath).forEach(sourcePath ->
                {
                    Path tempTargetFile = Paths.get(targetFilePath, sourcePath.replace(sourceFilePath, ""));
                    try {
                        createDirectory(tempTargetFile.toString() + StaticValue.FILE_SEPARATOR);
                        copyFile(sourcePath, tempTargetFile.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorList.add(e.getMessage());
                    }
                });
                if (errorList.size() > 0) {
                    throw new Exception(StringUtils.getCombineString(StaticValue.LINE_SEPARATOR, errorList));
                }
            } else {
                createDirectory(targetFilePath);
                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Clear directory boolean.
     * <p>
     * 如果文件夹不存在则创建一个文件夹
     *
     * @param filePath the file path
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean clearDirectory(String filePath) throws Exception {
        try {
            createDirectory(filePath);
            filePath = formatDirectoryPath(filePath);
            deleteFile(filePath);
            createDirectory(filePath);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Delete file boolean.
     *
     * @param filePaths the file paths
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean deleteFile(String... filePaths) {
        try {

            for (String filePath : filePaths) {
                if (StringUtils.isEmpty(filePath)) {
                    continue;
                }
                Path file = Paths.get(filePath);

                if (isDirectory(filePath)) {
                    File[] files = file.toFile().listFiles();
                    for (File tempPath : files) {
                        deleteFile(tempPath.getAbsolutePath());
                    }

                    file.toFile().deleteOnExit();
                } else {
                    file.toFile().deleteOnExit();

                }
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * @param file         ：经tomcat解析后的上传文件客户端的文件路径，格式：
     * @param filename     ：要保存到服务器段的可认文件名称，上传成功后会自动加上UUID
     * @param saveFilePath ：上传到服务器段的路径，相对于配置文件定义的 rootSavePath
     * @throws Exception ：
     * @功能简介：文件上传共用方法 @应用页面：
     * @作者姓名：司徒彬、司徒彬 @创建时间：2016年9月13日15:50:28
     * @return：上传成功后的文件名
     */
    public String uploadFile(File file, String filename, String saveFilePath) throws Exception {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            String realPath = PropertiesFileUtils.apiConfig("rootSavePath");
            String path = realPath + saveFilePath;

            createDirectory(path);

            String fileType = getFileType(filename);
            String saveFilename = DataSwitch.getUUID() + fileType;
            // 保存时文件路径和名称
            String saveFilePathAndName = path + StaticValue.FILE_SEPARATOR + saveFilename;
            fos = new FileOutputStream(saveFilePathAndName);
            fis = new FileInputStream(file);
            byte[] buffers = new byte[StaticValue.BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffers)) != -1) {
                fos.write(buffers, 0, len);
            }
            return saveFilename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return null;
    }

    /**
     * 获得文件扩展名 <p> 2016年10月25日23:37:57
     *
     * @param fileName the filename
     * @return the extension name 返回格式 ex: .exe .jpg .png
     */
    public static String getFileType(String fileName) {
        if (fileName.contains(".")) {
            return StringUtils.substring(fileName, fileName.lastIndexOf("."));
        } else {
            return "";
        }

    }


    /**
     * 获得不带扩展的文件名
     * <p>
     * 2016年10月25日23:38:19
     *
     * @param fileName the fileName
     * @return the file name no ex
     */
    public static String getFileName(String fileName) {
        int startIndex = formatFilePath(fileName).lastIndexOf(StaticValue.FILE_SEPARATOR);
        startIndex = startIndex == -1 ? 0 : startIndex + 1;
        int lastIndex = fileName.lastIndexOf(".");
        lastIndex = lastIndex == -1 ? fileName.length() : lastIndex;
        return StringUtils.substring(fileName, startIndex, lastIndex);
    }

    /**
     * Format file path string.
     *
     * @param path the path
     * @return the string
     */
    public static String formatFilePath(String path) {
        return StringUtils.replace(path, "\\", StaticValue.FILE_SEPARATOR);
    }

    /**
     * Format directory path string.
     *
     * @param path the path
     * @return the string
     */
    public static String formatDirectoryPath(String path) {
        if (path.length() == 0) {
            return "";
        }
        path = formatFilePath(path);
        return path.endsWith(StaticValue.FILE_SEPARATOR) ? path : path + StaticValue.FILE_SEPARATOR;
    }

    public static FileItem getFileItemFromRequest(HttpServletRequest request) throws FileUploadException {
        try {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            Optional<FileItem> fileItemOptional = upload.parseRequest(request).stream().filter(fileItem -> !fileItem.isFormField()).findFirst();
            return fileItemOptional.equals(Optional.empty()) ? null : fileItemOptional.get();

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * One file upload string. 单文件上传
     *
     * @param request  the request
     * @param filePath the file path
     * @return the string
     * @throws Exception the exception
     */
    public static String fileUpload(HttpServletRequest request, String filePath) throws Exception {
        String fileNameRet = "";
        String maxSize = request.getParameter("maxSize");
        String fileType = request.getParameter("fileType");
        FileUtils.createDirectory(filePath);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //最大缓存
        factory.setSizeThreshold(5 * 1024);
        //设置文件目录
        factory.setRepository(new File(filePath));
        ServletFileUpload upload = new ServletFileUpload(factory);

        if (StringUtils.isNotEmpty(maxSize)) {
            //文件最大上限
            upload.setSizeMax(Integer.valueOf(maxSize) * 1024 * 1024);
        }
        //获取所有文件列表
        List<FileItem> items = upload.parseRequest(request);
        for (FileItem item : items) {
            if (!item.isFormField()) {
                //文件名
                String fileName = item.getName();
                //检查文件后缀格式
                String fileEnd = getFileType(fileName);
                if (StringUtils.isNotEmpty(fileType)) {
                    List<String> arrType = StringUtils.splitToList(fileType, ",");
                    boolean typeChecked = arrType.stream().filter(type -> ("." + type).equalsIgnoreCase(fileEnd)).count() > 0;

                    if (!typeChecked) {
                        throw new Exception("文件格式不正确");
                    }
                }
                //创建文件唯一名称
                String uuid = DataSwitch.getUUID();
                //真实上传路径
                StringBuffer sbRealPath = new StringBuffer();
                sbRealPath.append(filePath).append(uuid).append(fileEnd);
                //写入文件
                File file = new File(sbRealPath.toString());
                item.write(file);
                //上传成功，更新数据库数据
                fileNameRet = uuid + fileEnd;
            }
        }
        return fileNameRet;
    }


    /**
     * Download file by url boolean.
     *
     * @param url      the url
     * @param savePath the save path
     * @param saveFile the save file
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean downloadFileByUrl(String url, String savePath, String saveFile) throws IOException {

        try {
            savePath = formatDirectoryPath(savePath);
            return downloadFileByUrl(url, savePath + saveFile);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static boolean downloadFileByUrl(String urlStr, String saveFilePath) throws IOException {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            //urlStr = StringUtils.substring(urlStr, 0, urlStr.indexOf("?"));
            saveFilePath = formatFilePath(saveFilePath);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            inputStream = conn.getInputStream();
            FileUtils.createFile(saveFilePath);

            outputStream = new FileOutputStream(saveFilePath);
            //缓存数组
            byte[] buffer = new byte[StaticValue.BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Write content to txt boolean.
     *
     * @param pathStr the path str
     * @param content the content
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean writeContentToTxt(String pathStr, String content) throws IOException {
        try {
            Path path = Paths.get(pathStr);
            createFile(pathStr);
            List<CharSequence> lineList = new ArrayList<>();
            lineList.add(content);
            Files.write(path, lineList, Charset.forName(StaticValue.ENCODING));
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }


    @Test
    public void testSave() {
        try {
            String url = "http://p0.ifengimg.com/pmop/2017/0525/431D297D0CD83ED7FD7A7783A496645A6EB82EB8_size23_w480_h320.jpeg";

            String ext = url.substring(url.lastIndexOf("."));
            System.out.println("ext = " + ext);
            List<Path> paths = listFiles("d:/a/2222/a/");

            for (Path path : paths) {
                System.out.println("path = " + path);
            }
            if (true) {
                return;
            }

            downloadFileByUrl("http://p0.ifengimg.com/pmop/2017/0525/431D297D0CD83ED7FD7A7783A496645A6EB82EB8_size23_w480_h320.jpeg", "d:/a/2222/", DataSwitch.getUUID() + ".jpeg");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setFileRemark(String fileName, String remark) throws Exception {
        String physicalPath = PropertiesFileUtils.apiConfig("physicalPath");
        physicalPath = formatDirectoryPath(physicalPath);
        String remarkFilePath = physicalPath + "remark.bak";
        clearDirectory(remarkFilePath);
        Path contentPath = Paths.get(remarkFilePath, StaticValue.ENCODING);
        String content = StringUtils.getCombineString("", Files.readAllLines(contentPath, Charset.forName(StaticValue.ENCODING)));
        JsonObject contentJson = new JsonParser().parse(content).getAsJsonObject();
        contentJson.addProperty(fileName, remark);
        Files.write(contentPath, contentJson.toString().getBytes());
    }

    @Test
    public void test1() {
        String fileName = "d:/ds/file.file1.exe";
        System.out.println("getFileName(fileName) = " + getFileName(fileName));
        System.out.println("getFileType(fileName) = " + getFileType(fileName));
    }

}
