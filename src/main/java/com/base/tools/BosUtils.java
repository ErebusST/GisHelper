/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.*;
import com.baidubce.util.HttpUtils;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * The type Bos util.
 *
 * @author 司徒彬
 * @date 2016/10/15
 */
public class BosUtils {
    //Access Key ID
    private final String Access_key = PropertiesFileUtils.bosConfig("accessKey");
    //Secret Access Key
    private final String secretAccessKey = PropertiesFileUtils.bosConfig("secretKey");
    //指定BOS服务域名
    private final String endPoint = PropertiesFileUtils.bosConfig("endPoint");
    //路径分隔字符
    private final static String DELIMITER = PropertiesFileUtils.bosConfig("delimiter");
    private final static String PAGE_DELIMITER = PropertiesFileUtils.bosConfig("pageDelimiter");
    //private long comparisonPartSize = 100 * 1024 * 1024 * 5L;//上传文件大小 如果 大于此数字，分块使用按1M算，如果大于此大小 分块按20M算
    //Obj类型：文件
    private final static String IS_FILE = "F";
    //Obj类型：文件夹
    private final static String IS_DIRECTORY = "D";
    private BosClient bosClient = null;
    private final static String BUCKET_NAME = PropertiesFileUtils.bosConfig("bucketName");
    private final static String FINAL_FILES_PATH = PropertiesFileUtils.bosConfig("finalFilesPath");
    private final static String PRELIMINARIES_PATH = PropertiesFileUtils.bosConfig("preliminariesPath");

    private final static String BOS_OBJECT_DESCRIPTION_PATH = PropertiesFileUtils.bosConfig("bosObjectDescriptionPath");
    private final static long MAX_FILE_SIZE = DataSwitch.convertObjectToLong(PropertiesFileUtils.bosConfig("maxFileSize"));

    private static Map<String, String> contentTypeMap = new HashMap<>();

    public BosUtils() {
        this.generateBosClient();
        contentTypeMap.put("", "application/octet-stream");

        contentTypeMap.put(".jpg", "image/jpeg");
        contentTypeMap.put(".png", "image/png");
        contentTypeMap.put(".jpeg", "image/jpeg");
        contentTypeMap.put(".bmp", "application/lng-bmp");
        contentTypeMap.put(".gif", "image/gif");

        contentTypeMap.put(".txt", "text/plain");
        contentTypeMap.put(".doc", "application/msword");
        contentTypeMap.put(".docx", "application/msword");
        contentTypeMap.put(".xls", "application/vnd.ms-excel");
        contentTypeMap.put(".xlst", "application/vnd.ms-excel");

        contentTypeMap.put(".rm", "application/vnd.rn-realmedia");
        contentTypeMap.put(".rmvb", "application/vnd.rn-realmedia-vbr");
        contentTypeMap.put(".avi", "video/avi");
        contentTypeMap.put(".mp4", "video/mpeg4");

        contentTypeMap.put(".mp3", "audio/mp3");

        contentTypeMap.put(".zip", "application/zip");
    }

    public String getBucketName() {
        return BUCKET_NAME;
    }

    public void setBucketName(String bucketName) {
        bucketName = bucketName;
    }

    public static String getFileType() {
        return IS_FILE;
    }

    public static String getDirectoryType() {
        return IS_DIRECTORY;
    }

    public static String getDelimter() {
        return DELIMITER;
    }

    public static String getPageDelimiter() {
        return PAGE_DELIMITER;
    }

    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    /**
     * 创建BOS链接客户端
     */
    private void generateBosClient() {
        if (bosClient == null) {
            BosClientConfiguration config = new BosClientConfiguration();
            config.setCredentials(new DefaultBceCredentials(Access_key, secretAccessKey));
            config.setEndpoint(endPoint);
            bosClient = new BosClient(config);
        }
    }

    /**
     * 获得初赛文件上传根目录
     *
     * @return the files root path
     */
    private static String getReliminariesFilesRootPath() {
        return PRELIMINARIES_PATH + DELIMITER;
    }

    /**
     * 获得复赛文件上传根目录
     *
     * @return the files root path
     */
    private static String getFinalFilesRootPath() {
        return FINAL_FILES_PATH + DELIMITER;
    }

    /**
     * 获得描述路径
     *
     * @return the description path
     */
    public static String getDescriptionPath() {
        return BOS_OBJECT_DESCRIPTION_PATH + DELIMITER;
    }

    /**
     * 创建Bucket <p> 2016年10月15日22:02:39 <p> situ
     *
     * @param bucketName the bucket name
     * @return boolean
     * @throws Exception the exception
     */
    public boolean createBucket(@Nonnull String bucketName) throws Exception {
        try {
            this.generateBosClient();
            boolean isExist = bosClient.doesBucketExist(bucketName);
            if (isExist) {
                throw new Exception("待创建的[" + bucketName + "]不存在!");
            } else {
                bosClient.createBucket(bucketName);
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获取现有的bucket列表
     *
     * @return the bucket list
     */
    public List<String> getBucketList() {
        try {
            this.generateBosClient();

            List<BucketSummary> bucketSummaries = bosClient.listBuckets().getBuckets();

            List<String> buckNameList = new ArrayList<>();

            for (BucketSummary bucketSummary : bucketSummaries) {
                buckNameList.add(bucketSummary.getName());
            }
            return buckNameList;

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 判断bucket是否存在
     *
     * @param bucketName the bucket name
     * @return the boolean
     */
    public boolean checkBucketExist(@Nonnull String bucketName) {
        try {
            this.generateBosClient();
            boolean isExist = bosClient.doesBucketExist(bucketName);
            return isExist;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 删除Bucket
     *
     * @param bucketName the bucket name
     * @return the boolean
     */
    public boolean deleteBucket(@Nonnull String bucketName) {
        try {
            this.generateBosClient();
            bosClient.deleteBucket(bucketName);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private final static String BOS_PREFIX = "BOS://";

    /**
     * 从BOS路径中获取BucketName BOS://BucketName/Key
     *
     * @param url the url
     * @return the string
     * @throws Exception the exception
     */
    public static String getBucketNameFromUrl(@Nonnull String url) throws Exception {
        try {
            if (url.trim().toUpperCase().startsWith(BOS_PREFIX)) {
                URI uri = new URI(url);
                String bucketName = HttpUtils.generateHostHeader(uri);
                if (bucketName.length() == 0) {
                    throw new Exception("错误的地址信息[" + url + "]");
                } else {
                    return bucketName;
                }
            } else {
                throw new Exception("错误的BOS路径，应如BOS://BucketName/Key，当前路径为[" + url + "]");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public JsonObject listFile(String dirKey, int pageSize, int startIndex) {
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
            this.generateBosClient();
            listObjectsRequest.setPrefix(dirKey);
            listObjectsRequest.setMaxKeys(startIndex + pageSize);

            ListObjectsResponse listing = bosClient.listObjects(listObjectsRequest);
            listing.getContents().stream().skip(startIndex).forEach(obj ->
            {
                String fileKey = obj.getKey();
                System.out.println("fileKey = " + fileKey);
            });
            for (BosObjectSummary objectSummary : listing.getContents()) {
                System.out.println(objectSummary.getKey());
            }

            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void test() {
        String dirKey = "others/ueFile/ueditor/upload/image/";
        this.generateBosClient();

        List<BosObjectSummary> lists = bosClient.listObjects(BUCKET_NAME, dirKey).getContents();
        int total = lists.size();
        System.out.println(" 所有的bos对象，共[" + total + "]个");
        lists.forEach(file -> System.out.println("file.getKey() = " + file.getKey()));
        int pageSize = 2;
        int startIndex = 0;

        System.out.println(" 分页显示");
        while (startIndex + pageSize < total) {

            this.listFile(dirKey, pageSize, startIndex);
            startIndex = startIndex + pageSize - 1;
            System.out.println("翻页");
        }
        //listFile("others/ueFile/ueditor/upload/image/", 20, 0);
    }

    /**
     * 在bos端保存一个指定文件名的文本文件 司徒彬 2016年10月16日15:22:03
     *
     * @param path     the path
     * @param fileName the file name
     * @param content  the content
     * @return the boolean
     */
    public boolean saveTxtFileToBos(@Nonnull String path, @Nonnull String fileName, String content) {
        try {
            path = path.replace(PAGE_DELIMITER, DELIMITER).trim();
            this.generateBosClient();
            if (path.endsWith(DELIMITER)) {
                path = path + fileName;
            } else {
                path = path + DELIMITER + fileName;
            }
            this.deleteFile(path);
            bosClient.putObject(BUCKET_NAME, path, content);
            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {

        }
    }

    /**
     * 读取BOS上的文本文件
     *
     * @param path the path
     * @return the string
     * @throws IOException the io exception
     */
    public String readFileToString(@Nonnull String path) throws Exception {
        try {
            return readFileToString(path, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Read file to string string.
     * <p>
     * 读取bos流文件，根据endStr判断每行的数据是否包含这个信息，如果包含则结束读取，包含本行
     *
     * @param fileKey the path
     * @param endStr  the end str
     * @return the string
     * @throws IOException the io exception
     */
    public String readFileToString(@Nonnull String fileKey, String endStr) throws Exception {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            fileKey = fileKey.replace(PAGE_DELIMITER, DELIMITER).trim();
            this.generateBosClient();
            inputStream = getFileDownloadStream(fileKey);
            inputStreamReader = new InputStreamReader(inputStream, StaticValue.ENCODING);
            reader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = reader.lines().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                sb.append(next);
                if (!StringUtils.isEmpty(endStr) && next.startsWith(endStr)) {
                    break;
                }
            }

            return sb.toString();
        } catch (Exception ex) {
            throw ex;
        } finally {
            reader.close();
            inputStreamReader.close();
            inputStream.close();
        }
    }

    /**
     * 验证文件是否存在
     *
     * @param fileKey the obj key
     * @return boolean
     */
    public boolean checkObjExist(@Nonnull String fileKey) {
        try {
            this.generateBosClient();
            bosClient.getObject(BUCKET_NAME, fileKey);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 创建文件夹
     *
     * @param path       路径
     * @param folderName 文件名
     * @return the boolean
     * @author: 司徒彬 <p>
     * @date: 2016年10月15日22:12:57
     */
    public boolean createFolder(@Nonnull String path, @Nonnull String folderName) {

        try {
            path = path.replace(PAGE_DELIMITER, DELIMITER).trim();
            if (path.length() == 0) {
                path = folderName + DELIMITER;
            } else if (path.endsWith(DELIMITER)) {
                path = path + folderName + DELIMITER;
            } else {
                path = path + DELIMITER + folderName + DELIMITER;
            }

            return createFolder(path);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Create folder boolean.
     *
     * @param dirKey the dir key
     * @return the boolean
     */
    public boolean createFolder(@Nonnull String dirKey) {
        try {
            dirKey = dirKey.replace(PAGE_DELIMITER, DELIMITER).trim();
            if (!dirKey.endsWith(DELIMITER)) {
                dirKey += DELIMITER;
            }
            this.generateBosClient();
            if (!this.checkObjExist(dirKey)) {
                bosClient.putObject(BUCKET_NAME, dirKey, "");
            }

            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 删除文件或文件夹，如果是文件夹 将删除该文件夹下所有文件
     *
     * @param sourceKey the source key
     * @return the boolean
     */
    public boolean deleteFile(@Nonnull String sourceKey) {
        try {
            sourceKey = sourceKey.replace(PAGE_DELIMITER, DELIMITER);
            this.generateBosClient();
            //如果是文件夹
            if (sourceKey.endsWith(DELIMITER)) {
                ListObjectsResponse response = bosClient.listObjects(BUCKET_NAME, sourceKey);

                response.getContents().stream().forEach(bosObjectSummary ->
                {
                    String fileKey = bosObjectSummary.getKey();
                    bosClient.deleteObject(BUCKET_NAME, fileKey);
                });
            } else {//如果是文件
                bosClient.deleteObject(BUCKET_NAME, sourceKey);
            }
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 剪切文件 ,不建议大文件使用此方法,方法已过时
     *
     * @param sourceKey the source key
     * @param targetKey the target key
     * @return the boolean
     * @author: 司徒彬
     * @date: 2016年10月16日18:05:54
     */
    @Deprecated
    public boolean cutFile(@Nonnull String sourceKey, @Nonnull String targetKey) {
        try {
            sourceKey = sourceKey.replace(PAGE_DELIMITER, DELIMITER);
            targetKey = targetKey.replace(PAGE_DELIMITER, DELIMITER);
            this.generateBosClient();
            bosClient.copyObject(BUCKET_NAME, sourceKey, BUCKET_NAME, targetKey);
            bosClient.deleteObject(BUCKET_NAME, sourceKey);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 从BOS路径中获取FileKey BOS://BucketName/Key
     *
     * @param url the url
     * @return the file key from url
     * @throws Exception the exception
     */
    public static String getFileKeyFromUrl(@Nonnull String url) throws Exception {
        try {
            url = url.trim();
            if (url.toUpperCase().startsWith(BOS_PREFIX)) {
                String bucketName = getBucketNameFromUrl(url);
                String filter = "://" + bucketName;
                String fileKey = url.substring(url.indexOf(filter) + filter.length() + 1).trim();
                if (fileKey.length() == 0) {
                    throw new Exception();
                } else {
                    return fileKey;
                }
            } else {
                throw new Exception("错误的BOS路径，应如BOS://BucketName/Key,当前路径为[" + url + "]");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 根据FileKey得到Url BOS://BucketName/Key
     *
     * @param fileKey the file key
     * @return the url
     */
    public static String getUrl(@Nonnull String fileKey) {
        try {
            String filter = "BOS://" + BUCKET_NAME.trim() + DELIMITER;
            return filter + fileKey;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 根据Url获得文件名
     *
     * @param url the url
     * @return the file name from url
     * @throws Exception the exception
     */
    public static String getFileNameFromUrl(@Nonnull String url) throws Exception {
        try {
            String fileKey = getFileKeyFromUrl(url);
            if (fileKey.endsWith(DELIMITER)) {
                String folderName = fileKey.substring(0, fileKey.length() - 1);
                folderName = folderName.substring(folderName.lastIndexOf(DELIMITER) + 1);
                return folderName;
            } else {
                String fileName = fileKey.substring(fileKey.lastIndexOf(DELIMITER) + 1);
                return fileName;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static String getFileTypeFromFileKey(@Nonnull String fileKey) {
        try {
            fileKey = fileKey.replace(PAGE_DELIMITER, DELIMITER);
            return FileUtils.getFileType(fileKey);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 文件上传
     *
     * @param path   the path
     * @param stream the stream
     * @return the boolean
     */
    public boolean uploadFile(@Nonnull String path, @Nonnull InputStream stream) throws Exception {
        try {
            return uploadFile(path, stream, null);
        } catch (Exception ex) {
            throw ex;
        }

    }

    public boolean uploadFile(@Nonnull String path, @Nonnull InputStream stream, String metaKey, String metaValue) throws IOException {
        try {
            Map<String, String> userMeta = new HashMap<>();
            userMeta.put(metaKey, metaValue);
            return uploadFile(path, stream, userMeta);
        } catch (Exception ex) {
            throw ex;
        }
    }


    public boolean uploadFile(@Nonnull String path, @Nonnull InputStream stream, Map<String, String> userMeta) throws IOException {
        try {
            userMeta = userMeta == null ? new HashMap<>() : userMeta;
            path = path.replace(PAGE_DELIMITER, DELIMITER);
            ObjectMetadata metadata = new ObjectMetadata();
            userMeta.forEach((key, value) -> metadata.addUserMetadata(key, value));
            this.generateBosClient();

            String fileType = getFileTypeFromFileKey(path);
            String contentType = "application/octet-stream";
            if (contentTypeMap.containsKey(fileType)) {
                contentType = contentTypeMap.get(fileType);
            }
            metadata.setContentType(contentType);

            bosClient.putObject(BUCKET_NAME, path, stream, metadata);
            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
        }
    }

    public boolean uploadFile(@Nonnull String path, @Nonnull byte[] bytes) {
        try {
            return uploadFile(path, bytes, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean uploadFile(@Nonnull String path, @Nonnull byte[] bytes, String metaKey, String metaValue) {
        try {
            Map<String, String> userMeta = new HashMap<>();
            userMeta.put(metaKey, metaValue);
            return uploadFile(path, bytes, userMeta);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean uploadFile(@Nonnull String path, @Nonnull byte[] bytes, Map<String, String> userMeta) {
        try {
            userMeta = userMeta == null ? new HashMap<>() : userMeta;
            path = path.replace(PAGE_DELIMITER, DELIMITER);
            ObjectMetadata metadata = new ObjectMetadata();
            userMeta.forEach((key, value) -> metadata.addUserMetadata(key, value));
            this.generateBosClient();

            String fileType = getFileTypeFromFileKey(path);
            String contentType = "application/octet-stream";
            if (contentTypeMap.containsKey(fileType)) {
                contentType = contentTypeMap.get(fileType);
            }
            metadata.setContentType(contentType);
            bosClient.putObject(BUCKET_NAME, path, bytes, metadata);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * 获得文件下载的流
     *
     * @param fileKey the file key
     * @return the input stream
     * @throws Exception the exception
     */
    public InputStream getFileDownloadStream(@Nonnull String fileKey) throws Exception {
        try {
            fileKey = fileKey.replace(PAGE_DELIMITER, DELIMITER);
            this.generateBosClient();
            boolean isExist = checkObjExist(fileKey);
            if (!isExist) {
                throw new Exception("文件[" + fileKey + "]不存在");
            }
            BosObject obj = bosClient.getObject(BUCKET_NAME, fileKey);
            return obj.getObjectContent();
        } catch (Exception ex) {
            throw ex;
        }
    }

//    public String updateUserMeta(String fileKey){
//
//    }

    /**
     * 获取文件在bos上的http路径
     *
     * @param fileKey the file key
     * @return the file url
     * @throws Exception the exception
     */
    public String getFileUrl(String fileKey) throws Exception {
        try {
            fileKey = fileKey.replace(PAGE_DELIMITER, DELIMITER);
            this.generateBosClient();
            boolean isExist = checkObjExist(fileKey);
            if (!isExist) {
                throw new Exception("文件[" + fileKey + "]不存在");
            }
            URL url = bosClient.generatePresignedUrl(BUCKET_NAME, fileKey, -1);
            return url.toString();
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
    public static String formatSize(@Nonnull long size) {
        try {
            long rankKB = 1024;
            long rankMB = rankKB * 1024;
            long rankGB = rankMB * 1024;
            long rankTB = rankGB * 1024;
            DecimalFormat format = new DecimalFormat("0.00");
            if (size < 0) {
                return "-";
            } else if (size < rankKB) {
                return size + " B";
            } else if (size < rankMB) {

                return format.format((double) size / rankKB) + " KB";
            } else if (size < rankGB) {
                return format.format((double) size / rankMB) + " MB";
            } else if (size < rankTB) {
                return format.format((double) size / rankGB) + " GB";
            } else {
                return format.format((double) size / rankTB) + " TB";
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 格式化文件大小 最大单位为GB
     *
     * @param size the size
     * @return the string
     */
    public static String formatSizeToGB(@Nonnull long size) {
        try {
            long rankKB = 1024;
            long rankMB = rankKB * 1024;
            long rankGB = rankMB * 1024;
            DecimalFormat format = new DecimalFormat("0.00");
            if (size < 0) {
                return "-";
            } else if (size < rankKB) {
                return size + " B";
            } else if (size < rankMB) {
                return format.format((double) size / rankKB) + " KB";
            } else if (size < rankGB) {
                return format.format((double) size / rankMB) + " MB";
            } else {
                return format.format((double) size / rankGB) + " GB";
            }
        } catch (Exception ex) {
            throw ex;
        }

    }


}
