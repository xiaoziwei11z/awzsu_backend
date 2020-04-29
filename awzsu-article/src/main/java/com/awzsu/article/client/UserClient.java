package com.awzsu.article.client;

import com.awzsu.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "awzsu-user",fallback = UserClientImpl.class)
public interface UserClient {
    @GetMapping("/user/{id}")
    public Result findById(@PathVariable("id") String id);
}
