package com.liao.niso.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片对象
 *
 * @author liaoguixin
 * @date 2023/7/19
 */
@Data
public class Picture implements Serializable {

    private static final long serialVersionUID = 4473334845302675835L;
    private String title;
    private String url;
}
