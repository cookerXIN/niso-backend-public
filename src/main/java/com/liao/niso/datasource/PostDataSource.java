package com.liao.niso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.niso.model.dto.post.PostQueryRequest;
import com.liao.niso.model.vo.PostVO;
import com.liao.niso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子源数据源接口
 *
 * @author liaoguixin
 * @date 2023/7/19
 */

@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO>{
    @Resource
    private PostService postService;
    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        // 获取帖子查询请求
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);

        // 获取http请求
        ServletRequestAttributes servletRequestAttributes =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 获取脱敏帖子列表信息
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        return postVOPage;
    }
}
