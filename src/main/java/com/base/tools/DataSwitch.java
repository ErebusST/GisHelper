/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.base.tools;

import com.base.model.enumeration.publicenum.DateFormatEnum;
import com.base.model.mongodbentity.IdEntity;
import com.google.gson.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DataSwitch 操作类
 *
 * @author 司徒彬
 * @date 2017年1月12日12 :06:33
 */
public class DataSwitch {

    private static final Logger logger = Logger.getLogger(DataSwitch.class);

    //region Util

    /**
     * Gets default value.
     *
     * @param value the value
     * @param type  the type
     * @return the default value
     */
    public static Object getDefaultValue(Object value, Type type) {
        try {
            Object resultObject;
            if (value == null) {
                resultObject = null;
            } else {
                if (type == String.class) {
                    resultObject = convertObjectToString(value);
                } else if (type == Integer.class || type == int.class) {
                    resultObject = convertObjectToInteger(value);
                } else if (type == Long.class || type == long.class) {
                    resultObject = convertObjectToLong(value);
                } else if (type == Date.class) {
                    resultObject = convertObjectToDate(value);
                } else if (type == Double.class || type == double.class) {
                    resultObject = convertObjectToDouble(value);
                } else if (type == Float.class || type == float.class) {
                    resultObject = convertObjectToFloat(value);
                } else if (type == Boolean.class || type == boolean.class) {
                    resultObject = convertObjectToBoolean(value);
                } else if (type == BigDecimal.class) {
                    resultObject = convertObjectToBigDecimal(value);
                } else if (type == Short.class) {
                    resultObject = convertObjectToShort(value);
                } else if (type == Timestamp.class) {
                    resultObject = DateUtils.getDate(value.toString());
                } else {
                    resultObject = "";
                }
            }
            return resultObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //endregion

    //region 数据格式转换

    /**
     * 将整型对象格式字符串转换成整型对象，如果传入对象为 null 或 空，返回 0
     *
     * @param value the value
     * @return the integer
     */
    public static Integer convertObjectToInteger(Object value) {
        try {

            if (null != value) {
                return NumberUtils.toInt(value.toString(), 0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将类型转换成Double对象，如果传入对象为 null 或 空，返回 0.0
     *
     * @param value the value
     * @return the double
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static Double convertObjectToDouble(Object value) {
        try {
            if (null != value) {
                return new Double(value.toString());
                // return NumberUtils.toDouble(value.toString(), 0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 将对象转换成Long对象，如果传入对象为 null 或 空，返回 0l
     *
     * @param value ： 传入参数值
     * @return the long
     */
    public static Long convertObjectToLong(Object value) {
        try {
            return convertObjectToLong(value, null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将对象转换成Long对象，如果传入对象为 null 或 空，返回 0l
     *
     * @param value        ： 传入参数值
     * @param defaultValue the default value
     * @return the long
     */
    public static Long convertObjectToLong(Object value, Long defaultValue) {
        try {
            if (null != value) {
                return NumberUtils.toLong(value.toString(), 0);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将对象转换成Float对象，如果传入对象为 null 或 空，返回 0f
     *
     * @param value the value
     * @return the float
     */
    public static Float convertObjectToFloat(Object value) {
        try {
            if (null != value) {
                return NumberUtils.toFloat(value.toString(), 0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 将对象转换成BigDecimal对象，如果传入对象为 null 或 空，返回 null
     *
     * @param value the value
     * @return the big decimal
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public static BigDecimal convertObjectToBigDecimal(Object value) {
        try {
            if (null != value) {
                return new BigDecimal(value.toString());
                //float f = DataSwitch.convertObjectToFloat(value);
                //return new BigDecimal(f);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert object to big decimal big decimal.
     *
     * @param value the value
     * @param scale the scale
     * @return the big decimal
     */
    public static BigDecimal convertObjectToBigDecimal(Object value, int scale) {
        try {
            if (null != value) {
                return new BigDecimal(value.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert object to big decimal big decimal.
     *
     * @param value        the value
     * @param scale        the scale
     * @param defaultValue the default value
     * @return the big decimal
     */
    public static BigDecimal convertObjectToBigDecimal(Object value, int scale, BigDecimal defaultValue) {
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                return new BigDecimal(value.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将对象转换成Short对象，如果传入对象为 null 或 空，返回 null
     *
     * @param value the value
     * @return the short
     */
    public static Short convertObjectToShort(Object value) {
        try {
            if (null != value) {
                return Short.parseShort(value.toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将对象转换成Boolean对象，如果传入对象为 null 或 空，返回false
     *
     * @param value the value
     * @return the boolean
     */
    public static Boolean convertObjectToBoolean(Object value) {
        return convertObjectToBoolean(value, false);
    }

    /**
     * Convert object to boolean boolean.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the boolean
     */
    public static Boolean convertObjectToBoolean(Object value, Boolean defaultValue) {
        try {
            if (value == null) {
                return defaultValue;
            }
            Class clazz = value.getClass();
            if (clazz.equals(String.class)) {
                return value.toString().equalsIgnoreCase("true") || value.toString().equals("1");
            } else if (clazz.equals(Long.class)) {
                return DataSwitch.convertObjectToLong(value) == 1L;
            } else if (clazz.equals(Integer.class)) {
                return DataSwitch.convertObjectToInteger(value) == 1;
            } else {
                return (Boolean) value;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 对象转换成String对象，如果传入对象为 null 或 空，返回 ""
     *
     * @param value the value
     * @return the string
     */
    public static String convertObjectToString(Object value) {
        try {
            if (null != value) {
                return value.toString().trim();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将日期字符串转换成日期对象
     *
     * @param time the time
     * @return the date
     */
    public static Date convertObjectToDate(Object time) {
        try {
            String format = null;
            if (time == null || "".equals(DataSwitch.convertObjectToString(time))) {
                return null;
            }
            String timeStr = time.toString();
            if (timeStr.contains(":") && !timeStr.contains(".")) {
                if (timeStr.indexOf(":") == timeStr.lastIndexOf(":")) {
                    format = "yyyy-MM-dd HH:mm";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss";
                }
            } else if (timeStr.contains(".")) {
                timeStr = timeStr.substring(0, timeStr.indexOf("."));
                format = "yyyy-MM-dd HH:mm:ss";
            } else {
                format = "yyyy-MM-dd";
            }
            return DateUtils.parseDate(timeStr, new String[]{format});
        } catch (ParseException e) {
            logger.error("日期格式错误{" + time + "}，正确格式为：yyyy-MM-dd HH:mm");
            e.printStackTrace();
            return null;
        }
    }

    //endregion

    //region Map 与 实体转换

    /**
     * Convert map obj to toolsentity t. 不区分大小写
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param map   the map
     * @return the t
     * @throws Exception the exception
     */
    public static <T> T convertMapObjToEntity(Class<T> clazz, Map<String, Object> map) {
        try {
            T t = clazz.newInstance();
            Field[] fields = ReflectionUtils.getFields(clazz);
            Arrays.stream(fields).forEach(field ->
            {
                String name = field.getName();
                Optional<String> keyOptional =
                        map.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst();
                if (!keyOptional.equals(Optional.empty())) {
                    Type type = field.getType();
                    String keyName = keyOptional.get();
                    ReflectionUtils.setFieldValue(t, name, getDefaultValue(map.get(keyName), type));
                    map.remove(keyName);
                }
            });
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将单个实体转换为Map
     *
     * @param entityObject :目标实体对象
     * @return the map
     * @throws Exception the exception
     */
    public static Map<String, Object> convertEntityToMap(Object entityObject) throws Exception {
        try {
            // 转换后的Map
            Map<String, Object> map = new HashMap<>();
            Class clazz = entityObject.getClass();
            Field[] fields = clazz.getDeclaredFields();

            List<String> errorList = new ArrayList<>();

            Arrays.stream(fields).forEach(field ->
            {
                String key = field.getName();
                Object value = ReflectionUtils.getFieldValue(entityObject, key);
                Class valueClass = value.getClass();
                if (valueClass.equals(ArrayList.class) || valueClass.equals(Collection.class) ||
                        valueClass.equals(List.class)) {
                    try {
                        value = convertListEntityToListMap((List<Object>) value);
                    } catch (Exception e) {
                        errorList.add(e.getMessage());
                    }
                }
                map.put(key, value);
            });
            if (errorList.size() > 0) {
                String errorMessage = StringUtils.getCombineString(errorList);
                throw new Exception(errorMessage);
            }
            return map;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 将listEntity转换成listMap
     *
     * @param listEntityObject the list toolsentity object
     * @return the list
     * @throws Exception the exception
     * @功能简介：将listEntity转换成listMap
     */
    public static List<Map<String, Object>> convertListEntityToListMap(List<Object> listEntityObject) throws Exception {
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            List<String> errorList = new ArrayList<>();
            listEntityObject.forEach(entity ->
            {
                try {
                    Map<String, Object> map = convertEntityToMap(entity);
                    listMap.add(map);
                } catch (Exception e) {
                    errorList.add(e.getMessage());
                }
            });
            if (errorList.size() > 0) {
                String errorMessage = StringUtils.getCombineString(errorList);
                throw new Exception(errorMessage);
            }
            return listMap;
        } catch (Exception ex) {
            throw ex;
        }

    }

    //endregion

    //region Json与实体转换

    /**
     * Gets gson instance.
     *
     * @return the gson instance
     */
    public static Gson getGsonInstance() {
        return getGsonInstance(true);
    }

    private static Gson getGsonInstance(boolean isSerializeNulls) {
        return getGsonInstance(isSerializeNulls, null);
    }

    private static Gson getGsonInstance(boolean isSerializeNulls, DateFormatEnum dateFormatEnum) {
        dateFormatEnum = ObjectUtils.isNull(dateFormatEnum) ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormatEnum;
        GsonBuilder gsonBuilder = new GsonBuilder().
                setPrettyPrinting().
                setDateFormat(dateFormatEnum.getValue());
        if (isSerializeNulls) {
            gsonBuilder.serializeNulls();
        }
        return gsonBuilder.create();
    }

    /**
     * 将json格式的字符串转换成目标实体
     *
     * @param <T>   the type parameter
     * @param json  ：json格式的字符串
     * @param clazz ：实体
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     */
    public static <T> T convertJsonToEntity(JsonObject json, Class<T> clazz) {
        try {
            Gson gson = getGsonInstance();
            T entity = gson.fromJson(json, clazz);
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将实体对象转换成jsonObj对象
     *
     * @param obj the obj
     * @return the json object
     * @throws Exception the exception
     */
    public static JsonObject convertObjectToJsonObject(Object obj) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(obj);
            return jsonElement == null ? null : jsonElement.getAsJsonObject();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Convert string to json json object.
     *
     * @param value the value
     * @return the json object
     * @throws Exception the exception
     */
    public static JsonObject convertStringToJsonObject(String value) {
        try {
            return convertStringToJsonElement(value).getAsJsonObject();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Convert string to json json object.
     *
     * @param value the value
     * @return the json object
     * @throws Exception the exception
     */
    public static JsonElement convertStringToJsonElement(String value) {
        try {
            if (null == value || "".equals(value)) {
                return new JsonObject();
            }
            JsonParser parser = new JsonParser();
            JsonElement jObject = parser.parse(value);
            return jObject;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Convert object to json json object.
     *
     * @param obj              the obj
     * @param isSerializeNulls the is serialize nulls
     * @return the json object
     */
    public static JsonObject convertObjectToJsonObject(Object obj, boolean isSerializeNulls) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(obj, isSerializeNulls);
            return jsonElement == null ? null : jsonElement.getAsJsonObject();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json element json element.
     *
     * @param value the value
     * @return the json element
     */
    public static JsonElement convertObjectToJsonElement(Object value) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(value, true);
            return jsonElement;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json element json element.
     *
     * @param value            the value
     * @param isSerializeNulls the is serialize nulls
     * @return the json element
     */
    public static JsonElement convertObjectToJsonElement(Object value, boolean isSerializeNulls) {
        try {
            if (value == null) {
                return null;
            }
            Gson gson = getGsonInstance(isSerializeNulls);

            String jsonString = gson.toJson(value);
            JsonElement jsonElement = new JsonParser().parse(jsonString);
            return jsonElement.getClass().equals(JsonNull.class) ? null : jsonElement;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert object to json string string.
     *
     * @param value the value
     * @return the string
     */
    public static String convertObjectToJsonString(Object value) {
        Gson gson = getGsonInstance();
        return gson.toJson(value);
    }

    /**
     * Convert list map to json array json array.
     *
     * @param maps the maps
     * @return the json array
     */
    public static JsonArray convertListMapToJsonArray(List<Map<String, Object>> maps) {
        try {
            return convertObjectToJsonElement(maps).getAsJsonArray();
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Convert jsonArrayStr to list list. 将json格式的字符串转换成List对象
     *
     * @param jsonArrayStr the json str
     * @return the list
     * @throws Exception the exception
     */
    public static List<Object> convertJsonArrayStrToList(String jsonArrayStr) {
        if (StringUtils.isEmpty(jsonArrayStr)) {
            return null;
        } else {
            JsonArray jsonArray = convertStringToJsonElement(jsonArrayStr).getAsJsonArray();
            return convertJsonArrayToList(jsonArray);
        }
    }

    /**
     * Convert json str to map map. ：将json格式的字符串转换成Map对象
     *
     * @param jsonObjectStr the json str
     * @return the map
     * @throws Exception the exception
     */
    public static Map<String, Object> convertJsonStringToMap(String jsonObjectStr) {
        if (StringUtils.isEmpty(jsonObjectStr)) {
            return null;
        } else {
            JsonObject jsonObj = convertStringToJsonObject(jsonObjectStr);
            return convertJsonToMap(jsonObj);
        }
    }

    /**
     * Convert json obj to map map.
     *
     * @param json the json
     * @return the map
     */
    public static Map<String, Object> convertJsonToMap(JsonObject json) {
        Map<String, Object> map = new HashMap<>();
        Set<Entry<String, JsonElement>> entrySet = json.entrySet();
        entrySet.forEach(stringJsonElementEntry -> {
            String key = stringJsonElementEntry.getKey();
            JsonElement value = stringJsonElementEntry.getValue();
            if (value instanceof JsonArray) {
                map.put(key, convertJsonArrayToList(value.getAsJsonArray()));
            } else if (value instanceof JsonObject) {

                map.put(key, convertJsonToMap(value.getAsJsonObject()));
            } else {
                map.put(key, value.toString().replaceAll("\"", ""));
            }
        });
        return map;
    }

    /**
     * 将JSONArray对象转换成List集合
     *
     * @param jsonArray the json
     * @return the list
     */
    private static List<Object> convertJsonArrayToList(JsonArray jsonArray) {
        List<Object> list = new ArrayList<>();
        jsonArray.forEach(jsonElement -> {
            if (jsonElement instanceof JsonArray) {
                list.add(convertJsonArrayToList(jsonElement.getAsJsonArray()));
            } else if (jsonElement instanceof JsonObject) {
                list.add(convertJsonToMap(jsonElement.getAsJsonObject()));
            } else if (jsonElement instanceof JsonPrimitive) {
                list.add(jsonElement.getAsJsonPrimitive());
            } else {
                list.add(jsonElement);
            }
        });
        return list;
    }

    /**
     * Convert list t to json object json array.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the json array
     */
    public static <T> JsonArray convertListToJsonArray(List<T> list) {
        try {
            JsonElement element = convertListToJsonArray(list, null);
            return element.getAsJsonArray();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert list t to json object json array.
     *
     * @param <T>        the type parameter
     * @param list       the list
     * @param dateFormat the date format
     * @return the json array
     */
    public static <T> JsonArray convertListToJsonArray(List<T> list, DateFormatEnum dateFormat) {
        try {
            JsonElement element = convertListToJsonElement(list, dateFormat);
            return element.getAsJsonArray();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert list to json element json element.
     *
     * @param list the list
     * @return the json element
     */
    public static JsonElement convertListToJsonElement(List list) {
        return convertListToJsonElement(list, null);
    }

    /**
     * Convert list to json element json element.
     *
     * @param list       the list
     * @param dateFormat the date format
     * @return the json element
     */
    public static JsonElement convertListToJsonElement(List list, DateFormatEnum dateFormat) {
        try {
            Gson gson = getGsonInstance(true, dateFormat);
            return gson.toJsonTree(list);
        } catch (Exception ex) {
            throw ex;
        }
    }


    //endregion

    //region 拼音操作

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母 cn 2 first spell
     */
    public static String getCn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (t != null) {
                        pybf.append(t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().toLowerCase();
    }

    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音 cn 2 spell
     */
    public static String getCn2Spell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().toUpperCase();
    }

    //endregion

    //region DBObject 操作方法

    /**
     * Convert bean to db object db object. JavaBean转化为DBObject
     *
     * @param <T> the type parameter
     * @param t   the t
     * @return db object
     * @throws Exception
     */
    @Deprecated
    public static <T extends IdEntity> DBObject convertBeanToDBObject(T t) {
        if (t == null) {
            return null;
        }
        DBObject dbObject = new BasicDBObject();
        // 获取对象对应类中的所有属性域
        Class clazz = t.getClass();
        Field[] clazzField = clazz.getDeclaredFields();
        Field[] superClassField = clazz.getSuperclass().getDeclaredFields();
        List<Field> fields = ListUtils.union(Arrays.asList(clazzField), Arrays.asList(superClassField));

        fields.forEach(field ->
        {
            // 获取属性名
            String varName = field.getName();
            Object param = ReflectionUtils.getFieldValue(t, field.getName());
            if (param instanceof Integer) {//判断变量的类型
                int value = ((Integer) param).intValue();
                dbObject.put(varName, value);
            } else if (param instanceof String) {
                String value = (String) param;
                dbObject.put(varName, value);
            } else if (param instanceof Double) {
                double value = ((Double) param).doubleValue();
                dbObject.put(varName, value);
            } else if (param instanceof Float) {
                float value = ((Float) param).floatValue();
                dbObject.put(varName, value);
            } else if (param instanceof Long) {
                long value = ((Long) param).longValue();
                dbObject.put(varName, value);
            } else if (param instanceof Boolean) {
                boolean value = ((Boolean) param).booleanValue();
                dbObject.put(varName, value);
            } else if (param instanceof Date) {
                Date value = (Date) param;
                dbObject.put(varName, value);
            } else {
                dbObject.put(varName, param);
            }
        });

        return dbObject;
    }

    /**
     * DBObject转化为JavaBean javabean中的集合未被正确赋值
     *
     * @param <T>   the type parameter
     * @param dbObj the db obj
     * @param clazz the clazz
     * @return the t
     * @throws Exception the exception
     */
    public static <T extends IdEntity> T convertDBObjectToBean(DBObject dbObj, Class<T> clazz) {
        if (dbObj == null) {
            return null;
        }
        JsonObject jsonObject = convertDBObjectToJsonObject(dbObj, clazz, null, true);
        return new Gson().fromJson(jsonObject, clazz);

    }

    /**
     * Convert basic db object to json object json object.
     *
     * @param <T>           the type parameter
     * @param basicDBObject the basic db object
     * @param clazz         the clazz
     * @return the json object
     * @throws Exception the exception
     */
    public static <T> JsonObject convertDBObjectToJsonObject(BasicDBObject basicDBObject, Class<T> clazz) {
        DBObject object = basicDBObject;
        return convertDBObjectToJsonObject(object, clazz);
    }

    /**
     * Convert db object to json object json object.
     *
     * @param <T>           the type parameter
     * @param basicDBObject the basic db object
     * @param clazz         the clazz
     * @return the json object
     * @throws Exception the exception
     */
    public static <T> JsonObject convertDBObjectToJsonObject(DBObject basicDBObject, Class<T> clazz) {
        return convertDBObjectToJsonObject(basicDBObject, clazz, null);
    }

    /**
     * Convert list db object to json array json array.
     *
     * @param <T>          the type parameter
     * @param dbObjectList the db object list
     * @param clazz        the clazz
     * @param startIndex   the start index
     * @return the json array
     */
    public static <T> JsonArray convertListDBObjectToJsonArray(List<DBObject> dbObjectList, Class<T> clazz, int startIndex) {
        try {
            return convertListDBObjectToJsonArray(dbObjectList, clazz, startIndex, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert list db object to json array json array.
     *
     * @param <T>            the type parameter
     * @param dbObjectList   the db object list
     * @param clazz          the clazz
     * @param startIndex     the start index
     * @param dateFormatEnum the date format enum
     * @return the json array
     */
    public static <T> JsonArray convertListDBObjectToJsonArray(List<DBObject> dbObjectList, Class<T> clazz, int startIndex, DateFormatEnum dateFormatEnum) {
        try {
            JsonArray jsonArray = new JsonArray();
            dbObjectList.forEach(dbObject ->
            {
                JsonObject jsonObject = convertDBObjectToJsonObject(dbObject, clazz, dateFormatEnum);
                jsonObject.addProperty("rowIndex", startIndex + jsonArray.size());
                jsonArray.add(jsonObject);
            });

            return jsonArray;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert db object to json object json object.
     *
     * @param <T>        the type parameter
     * @param dbObject   the db object
     * @param clazz      the clazz
     * @param dateFormat the date format
     * @return the json object
     */
    public static <T> JsonObject convertDBObjectToJsonObject(DBObject dbObject, Class<T> clazz, DateFormatEnum dateFormat) {
        try {
            return convertDBObjectToJsonObject(dbObject, clazz, dateFormat, false);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert db object to json object json object.
     *
     * @param <T>           the type parameter
     * @param dbObject      the db object
     * @param clazz         the clazz
     * @param dateFormat    the date format
     * @param isContainList the is contain list
     * @return the json object
     */
    public static <T> JsonObject convertDBObjectToJsonObject(DBObject dbObject, Class<T> clazz, DateFormatEnum dateFormat, boolean isContainList) {
        try {
            dateFormat = dateFormat == null ? DateFormatEnum.YYYY_MM_DD_HH_MM_SS : dateFormat;
            GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setDateFormat(dateFormat.getValue());
            Gson gson = gsonBuilder.create();

            JsonObject jsonObject = gson.toJsonTree(dbObject).getAsJsonObject();
            List<String> keySet = jsonObject.entrySet().stream().map(t -> t.getKey()).collect(Collectors.toList());

            Field[] clazzField = clazz.getDeclaredFields();
            Field[] superClassField = clazz.getSuperclass().getDeclaredFields();
            List<Field> fields = ListUtils.union(Arrays.asList(clazzField), Arrays.asList(superClassField));

            keySet.forEach(key ->
            {
                if (fields.stream().filter(field -> field.getName().equalsIgnoreCase(key)).count() == 0) {
                    jsonObject.remove(key);
                } else if (!isContainList) {
                    Optional<Field> fieldOptional =
                            fields.stream().filter(f -> f.getName().equalsIgnoreCase(key)).findFirst();
                    if (!fieldOptional.equals(Optional.empty())) {
                        Field field = fieldOptional.get();
                        if (field.getType().equals(ArrayList.class) || field.getType().equals(List.class)) {
                            jsonObject.remove(key);
                        }
                    }
                }
            });

            if (keySet.contains("_id")) {
                String id = dbObject.get("_id").toString();
                jsonObject.addProperty("id", id);
            }

            return jsonObject;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert db cursor to json array json array.
     *
     * @param <T>    the type parameter
     * @param cursor the cursor
     * @param clazz  the clazz
     * @return the json array
     */
    public static <T extends IdEntity> JsonArray convertDBCursorToJsonArray(DBCursor cursor, Class<T> clazz) {
        try {
            return convertDBCursorToJsonArray(cursor, clazz, 1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert db cursor to json array json array.
     *
     * @param <T>        the type parameter
     * @param cursor     the cursor
     * @param clazz      the clazz
     * @param startIndex the start index
     * @return the json array
     */
    public static <T extends IdEntity> JsonArray convertDBCursorToJsonArray(DBCursor cursor, Class<T> clazz, int startIndex) {
        try {
            return convertDBCursorToJsonArray(cursor, clazz, startIndex, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Convert db cursor json array.
     *
     * @param <T>        the type parameter
     * @param cursor     the cursor
     * @param clazz      the clazz
     * @param startIndex the start index
     * @param dateFormat the date format
     * @return the json array
     */
    public static <T extends IdEntity> JsonArray convertDBCursorToJsonArray(DBCursor cursor, Class<T> clazz, int startIndex, DateFormatEnum dateFormat) {
        try {
            List<DBObject> dbObjects = cursor.toArray();
            return convertListDBObjectToJsonArray(dbObjects, clazz, startIndex, dateFormat);
        } catch (Exception ex) {
            throw ex;
        }
    }

    //endregion


    //region mongo分页方法

    /**
     * 通过mongo游标获取分页json
     *
     * @param <T>      the type parameter
     * @param cursor   the cursor
     * @param clazz    the clazz
     * @param pageNum  the page num
     * @param pageSize the page size
     * @param total    the total
     * @return the pager info
     */
    public static <T extends IdEntity> JsonObject getPagerInfo(DBCursor cursor, Class<T> clazz, int pageNum, int pageSize, long total) {
        try {
            return getPagerInfo(cursor, clazz, pageNum, pageSize, total, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 通过mongo游标获取分页json
     *
     * @param <T>            the type parameter
     * @param cursor         the cursor
     * @param clazz          the clazz
     * @param pageNum        the page num
     * @param pageSize       the page size
     * @param total          the total
     * @param dateFormatEnum the date format enum
     * @return the pager info
     */
    public static <T extends IdEntity> JsonObject getPagerInfo(DBCursor cursor, Class<T> clazz, int pageNum, int pageSize, long total,
                                                               DateFormatEnum dateFormatEnum) {
        try {
            return getPagerInfo(cursor.toArray(), clazz, pageNum, pageSize, total, dateFormatEnum);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 通过ListDBObject获取分页json
     *
     * @param <T>          the type parameter
     * @param dbObjectList the db object list
     * @param clazz        the clazz
     * @param pageNum      the page num
     * @param pageSize     the page size
     * @param total        the total
     * @return the pager info
     */
    public static <T extends IdEntity> JsonObject getPagerInfo(List<DBObject> dbObjectList, Class<T> clazz, int pageNum, int pageSize, long total) {
        try {
            return getPagerInfo(dbObjectList, clazz, pageNum, pageSize, total, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 通过ListDBObject获取分页json
     *
     * @param <T>            the type parameter
     * @param dbObjectList   the db object list
     * @param clazz          the clazz
     * @param pageNum        the page num
     * @param pageSize       the page size
     * @param total          the total
     * @param dateFormatEnum the date format enum
     * @return the pager info
     */
    public static <T extends IdEntity> JsonObject getPagerInfo(List<DBObject> dbObjectList, Class<T> clazz, int pageNum, int pageSize, long total,
                                                               DateFormatEnum dateFormatEnum) {
        try {
            int startIndex = (pageNum - 1) * pageSize + 1;
            JsonArray rows = convertListDBObjectToJsonArray(dbObjectList, clazz, startIndex, dateFormatEnum);
            return getPagerInfo(rows, pageNum, pageSize, total);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 通过jsonarray获得分页json
     *
     * @param rows     the rows
     * @param pageNum  the page num
     * @param pageSize the page size
     * @param total    the total
     * @return the pager info
     */
    public static JsonObject getPagerInfo(JsonArray rows, int pageNum, int pageSize, long total) {
        try {
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("pageNum", pageNum);
            resultJson.addProperty("pageSize", pageSize);
            resultJson.addProperty("total", total);
            resultJson.add("rows", rows);
            return resultJson;
        } catch (Exception ex) {
            throw ex;
        }
    }

    //endregion

    /**
     * 生成UUID
     *
     * @return the uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 将ajax url中文解码
     *
     * @param strValue the str value
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public String httpUrlDecodeUTF8(String strValue) throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(strValue, "utf-8");
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets random num.
     *
     * @param min the min
     * @param max the max
     * @return the random num
     */
    public static int getRandomNum(int min, int max) {
        int random = new Random(System.nanoTime()).nextInt(max) % (max - min + 1) + min;
        return random;
    }

    /**
     * 清洗html中注码问题
     *
     * @param html the html
     * @return the string
     * @author：ErebusST
     * @date：2017/7/11 9 :43
     */
    public static String cleanHtml(String html) {
        html = Jsoup.clean(html, Whitelist.none());
        html = Jsoup.clean(html, Whitelist.simpleText());
        html = Jsoup.clean(html, Whitelist.basic());
        html = Jsoup.clean(html, Whitelist.basicWithImages());
        html = Jsoup.clean(html, Whitelist.relaxed());
        return html;
    }

    /**
     * Convert request to entity t.
     * <p>
     * 用于对应实体中有List的情况
     *
     * @param <T>     the type parameter
     * @param clazz   the clazz
     * @param request the request
     * @return the t
     */
    public static <T> T convertRequestToEntity(Class<T> clazz, HttpServletRequest request) {
        Map<String, Object> parameterMap = new HashMap<>();
        request.getParameterMap().entrySet().stream().forEach(map -> {

            if (map.getValue().length == 1 && ObjectUtils.isNotEmpty(map.getValue()[0])) {
                parameterMap.put(map.getKey(), map.getValue()[0]);
            } else if (map.getValue().length > 1) {
                Object[] objects = Arrays.stream(map.getValue()).filter(para -> ObjectUtils.isNotEmpty(para)).collect(Collectors.toList()).toArray();
                parameterMap.put(map.getKey(), objects);
            }

        });
        JsonObject jsonObject = convertObjectToJson(parameterMap);
        return convertJsonToEntity(jsonObject, clazz);
    }

    /**
     * 将实体对象转换成jsonObj对象
     *
     * @param obj the obj
     * @return the json object
     * @throws Exception the exception
     */
    public static JsonObject convertObjectToJson(Object obj) {
        try {
            JsonElement jsonElement = convertObjectToJsonElement(obj);
            return jsonElement == null ? null : jsonElement.getAsJsonObject();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 传入多个字符串，取得合并并且用半角逗号分隔的字符串 <p> 如：getCombineString(str1,str2,str3) 返回值 str1,str2,str3 <p> 如果 str1、str2、str3中有空字符串则去掉 如：str2为空，则 返回str1,str2
     *
     * @param <T>     the type parameter
     * @param strings the strings
     * @return the string
     */
    public static <T> String getCombineString(List<T> strings) {
        try {
            Stream<String> stream = strings.stream().filter(str -> ObjectUtils.isNotNull(str)).map(str -> str.toString());
            return getCombineString(stream);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param strings the strings
     * @return the combine string
     */
    public static String getCombineString(Stream<String> strings) {
        try {
            return getCombineString(",", strings);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, Stream<String> strings) {
        try {
            return strings.collect(Collectors.joining(delimiter));
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, String... strings) {
        try {
            return getCombineString(delimiter, Arrays.asList(strings));
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets combine string.
     *
     * @param delimiter the delimiter
     * @param strings   the strings
     * @return the combine string
     */
    public static String getCombineString(String delimiter, List<String> strings) {
        try {
            return getCombineString(delimiter, strings.stream());
        } catch (Exception ex) {
            throw ex;
        }
    }

}
