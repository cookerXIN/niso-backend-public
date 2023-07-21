package com.liao.niso.datasource;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.Retryer;
import com.liao.niso.common.ErrorCode;
import com.liao.niso.exception.BusinessException;
import com.liao.niso.model.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频源数据源接口
 *
 * @author liaoguixin
 * @date 2023/7/19
 */

@Service
@Slf4j
public class VideoDataSource implements DataSource<VideoVo> {
    @Resource
    private Retryer<String> retryer;

    @Override
    public Page<VideoVo> doSearch(String searchText, long pageNum, long pageSize) {
        // 获取cookie信息
        String url1 = "https://www.bilibili.com/";
        // 通过bilibili搜索视频数据
        String url2 = String.format("https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=%s",searchText);

        HttpCookie cookie = HttpRequest.get(url1).execute().getCookie("buvid3");

        String body = null;
        try {
            // 通过重试机制实现获取视频源信息 重试超过3次则停止
            body = retryer.call(() -> HttpRequest.get(url2)
                    .cookie(cookie)
                    .execute().body());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"重试失败");
        }

        // 根据获取的信息 封装搜索视频源列表对象
        Map map = JSONUtil.toBean(body, Map.class);
        Map data = (Map)map.get("data");
        JSONArray videoList = (JSONArray) data.get("result");
        Page<VideoVo> page = new Page<>(pageNum,pageSize);
        List<VideoVo> videoVoList = new ArrayList<>();
        for(Object video:videoList){
            JSONObject tempVideo = (JSONObject)video;
            VideoVo videoVo = new VideoVo();
            videoVo.setUpic(tempVideo.getStr("upic"));
            videoVo.setAuthor(tempVideo.getStr("author"));
            videoVo.setPubdate(tempVideo.getInt("pubdate"));
            videoVo.setArcurl(tempVideo.getStr("arcurl"));
            videoVo.setPic("http:"+tempVideo.getStr("pic"));
            videoVo.setTitle(tempVideo.getStr("title"));
            videoVo.setDescription(tempVideo.getStr("description"));
            videoVoList.add(videoVo);
            if(videoVoList.size()>=pageSize){
                break;
            }
        }
        page.setRecords(videoVoList);
        return page;
    }
}
