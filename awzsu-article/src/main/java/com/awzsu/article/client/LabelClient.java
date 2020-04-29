package com.awzsu.article.client;

import com.awzsu.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "awzsu-label",fallback = LabelClientImpl.class)
public interface LabelClient {
    @GetMapping("/label/{id}")
    public Result findById(@PathVariable("id") String id);
    @GetMapping("/label")
    public Result findAll();
}
