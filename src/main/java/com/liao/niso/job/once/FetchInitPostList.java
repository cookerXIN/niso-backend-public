package com.liao.niso.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.liao.niso.model.entity.Post;
import com.liao.niso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 初始化帖子
 *
 * @author liaoguixin
 * @date 2023/7/16
 */
// todo 取消注释开启任务
// 取消注释则每次程序运行都会开始执行一边初始化帖子列表
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {
        //https://hutool.cn/docs/index.html#/
        // 1.获取数据
        // json:请求头信息
        String json = "new_hot_flag=1&channel_name=pc_hot_word&size=20&user_name=weixin_46900694&platform=pc&imei=10_19000210280-1644911455348-731762";
        // url:请求连接 访问的链接
        String url = "https://silkroad.csdn.net/api/v2/assemble/list/channel/search_hot_word";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        // 2. json转对象 转成map对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        // 获取需要的数据列表
        JSONObject data = (JSONObject) map.get("data");
        JSONArray items = (JSONArray) data.get("items");
        // 添加到帖子列表中
        List<Post> postList = new ArrayList<>();
        for (Object item : items) {
            if (item == null) {
                continue;
            }
            JSONObject tempRecord = (JSONObject) item;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("productId"));
            JSONObject reportData = (JSONObject) tempRecord.get("reportData");
            JSONObject reportData_data = (JSONObject) reportData.get("data");
            post.setContent(reportData_data.getStr("extra"));
            String tag = "[\"" + tempRecord.getStr("recommendType") + "\"]";
            post.setTags(tag);
            post.setThumbNum(0);
            post.setFavourNum(0);
            post.setUserId(1665917368012730370L);
            post.setCreateTime(new Date());
            post.setUpdateTime(new Date());
            post.setIsDelete(0);
            postList.add(post);
        }
        // 数据入库
        boolean b = postService.saveBatch(postList);
        if (b) {
            log.info("初始化帖子列表成功，条数为{}", postList.size());
        } else {
            log.info("初始化帖子列表失败");
        }
    }
}
