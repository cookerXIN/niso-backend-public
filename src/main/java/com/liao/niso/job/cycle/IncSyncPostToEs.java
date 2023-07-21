package com.liao.niso.job.cycle;


import com.liao.niso.esdao.PostEsDao;
import com.liao.niso.mapper.PostMapper;
import com.liao.niso.model.dto.post.PostEsDTO;
import com.liao.niso.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 增量同步帖子到 es
 *
 * @author liaoguixin
 * @date 2023/7/16
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncPostToEs {


    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;

    /**
     * 设置线程池参数
     */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto)
                .collect(Collectors.toList());

        // 分批次同步
        final int pageSize = 50;
        int total = postEsDTOList.size();
        log.info("IncSyncPostToEs start, total {}", total);
        // 创建线程池并提交同步任务
        ExecutorService executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            // 获取每批次的帖子列表
            List<PostEsDTO> postEsDTOItemList =  postEsDTOList.subList(i, end);

            // 提交异步任务进行数据同步
            Future<?> future = executorService.submit(() -> {
                postEsDao.saveAll(postEsDTOItemList);
            });
            futures.add(future);

        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Failed to execute sync task. Retrying in 10 seconds...", e);
                retryTask(future); // 重试任务
            }
        }

        // 关闭线程池
        executorService.shutdown();
        log.info("IncSyncPostToEs end, total {}", total);
    }


    // 执行失败重试
    private void retryTask(Future<?> future) {
        int retryCount = 0;
        boolean success = false;

        while (retryCount < 3 && !success) {
            try {
                Thread.sleep(10000); // 延时10秒
                future.get(); // 再次尝试等待任务完成
                success = true; // 任务成功执行
            } catch (InterruptedException ex) {
                log.error("Thread sleep interrupted", ex);
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                log.error("Failed to execute sync task. Retrying in 10 seconds...", ex);
                retryCount++;
            }
        }

        if (!success) {
            log.error("Failed to execute sync task after 3 retries. Writing error log to file...");
            // 将错误信息写入日志文件
            log.info(future.toString());
            // 这里使用你喜欢的日志框架，例如Log4j、Logback等
            // 可以将错误信息写入特定的日志文件，方便后续查看和分析
        }
    }
}
