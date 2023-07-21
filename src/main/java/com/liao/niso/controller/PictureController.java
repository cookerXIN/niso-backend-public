package com.liao.niso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.niso.common.BaseResponse;
import com.liao.niso.common.ErrorCode;
import com.liao.niso.common.ResultUtils;
import com.liao.niso.exception.ThrowUtils;
import com.liao.niso.model.dto.picture.PictureQueryRequest;
import com.liao.niso.model.entity.Picture;
import com.liao.niso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片控制器接口
 *
 * @author liaoguixin
 * @date 2023/7/19
 */
@RestControllerAdvice
@Slf4j
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private PictureService pictureService;

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long pageSize = pictureQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();

        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);

        return ResultUtils.success(picturePage);
    }
}
