package com.taki.cloud.common.utis;


import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.K;
import org.checkerframework.common.reflection.qual.UnknownClass;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * @ClassName JsonUtil
 * @Description json 工具类
 * @Author Long
 * @Date 2023/6/12 14:08
 * @Version 1.0
 */
@Slf4j
@UnknownClass
public class JsonUtil {


    /*** 
     * @description:  将对象序列化转为json字符串
     * @param object javaBean
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/12 14:10
     */
    @Nullable
    public static  String toJson(@Nullable Object object){

        if (object == null){
            return null;
        }
        try {
         return    getInstance().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw  Exceptions.unchecked(e);
        }

    }

    /*** 
     * @description:  将对象序列化 为 json byte 数组
     * @param object
     * @return  byte[]
     * @author Long
     * @date: 2023/6/12 14:32
     */ 
    @Nullable
    public static byte[] toJsonAsBytes(@Nullable Object object){
        if (object == null){
            return new byte[0];
        }

        try {
         return    getInstance().writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw Exceptions.unchecked(e);
        }

    }

    /*** 
     * @description:  将json 字符串 转换成 JsonNode
     * @param jsonString
     * @return  com.fasterxml.jackson.databind.JsonNode
     * @author Long
     * @date: 2023/6/12 14:33
     */ 
    public static JsonNode readTree(String jsonString){
        Objects.requireNonNull(jsonString,"jsonString is null");

        try {
            return  getInstance().readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw  Exceptions.unchecked(e);
        }
    }

    /*** 
     * @description: 将json 字符串 转为 JSONNode
     * @param inputStream
     * @return  com.fasterxml.jackson.databind.JsonNode
     * @author Long
     * @date: 2023/6/12 14:35
     */ 
    public static JsonNode readTree(InputStream inputStream){
        Objects.requireNonNull(inputStream,"InputStream in is null");

        try {
            return getInstance().readTree(inputStream);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }
    /*** 
     * @description:  将json字符串转为JsonNode
     * @param content
     * @return  com.fasterxml.jackson.databind.JsonNode
     * @author Long
     * @date: 2023/6/12 14:37
     */ 
    public static JsonNode readTree(byte[]  content){
        Objects.requireNonNull(content,"byte[] content is null");

        try {
            return getInstance().readTree(content);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }
    
    /*** 
     * @description: 将json 字符串转为 JsonNode
     * @param jsonParser
     * @return  com.fasterxml.jackson.databind.JsonNode
     * @author Long
     * @date: 2023/6/12 14:40
     */ 
    public  static JsonNode readTree(JsonParser jsonParser){
        Objects.requireNonNull(jsonParser,"jsonParser is null");

        try {
            return getInstance().readTree(jsonParser);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /*** 
     * @description: 将json byte 数组反序列化 成对象
     * @param content
     * valueType
     * @return  T
     * @author Long
     * @date: 2023/6/12 14:40
     */
    @Nullable
    public static <T> T readValue( @Nullable byte[] content,Class<T> valueType){
        if (ObjectUtils.isEmpty(content)){
            return null;
        }

        try {
          return   getInstance().readValue(content,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    /***
     * @description: 将json byte 数组反序列化 成对象
     * @param jsonString
     * valueType
     * @return  T
     * @author Long
     * @date: 2023/6/12 14:40
     */
    @Nullable
    public static <T> T readValue( @Nullable String jsonString,Class<T> valueType){
        if (StringUtils.isEmpty(jsonString)){
            return null;
        }

        try {
            return   getInstance().readValue(jsonString,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }


    /***
     * @description: 将json byte 数组反序列化 成对象
     * @param inputStream
     * valueType
     * @return  T
     * @author Long
     * @date: 2023/6/12 14:40
     */
    @Nullable
    public static <T> T readValue( @Nullable InputStream inputStream,Class<T> valueType){
        if (ObjectUtils.isEmpty(inputStream)){
            return null;
        }

        try {
            return   getInstance().readValue(inputStream,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    /***
     * @description: 将json byte 数组反序列化 成对象
     * @param bytes
     * valueType
     * @return  T
     * @author Long
     * @date: 2023/6/12 14:40
     */
    @Nullable
    public static <T> T readValue(@Nullable byte[] bytes, TypeReference<T> valueType){
        if (ObjectUtils.isEmpty(bytes)){
            return null;
        }

        try {
            return   getInstance().readValue(bytes,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <T> T readValue(@Nullable String jsonString, TypeReference<T> valueType){
        if (ObjectUtils.isEmpty(jsonString)){
            return null;
        }

        try {
            return   getInstance().readValue(jsonString,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <T> T readValue(@Nullable InputStream inputStream, TypeReference<T> valueType){
        if (ObjectUtils.isEmpty(inputStream)){
            return null;
        }

        try {
            return   getInstance().readValue(inputStream,valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <T> T readValue(@Nullable byte[] bytes, JavaType javaType){
        if (ObjectUtils.isEmpty(bytes)){
            return null;
        }

        try {
            return   getInstance().readValue(bytes,javaType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }
    @Nullable
    public static <T> T readValue(@Nullable String jsonString, JavaType javaType){
        if (ObjectUtils.isEmpty(jsonString)){
            return null;
        }

        try {
            return   getInstance().readValue(jsonString,javaType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <T> T readValue(@Nullable InputStream inputStream, JavaType javaType){
        if (ObjectUtils.isEmpty(inputStream)){
            return null;
        }

        try {
            return   getInstance().readValue(inputStream,javaType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

    }


    /*** 
     * @description:
     * @param valueClass
     * @return  com.fasterxml.jackson.databind.type.MapType
     * @author Long
     * @date: 2023/6/12 15:11
     */ 
    public static MapType getMapType(Class<?>  valueClass){

        return getMapType(String.class,valueClass);
    }

    /*** 
     * @description: 封装 map type
     * @param keyClass
     * @param valueClass
     * @return  com.fasterxml.jackson.databind.type.MapType
     * @author Long
     * @date: 2023/6/12 15:15
     */ 
    public static MapType getMapType(Class<?> keyClass,Class<?> valueClass){

        return getInstance().getTypeFactory().constructMapType(Map.class,keyClass,valueClass);
    }



    /*** 
     * @description:  封装 map type
     * @param elmentClass 集合值类型
     * @return  com.fasterxml.jackson.databind.type.CollectionLikeType
     * @author Long
     * @date: 2023/6/12 15:18
     */ 
    public static CollectionLikeType getListType(Class<?> elmentClass){
        return getInstance().getTypeFactory().constructCollectionLikeType(List.class,elmentClass);
        
    }

    /*** 
     * @description:
     * @param parametrized
    parameterClasses
     * @return  com.fasterxml.jackson.databind.JavaType
     * @author Long
     * @date: 2023/6/12 15:20
     */ 
    public  static JavaType getParametricType (Class<?>  parametrized ,Class<?> ... parameterClasses){

        return getInstance().getTypeFactory().constructParametricType(parametrized,parameterClasses);
    }


    /*** 
     * @description: 读取集合
     * @param types
     *  elmentClass
     * @return  java.util.List<T>
     * @author Long
     * @date: 2023/6/12 15:22
     */
    @Nullable
    public static <T> List<T> readList(  @Nullable byte[] types,Class<T> elementClass){
        if (ObjectUtils.isEmpty(types)){
            return Collections.emptyList();
        }

        try {
          return   getInstance().readValue(types,getListType(elementClass));
        } catch (IOException e) {
           throw  Exceptions.unchecked(e);
        }

    }
    @Nullable
    public static <T> List<T> readList(@Nullable InputStream inputStream,Class<T> elementClass){
        if (ObjectUtils.isEmpty(inputStream)){
            return Collections.emptyList();
        }

        try {
            return   getInstance().readValue(inputStream,getListType(elementClass));
        } catch (IOException e) {
            throw  Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <T> List<T> readList(@Nullable String jsonString,Class<T> elementClass){
        if (ObjectUtils.isEmpty(jsonString)){
            return Collections.emptyList();
        }

        try {
            return   getInstance().readValue(jsonString,getListType(elementClass));
        } catch (IOException e) {
            throw  Exceptions.unchecked(e);
        }

    }

    /*** 
     * @description:  读取 集合
     * @param content
     * elementClass
     * @return  java.util.Map<java.lang.String,V>
     * @author Long
     * @date: 2023/6/12 15:27
     */

    @Nullable
    public static <V> Map<String,V> readMap(String  content){

        return readMap(content,Object.class);
    }



    @Nullable
    public static <V> Map<String,V> readMap(@Nullable byte[] bytes, Class<?> valueClass){

    return readMap(bytes,String.class,valueClass);
    }


    @Nullable
    public static <V> Map<String,V> readMap(@Nullable InputStream inputStream, Class<?> valueClass){

        return readMap(inputStream,String.class,valueClass);
    }


    public static <V> Map<String,V> readMap(@Nullable String jsonString, Class<?> valueClass){

        return readMap(jsonString,String.class,valueClass);
    }

    /*** 
     * @description:  读取集合
     * @param bytes
     * @param keyClass
     * @param valueClass
     * @return  java.util.Map<java.lang.String,V>
     * @author Long
     * @date: 2023/6/12 15:30
     */ 
    @Nullable
    public static <V> Map<String,V> readMap(@Nullable byte[] bytes, Class<?> keyClass , Class<?> valueClass){
        if (ObjectUtils.isEmpty(bytes)){
            return Collections.emptyMap();
        }

        try {
            return getInstance().readValue(bytes,getMapType(keyClass,valueClass));
        } catch (IOException e) {
            throw  Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <V> Map<String,V> readMap(@Nullable InputStream inputStream, Class<?> keyClass , Class<?> valueClass){
        if (ObjectUtils.isEmpty(inputStream)){
            return Collections.emptyMap();
        }

        try {
            return getInstance().readValue(inputStream,getMapType(keyClass,valueClass));
        } catch (IOException e) {
            throw  Exceptions.unchecked(e);
        }

    }

    @Nullable
    public static <V> Map<String,V> readMap(@Nullable String content, Class<?> keyClass , Class<?> valueClass){
        if (ObjectUtils.isEmpty(content)){
            return Collections.emptyMap();
        }

        try {
            return getInstance().readValue(content,getMapType(keyClass,valueClass));
        } catch (IOException e) {
            throw  Exceptions.unchecked(e);
        }

    }


    /*** 
     * @description:  获取ObjectMapper 实例
     * @param 
     * @return  com.fasterxml.jackson.databind.ObjectMapper
     * @author Long
     * @date: 2023/6/12 14:14
     */ 
    private static ObjectMapper getInstance() {

        return JacksonHolder.INSTANCE;
    }


    private static  class JacksonHolder {
        private  static  final ObjectMapper INSTANCE = new JacksonObjectMapper();
    }

    private static class JacksonObjectMapper extends ObjectMapper {


        private static final long serialVersionUID = 1332038598718997280L;

        private static  final Locale CHINA = Locale.CHINA;

        public JacksonObjectMapper() {
            super(jsonFactory());
            super.setLocale(CHINA);
            super.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN,CHINA));
            // 单引号
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
            // 忽悠无法转换的对象
            super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
            super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.findAndRegisterModules();
        }

        public  JacksonObjectMapper(ObjectMapper src){
            super(src);
        }

        private static JsonFactory jsonFactory() {
            return JsonFactory.builder()
                    // 可解析反斜杆引用的所以字符
                    .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true)
                    // 允许JSON字符串包含非引号控制字符（值小于32）
                    .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS,true)
                    .build();


        }

        @Override
        public ObjectMapper copy() {
            return new JacksonObjectMapper(this);
        }
    }

    public static Map<String,Object> toMap(String jsonString){

        try {
            return getInstance().readValue(jsonString,Map.class);
        } catch (JsonProcessingException e) {
            throw  Exceptions.unchecked(e);
        }
    }


    public static <T> Map<String,T> toMap(String jsonString,Class<T> valueTypeRef){

        try {
            Map<String,Map<String,Object>> map = getInstance().readValue(jsonString, new TypeReference<Map<String, Map<String, Object>>>() {
            });

            Map<String,T> result = new HashMap<>(16);

            for (Map.Entry<String, Map<String,Object>> entry : map.entrySet()) {
                result.put(entry.getKey(),toPojo(entry.getValue(),valueTypeRef));
            }

            return result;

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }

        return null;

    }


    public static  <T> T toPojo(Map fromValue,Class<T>toValueType){

        return getInstance().convertValue(fromValue,toValueType);
    }
}
