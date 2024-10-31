package com.sucheon.jobs.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sucheon.jobs.constant.CommonConstant;
import com.sucheon.jobs.event.DistrbutePointData;
import com.sucheon.jobs.event.PointData;
import com.sucheon.jobs.event.RuleMatchResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectUtils {


    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    private static Pattern linePattern = Pattern.compile("\\_(\\w)");

    /**
     * 比较算法控制台配置的数据字段是否在边缘端数据中出现过
     * @param fieldNames
     * @param data
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> pointCompare(List<String> fieldNames, PointData data) throws IllegalAccessException {

        Class<?> clazz = data.getClass();

        Field[] fields = clazz.getDeclaredFields(); // 获取所有字段
        Map<String, Object> fieldMap = new HashMap<>();

        for (Field field : fields) {
            if (!field.isSynthetic()) { // 不处理合成字段（如编译器自动生成的）
                String name = field.getName(); // 获取字段名
                field.setAccessible(true);
                Object value = field.get(data);

                fieldMap.put(name, value); // 将字段名和描述存入 map
            }

        }
        Map<String, String> result = new HashMap<>();
        for (String fieldName: fieldNames) {
            if (!Objects.isNull(fieldMap.get(fieldName))){
                Object value = fieldMap.get(fieldName);
                result.put(fieldName, (String) value);
            }
        }

        return result;
    }


    public static <T> String appendSinkStr(T data) throws IllegalAccessException, JsonProcessingException {
        Class<?> clazz = data.getClass();
        Field[] fields = clazz.getDeclaredFields(); // 获取所有字段
        Map<String, Object> fieldMap = new HashMap<>();
        for (int i =0; i< fields.length;i++) {
            Field field = fields[i];
            // 不处理合成字段（如编译器自动生成的）
            if (field.isSynthetic()){
                continue;
            }

            String name = humbleToCase(field); // 获取字段名

            //去除中间设置的topic数据
            if (name.equals("topic")){
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(data);
            fieldMap.put(name, value);

        }
        String result = CommonConstant.objectMapper.writeValueAsString(fieldMap);
        return result;
    }


    /**
     * 如果JsonProperty有配置下划线，则替换
     * @param field
     * @return
     */
    public static String humbleToCase(Field field){
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations){
            String currentAnnotationName = annotation.annotationType().getSimpleName();
            if (!currentAnnotationName.equals("JsonProperty")){
                continue;
            }else {
                Matcher matcher = humpPattern.matcher(field.getName());
                StringBuffer sb = new StringBuffer();
                while (matcher.find()){
                    matcher.appendReplacement(sb, "\\_" + matcher.group(0).toLowerCase());
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
        return field.getName();
    }

}
