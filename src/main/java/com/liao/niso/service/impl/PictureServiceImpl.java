package com.liao.niso.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.liao.niso.common.ErrorCode;
import com.liao.niso.exception.BusinessException;
import com.liao.niso.model.entity.Picture;
import com.liao.niso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liaoguixin
 * @date 2023/6/6
 * @apiNote
 */
@Service
@Slf4j
public class PictureServiceImpl implements PictureService {

    /**
     * 搜索图片
     * @param searchText 搜索关键词
     * @param pageNum 搜索页面数目
     * @param pageSize 搜索页大小
     * @return 分页图片搜索结果
     */
    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        // 计算当前页面
        long current = (pageNum - 1) * pageSize;
        // bing链接搜索
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements newsHeadlines = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element headline : newsHeadlines) {
            // 获取图片地址
            String m = headline.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            // 获取图片标题
            String attr = headline.select(".inflnk").get(0).attr("aria-label");
            Picture picture = new Picture();
            picture.setTitle(attr);
            picture.setUrl(murl);
            pictures.add(picture);
            // 如果图片数量超过页面的距离 则停止添加
            if (pictures.size() >= pageSize) {
                break;
            }
        }
        // 分页图片搜索结果
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictures);
        return picturePage;
    }
}
