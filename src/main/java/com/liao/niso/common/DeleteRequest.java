package com.liao.niso.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author liaoguixin
 * @date 2023/7/16
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}