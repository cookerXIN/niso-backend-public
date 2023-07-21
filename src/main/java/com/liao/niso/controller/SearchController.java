package com.liao.niso.controller;


import com.liao.niso.common.BaseResponse;
import com.liao.niso.common.ResultUtils;
import com.liao.niso.manager.SearchFacade;
import com.liao.niso.model.dto.serach.SearchRequest;
import com.liao.niso.model.vo.SearchVO;
import com.liao.niso.service.PictureService;
import com.liao.niso.service.PostService;
import com.liao.niso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author liaoguixin
 * @date 2023/6/6
 * @apiNote
 */
@RestController
@Slf4j
@RequestMapping("/search")
public class SearchController {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

    //@PostMapping("/all")
    //public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
    //    String searchText = searchRequest.getSearchText();
    //    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
    //
    //    UserQueryRequest userQueryRequest = new UserQueryRequest();
    //    userQueryRequest.setUserName(searchText);
    //    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
    //
    //    PostQueryRequest postQueryRequest = new PostQueryRequest();
    //    postQueryRequest.setSearchText(searchText);
    //    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
    //
    //    SearchVO searchVO = new SearchVO();
    //    searchVO.setUserVOList(userVOPage.getRecords());
    //    searchVO.setPostVOList(postVOPage.getRecords());
    //    searchVO.setPictureList(picturePage.getRecords());
    //    return ResultUtils.success(searchVO);
    //}

    //@PostMapping("/all")
    //public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
    //    String searchText = searchRequest.getSearchText();
    //    并发搜索 提高搜索性能
    //    CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
    //        UserQueryRequest userQueryRequest = new UserQueryRequest();
    //        userQueryRequest.setUserName(searchText);
    //        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
    //        return userVOPage;
    //    });
    //
    //    CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
    //        PostQueryRequest postQueryRequest = new PostQueryRequest();
    //        postQueryRequest.setSearchText(searchText);
    //        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
    //        return postVOPage;
    //    });
    //
    //    CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
    //        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
    //        return picturePage;
    //    });
    //
    //    CompletableFuture.allOf(userTask, postTask, pictureTask).join();
    //    try {
    //        Page<UserVO> userVOPage = userTask.get();
    //        Page<PostVO> postVOPage = postTask.get();
    //        Page<Picture> picturePage = pictureTask.get();
    //        SearchVO searchVO = new SearchVO();
    //        searchVO.setUserVOList(userVOPage.getRecords());
    //        searchVO.setPostVOList(postVOPage.getRecords());
    //        searchVO.setPictureList(picturePage.getRecords());
    //
    //        return ResultUtils.success(searchVO);
    //    } catch (Exception e) {
    //        log.error("查询异常", e);
    //        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
    //    }
    //}
}
