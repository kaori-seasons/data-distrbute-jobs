package com.sucheon.jobs.cache;

import com.github.jesse.l2cache.spring.cache.L2CacheCacheManager;
import com.sucheon.jobs.event.PointData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserCacheService {

    @Autowired
    L2CacheCacheManager cacheCacheManager;


    private static Map<String, PointData> pointDataMap = new HashMap<>();

    @Cacheable(value = "pointDataCache", key = "#pointId")
    public PointData queryPointData(String pointId){
        PointData pointData = pointDataMap.get(pointId);
        return pointData;
    }

    @CachePut(value = "pointDataCache", key = "#pointId")
    public PointData putPointData(String pointId, PointData pointData) {
        return pointData;
    }

    /**
     * 淘汰缓存
     */
    @CacheEvict(value = "pointDataCache", key = "#pointId")
    public String evictPointDataSync(String pointId) {
        return pointId;
    }


}
