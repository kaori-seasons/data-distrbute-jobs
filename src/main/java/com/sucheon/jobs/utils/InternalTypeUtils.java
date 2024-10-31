package com.sucheon.jobs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sucheon.jobs.event.DistrbutePointData;
import com.sucheon.jobs.event.RuleMatchResult;
import com.sucheon.jobs.exception.DistrbuteException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class InternalTypeUtils {


    public static <T> byte[] transferData(T pointData){
        String result = "";
        try {
            result = ReflectUtils.appendSinkStr(pointData);
        } catch (IOException | IllegalAccessException e) {

            //todo 记录点位数据异常和时间戳
            throw new DistrbuteException("转换数据成kafka可接受的数据格式异常", e);
        }

        if (StringUtils.isNotBlank(result)) {
            return result.getBytes(StandardCharsets.UTF_8);
        }else {
            return new byte[0];
        }
    }


    public static boolean iaAllFieldsNull(Object obj){
        for (Field field: obj.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try{
                if (field.get(obj) !=null){
                    return false;
                }
            } catch (IllegalAccessException e){
                throw new DistrbuteException("判断当前对象中是否存在属性全都为空的现象, 报错原因: \n", e);
            }
        }
        return true;
    }
}
