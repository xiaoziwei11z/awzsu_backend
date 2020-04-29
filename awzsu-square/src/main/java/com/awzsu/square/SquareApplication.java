package com.awzsu.square;

import com.awzsu.common.utils.IdWorker;
import com.awzsu.common.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SquareApplication {
    public static void main(String[] args) {
        SpringApplication.run(SquareApplication.class);
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
