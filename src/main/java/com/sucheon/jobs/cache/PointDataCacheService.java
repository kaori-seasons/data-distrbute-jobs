package com.sucheon.jobs.cache;

import com.github.jesse.l2cache.spring.biz.AbstractCacheService;
import com.sucheon.jobs.event.PointData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * 用于算法回传结果失败的情况，
 * 以及用户在数据统一服务建完code没有及时从算法侧的输出结果加载的场景
 */
@Component
public class PointDataCacheService extends AbstractCacheService<String, PointData> {

    public static final String CACHE_NAME = "pointDataCache";

    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }

    @Override
    public String buildCacheKey(String pointId) {

        if (null == pointId){
            return null;
        }

        return super.buildCacheKey(pointId);
    }

    @Override
    public PointData queryData(String integer) {
        return null;
    }

    @Override
    public Map<String, PointData> queryDataList(List<String> list) {
        return null;
    }


    @Override
    public PointData put(String key, PointData value) {
        return super.put(key, value);
    }
}
