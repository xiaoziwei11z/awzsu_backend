package com.awzsu.activitycrawler;

import com.awzsu.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@SpringBootApplication
@EnableScheduling
public class ActivityCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivityCrawlerApplication.class);
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
