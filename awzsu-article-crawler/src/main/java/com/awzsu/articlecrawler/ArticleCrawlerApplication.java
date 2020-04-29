package com.awzsu.articlecrawler;

import com.awzsu.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@SpringBootApplication
@EnableScheduling
public class ArticleCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleCrawlerApplication.class);
    }

    @Value("${spring.redis.host}")
    private String redis_host;

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }

    @Bean
    public RedisScheduler redisScheduler(){
        return new RedisScheduler(redis_host);
    }
}
