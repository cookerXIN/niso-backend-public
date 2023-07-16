package com.liao.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liao.springbootinit.model.entity.Post;


import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 *
 * @author liaoguixin
 * @date 2023/7/16
 */
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

}




