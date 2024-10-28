package com.sucheon.jobs.typeinfo;

import com.sucheon.jobs.event.AlgFieldList;
import com.sucheon.jobs.event.EventBean;
import com.sucheon.jobs.event.PointTree;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EventBeanTypeInfoFactory extends TypeInfoFactory<EventBean> {

    @Override
    public TypeInformation<EventBean> createTypeInfo(Type type, Map<String, TypeInformation<?>> map) {
        return Types.POJO(EventBean.class, new HashMap<String, TypeInformation<?>>() { {
            put("pointId", Types.STRING);
            put("deviceChannel", Types.STRING);
            put("deviceTimestamp", Types.STRING);
            put("batchId", Types.LIST(Types.STRING));
            put("origin", Types.STRING);
            put("code", Types.STRING);
            put("topicList", Types.GENERIC(String.class));
        } } );
    }
}
