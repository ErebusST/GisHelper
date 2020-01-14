/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.base.model.enumeration.publicenum.ContentTypeEnum;
import com.base.model.enumeration.publicenum.RequestTypeEnum;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ErebusST
 * @date 2016/12/23 13:25
 */
public class UrlTools {

    private final static int TIME_OUT = 1000 * 10;

    /**
     * 功能简介：根据Url和参数返回被调用url的内容，GET形式
     *
     * @param url the url
     * @return the string
     * @throws IOException the io exception
     */
    public static String get(String url) throws IOException {
        try {
            return call(url, "", RequestTypeEnum.Get, ContentTypeEnum.application_x_www_form_urlencoded);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static String get(String url, ContentTypeEnum contentTypeEnum) throws IOException {
        try {
            String data = "";
            if (contentTypeEnum.equals(ContentTypeEnum.other)) {
                data = null;
            }
            return call(url, data, RequestTypeEnum.Get, contentTypeEnum);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static String get(String url, Map<String, String> map) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet();
        StringBuilder builder = new StringBuilder(url).append("?");
        map.entrySet().forEach(entry->{
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        });

        //System.out.println(builder.toString());
        List<NameValuePair> parameters = setHttpParams(map);
        String parameterStr = URLEncodedUtils.format(parameters, "UTF-8");
        url = url + "?" + parameterStr;
        httpGet.setURI(URI.create(url));
        HttpResponse response = httpClient.execute(httpGet);
        String httpEntityContent = getHttpEntityContent(response);
        httpGet.abort();
        return httpEntityContent;
    }

    /**
     * 设置请求参数
     *
     * @param
     * @return
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> map) {
        List<NameValuePair> parameters = new ArrayList<>();
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> entry : set) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return parameters;
    }

    /**
     * 获得响应HTTP实体内容
     *
     * @param response
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
        //通过HttpResponse 的getEntity()方法获取返回信息
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            br.close();
            is.close();
            return sb.toString();
        }
        return "";
    }

    /**
     * Post Json对象.
     *
     * @param url  the url
     * @param data the dataexport
     * @return the string
     * @throws IOException the io exception
     */
    public static String post(String url, JsonElement data) throws IOException {
        try {
            return call(url, data.toString(), RequestTypeEnum.Post, ContentTypeEnum.application_json);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Post string.
     *
     * @param url  the url
     * @param data the dataexport
     * @return the string
     * @throws IOException the io exception
     */
    public static String post(String url, String data) throws IOException {
        try {
            return call(url, data, RequestTypeEnum.Post, ContentTypeEnum.text_xml);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Call string.
     *
     * @param url             the url
     * @param data            the dataexport
     * @param requestTypeEnum the request type enum
     * @param contentTypeEnum the content type enum
     * @return the string
     * @throws IOException the io exception
     */
    public static String call(@Nonnull String url, String data, @Nonnull RequestTypeEnum requestTypeEnum, ContentTypeEnum contentTypeEnum)
            throws IOException {
        HttpURLConnection connect = null;
        try {
            connect = (HttpURLConnection) (new URL(url).openConnection());

            connect.setRequestMethod(requestTypeEnum.getValue());
            String contentType = "";
            if (contentTypeEnum != null) {
                contentType = contentTypeEnum.getValue() + ";";
            }
            contentType += "CHARSET=" + StaticValue.ENCODING;
            connect.setRequestProperty("Content-Type", contentType);
            connect.setRequestProperty("cache-control", "no-cache");

            connect.setConnectTimeout(TIME_OUT);
            connect.setReadTimeout(TIME_OUT);

            connect.setDoOutput(true);
            connect.setDoInput(true);

            // 连接
            connect.connect();


            if (data != null) {
                OutputStream outputStream = null;
                // 发送数据
                try {
                    outputStream = connect.getOutputStream();
                    outputStream.write(data.getBytes(StaticValue.ENCODING));
                    outputStream.flush();
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }


            // 接收数据
            int responseCode = connect.getResponseCode();
            StringBuffer response = new StringBuffer();
            BufferedReader in = null;
            try {
                String inputLine;
                in = new BufferedReader(new InputStreamReader(connect.getInputStream(), StaticValue.ENCODING));
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            //接受错误数据
            if (responseCode != HttpURLConnection.HTTP_OK) {
                InputStream inputStream = null;
                InputStreamReader reader = null;

                try {
                    inputStream = connect.getErrorStream();
                    reader = new InputStreamReader(inputStream);

                    in = new BufferedReader(reader);
                    String errorLine;
                    response = new StringBuffer();
                    while ((errorLine = in.readLine()) != null) {
                        response.append(errorLine);
                    }
                    JsonObject result = new JsonObject();
                    result.addProperty("result", "fail");
                    result.addProperty("responseCode", responseCode);
                    result.addProperty("errorInfo", response.toString());
                    response = new StringBuffer(response.toString());
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }

            return response.toString();
        } catch (Exception ex) {
            if (connect != null) {
                JsonObject result = new JsonObject();
                int responseCode = connect.getResponseCode();
                BufferedReader in = null;
                InputStream inputStream = null;
                InputStreamReader reader = null;
                try {
                    inputStream = connect.getInputStream();
                    reader = new InputStreamReader(inputStream);
                    in = new BufferedReader(reader);
                    String errorLine;
                    StringBuffer response = new StringBuffer();
                    while ((errorLine = in.readLine()) != null) {
                        response.append(errorLine);
                    }
                    result.addProperty("result", "fail");
                    result.addProperty("responseCode", responseCode);
                    result.addProperty("errorInfo", response.toString());
                    return result.toString();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }

            }
            throw ex;
        } finally {
            if (connect != null) {
                // 关闭连接
                connect.disconnect();
            }
        }
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Test
    public void test() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("fieldName", "loginName");
            jsonObject.addProperty("fieldValue", "e");
            //String s = UrlTools.post("http://meizu.baiducloud.top/api/1.0/baidiDuAI/runScoreResult", jsonObject);
            //String s = UrlTools.post("http://180.76.245.65/api/1.0/baidiDuAI/runScoreResult", jsonObject);
            String s = UrlTools.post("http://192.168.0.8:8084/api/1.0/baidiDuAI/runScoreResult", jsonObject);
            System.out.println("s = " + s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * post提交请求
     * <p>
     * 模拟表单提交
     * <p>
     * 可以提交文件
     * <p>
     * 提交文件的时候 在parameters中 放入 key 和 File
     *
     * @param url
     * @param parameters
     * @return
     */
    public static String post(String url, Map<String, Object> parameters) throws IOException {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<InputStream> inputStreams = new ArrayList<>();
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName(StaticValue.ENCODING));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            //决中文乱码
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

            final Set<Map.Entry<String, Object>> parameterEntries = parameters.entrySet();
            for (Map.Entry<String, Object> parameterEntry : parameterEntries) {
                final String key = parameterEntry.getKey();
                final Object value = parameterEntry.getValue();
                if (ObjectUtils.isNull(value)) {
                    continue;
                }
                if (File.class.equals(value.getClass())) {
                    File file = (File) value;
                    InputStream inputStream = new FileInputStream(file);
                    builder.addBinaryBody(key, inputStream, ContentType.MULTIPART_FORM_DATA, file.getName());
                    inputStreams.add(inputStream);
                } else {
                    builder.addTextBody(key, value.toString(), contentType);
                }
            }


            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpPost);// 执行提交

            // 设置连接超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(TIME_OUT)
                    .setConnectionRequestTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();
            httpPost.setConfig(requestConfig);

            HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", "error");
            jsonObject.addProperty("data", "HTTP请求出现异常: ".concat(ExceptionUtils.getStackTrace(e)));
            result = jsonObject.toString();
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (inputStreams.size() > 0) {
                    for (InputStream inputStream : inputStreams) {
                        inputStream.close();
                    }
                }
                httpClient.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return result;


    }

    public static String get1(String url) throws IOException, URISyntaxException {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder builder = new URIBuilder(url);
            if (url.contains("?")) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                List<String> splitArr = StringUtils.splitToList(url, "?");
                String parameterStr = splitArr.get(splitArr.size() - 1);
                List<String> parameters = StringUtils.splitToList(parameterStr, "&");
                parameters.stream().forEach((parameter -> {
                    List<String> temp = StringUtils.splitToList(parameter, "=");
                    nameValuePairs.add(new BasicNameValuePair(temp.get(0), temp.get(1)));
                }));
                builder.setParameters(nameValuePairs);
            }
            HttpGet httpGet = new HttpGet(builder.build());
            HttpResponse httpResponse = httpClient.execute(httpGet);
            result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {

        }
        return result;
    }


}
