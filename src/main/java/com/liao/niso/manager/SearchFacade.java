package com.liao.niso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.niso.common.ErrorCode;
import com.liao.niso.datasource.*;
import com.liao.niso.exception.BusinessException;
import com.liao.niso.exception.ThrowUtils;
import com.liao.niso.model.dto.post.PostQueryRequest;
import com.liao.niso.model.dto.serach.SearchRequest;
import com.liao.niso.model.dto.user.UserQueryRequest;
import com.liao.niso.model.entity.Picture;
import com.liao.niso.model.enums.SearchTypeEnum;
import com.liao.niso.model.vo.PostVO;
import com.liao.niso.model.vo.SearchVO;
import com.liao.niso.model.vo.UserVO;
import com.liao.niso.model.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索结果门面类 （门面模式）
 * 使前端只用发一次请求就可以获取所有搜索结果，不要发多次请求，减少请求次数
 * @author liaoguixin
 * @date 2023/7/19
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private VideoDataSource videoDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;


    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // 聚合搜索请求
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);
        // 搜索出所有数据
        // 通过CompletableFuture并发处理请求,增加搜索性能
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });
            CompletableFuture<Page<VideoVo>> videoTask = CompletableFuture.supplyAsync(() -> {
                Page<VideoVo> videoVoPage = videoDataSource.doSearch(searchText, current, pageSize);
                return videoVoPage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask, videoTask).join();
            SearchVO searchVO = new SearchVO();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<VideoVo> videoVOPage = videoTask.get();

                searchVO.setUserVOList(userVOPage.getRecords());
                searchVO.setPostVOList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setVideoVoList(videoVOPage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            // 搜索固定类型搜索源结果
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
