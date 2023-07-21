package com.liao.niso.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.niso.model.entity.Picture;


/**
 * 图片服务层
 *
 * @author liaoguixin
 * @date 2023/6/6
 * @apiNote
 */
public interface PictureService {
    /**
     * 搜索图片
     * @param searchText 搜索关键词
     * @param pageNum 搜索页面数目
     * @param pageSize 搜索页大小
     * @return 搜索结果图片列表
     */
    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);
}
