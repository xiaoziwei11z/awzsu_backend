package com.awzsu.label;

import com.awzsu.common.utils.IdWorker;
import com.awzsu.common.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class LabelApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabelApplication.class);
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
