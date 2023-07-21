package com.liao.niso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.niso.model.dto.user.UserQueryRequest;
import com.liao.niso.model.entity.User;
import com.liao.niso.model.vo.UserVO;
import com.liao.niso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户源搜索接口
 *
 * @author liaoguixin
 * @date 2023/7/19
 */

@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        // 获取用户搜索请求对象
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setPageSize(pageSize);
        userQueryRequest.setCurrent(pageNum);
        // 获取脱敏后的分页搜索结果
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
}
