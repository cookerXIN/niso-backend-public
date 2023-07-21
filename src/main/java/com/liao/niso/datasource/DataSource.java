package com.liao.niso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 接口适配器
 * 数据源接口（新接入的数据源必须实现）
 *
 * @author liaoguixin
 * @date 2023/7/19
 */
public interface DataSource<T> {


    /**
     * 根据搜索词搜索
     *
     * @param searchText 搜索词
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
