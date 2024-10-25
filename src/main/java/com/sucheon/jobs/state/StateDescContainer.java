package com.sucheon.jobs.state;

import com.sucheon.jobs.event.PointTree;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.util.List;

public class StateDescContainer {


    public static MapStateDescriptor<String, List<PointTree>> ruleStateDesc
            = new MapStateDescriptor<String, List<PointTree>>("rule_broadcast_state",
            TypeInformation.of(String.class),
            TypeInformation.of(new TypeHint<List<PointTree>>() {
                @Override
                public TypeInformation<List<PointTree>> getTypeInfo() {
                    return super.getTypeInfo();
                }
            }));
}
