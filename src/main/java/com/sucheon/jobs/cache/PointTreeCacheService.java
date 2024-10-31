package com.sucheon.jobs.cache;

import com.github.jesse.l2cache.spring.biz.AbstractCacheService;
import com.sucheon.jobs.event.PointData;
import com.sucheon.jobs.event.PointTree;

import java.util.List;
import java.util.Map;

/**
 * 存点位树编号和点位数据的映射关系
 */
public class PointTreeCacheService  extends AbstractCacheService<String, List<PointTree>> {


    public static final String POINT_TREE_CACHE = "pointTreeCache";

    @Override
    public String getCacheName() {
        return POINT_TREE_CACHE;
    }

    @Override
    public List<PointTree> queryData(String s) {
        return null;
    }

    @Override
    public String buildCacheKey(String key) {
        return super.buildCacheKey(key);
    }

    @Override
    public Map<String, List<PointTree>> queryDataList(List<String> list) {
        return null;
    }

    @Override
    public List<PointTree> get(String key) {
        return super.get(key);
    }

    @Override
    public List<PointTree> put(String key, List<PointTree> value) {
        return super.put(key, value);
    }

    @Override
    public void evict(String key) {
        super.evict(key);
    }
}
