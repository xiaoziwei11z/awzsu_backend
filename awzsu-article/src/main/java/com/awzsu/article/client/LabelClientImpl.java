package com.awzsu.article.client;

import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.ERROR,"标签微服务熔断");
    }

    @Override
    public Result findAll() {
        return new Result(false, StatusCode.ERROR,"标签微服务熔断");
    }
}
