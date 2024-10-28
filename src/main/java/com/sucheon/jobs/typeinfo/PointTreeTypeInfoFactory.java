package com.sucheon.jobs.typeinfo;

import com.sucheon.jobs.event.AlgFieldList;
import com.sucheon.jobs.event.PointData;
import com.sucheon.jobs.event.PointTree;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pointtree与flink类型兼容
 */
public class PointTreeTypeInfoFactory extends TypeInfoFactory<PointTree> {

    @Override
    public TypeInformation<PointTree> createTypeInfo(Type type, Map<String, TypeInformation<?>> map) {
        return Types.POJO(PointTree.class, new HashMap<String, TypeInformation<?>>() { {
            put("code", Types.STRING);
            put("fieldList", TypeInformation.of(new TypeHint<Tuple4<String, String, String, String>>(){}));
            put("timestamp", Types.LONG);
            put("topicList", TypeInformation.of(new TypeHint<String>(){}));
            put("algGroup", Types.STRING);
        } } );
    }
}
