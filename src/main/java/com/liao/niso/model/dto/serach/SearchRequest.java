package com.liao.niso.model.dto.serach;


import com.liao.niso.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author liaoguixin
 * @date 2023/6/6
 * @apiNote
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -3548563049140954309L;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;
}
