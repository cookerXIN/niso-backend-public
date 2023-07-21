package com.liao.niso.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频信息
 *
 * @author liaoguixin
 * @date 2023/7/19
 */
@Data
public class VideoVo implements Serializable {
    private String arcurl;
    private String pic;
    private String title;
    private String description;
    private String author;
    private Integer pubdate;
    private String upic;
    private static final long serialVersionUID = 7037843325406822290L;
}
