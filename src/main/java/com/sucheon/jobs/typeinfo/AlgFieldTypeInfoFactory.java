package com.sucheon.jobs.typeinfo;

import com.sucheon.jobs.event.AlgFieldList;
import com.sucheon.jobs.event.PointTree;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AlgFieldTypeInfoFactory extends TypeInfoFactory<AlgFieldList> {

    @Override
    public TypeInformation<AlgFieldList> createTypeInfo(Type type, Map<String, TypeInformation<?>> map) {
        return Types.POJO(AlgFieldList.class, new HashMap<String, TypeInformation<?>>() { {
            put("key", Types.STRING);
            put("pointId", Types.STRING);
            put("instanceId", Types.STRING);
            put("group", Types.STRING);
        } } );
    }
}
