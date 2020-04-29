package com.awzsu.activitycrawler.task;

import com.awzsu.activitycrawler.pipeline.ActivityDBPipeline;
import com.awzsu.activitycrawler.processor.ActivityProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Component
@Slf4j
public class ActivityTask {
    @Autowired
    private ActivityProcessor activityProcessor;
    @Autowired
    private ActivityDBPipeline activityDBPipeline;
    @Autowired
    private RedisScheduler redisScheduler;

    @Scheduled(cron = "0 * * * * ?")
    public void pythonTask(){
        log.info("爬取活动");
        Spider spider = Spider.create(activityProcessor);
        spider.addUrl("https://huiyi.csdn.net/activity/lists?&tag_id=33");
        spider.addPipeline(activityDBPipeline);
        spider.setScheduler(redisScheduler);
        spider.start();
    }
}
