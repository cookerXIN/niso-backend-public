package com.liao.niso.model.dto.picture;

import com.liao.niso.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 图片搜索请求
 *
 * @author liaoguixin
 * @date 2023/7/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -2135064106064171835L;

    /**
     * 搜索关键词
     */
    private String searchText;
}
