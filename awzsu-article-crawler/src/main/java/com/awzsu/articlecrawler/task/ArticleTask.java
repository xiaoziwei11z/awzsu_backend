package com.awzsu.articlecrawler.task;

import com.awzsu.articlecrawler.pipeline.ArticleDBPipeline;
import com.awzsu.articlecrawler.processor.ArticleProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Component
@Slf4j
public class ArticleTask {
    @Autowired
    private ArticleProcessor articleProcessor;
    @Autowired
    private ArticleDBPipeline articleDBPipeline;
    @Autowired
    private RedisScheduler redisScheduler;

    //0秒 0分 0时 每天 每月 放弃星期（星期和月不可重复）
    @Scheduled(cron = "0 0 0 * * ?")
    public void AITask(){
        log.info("爬取AI文章");
        Spider spider = Spider.create(articleProcessor);
        spider.addUrl("https://ai.csdn.net/");
        articleDBPipeline.setLabelid("4");
        spider.addPipeline(articleDBPipeline);
        spider.setScheduler(redisScheduler);
        spider.start();
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void JavaTask(){
        log.info("爬取Java文章");
        Spider spider = Spider.create(articleProcessor);
        spider.addUrl("https://www.csdn.net/nav/java");
        articleDBPipeline.setLabelid("1");
        spider.addPipeline(articleDBPipeline);
        spider.setScheduler(redisScheduler);
        spider.start();
    }

    @Scheduled(cron = "0 20 0 * * ?")
    public void pythonTask(){
        log.info("爬取python文章");
        Spider spider = Spider.create(articleProcessor);
        spider.addUrl("https://www.csdn.net/nav/python");
        articleDBPipeline.setLabelid("3");
        spider.addPipeline(articleDBPipeline);
        spider.setScheduler(redisScheduler);
        spider.start();
    }
}
