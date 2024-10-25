package com.sucheon.jobs.typeinfo;

import com.sucheon.jobs.event.PointData;
import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 将kafka接受到的pojo对象进行类型兼容
 */
public class KafkaTypeInfoFactory extends TypeInfoFactory<PointData> {
    @Override
    public TypeInformation<PointData> createTypeInfo(
            Type t, Map<String, TypeInformation<?>> genericParameters) {

        return Types.POJO(PointData.class, new HashMap<String, TypeInformation<?>>() { {
            put("pageNum", Types.INT);
            put("pageBatchID", Types.STRING);
            put("time", Types.LONG);
            put("deviceTimestamp", Types.SQL_TIMESTAMP);
            // ...
        } } );
    }
}
