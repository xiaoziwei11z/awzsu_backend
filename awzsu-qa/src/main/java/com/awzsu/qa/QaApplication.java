package com.awzsu.qa;

import com.awzsu.common.utils.IdWorker;
import com.awzsu.common.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class QaApplication {
    public static void main(String[] args) {
        SpringApplication.run(QaApplication.class);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil();
    }
}
