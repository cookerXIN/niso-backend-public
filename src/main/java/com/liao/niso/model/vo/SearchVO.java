package com.liao.niso.model.vo;


import com.liao.niso.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liaoguixin
 * @date 2023/6/6
 * @apiNote
 */
@Data
public class SearchVO implements Serializable {

    private static final long serialVersionUID = -708910218656150160L;

    private List<UserVO> userVOList;

    private List<PostVO> postVOList;

    private List<Picture> pictureList;

    private List<VideoVo> videoVoList;

    private List<?> dataList;
}
