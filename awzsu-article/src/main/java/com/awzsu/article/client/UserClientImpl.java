package com.awzsu.article.client;

import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.ERROR,"用户微服务熔断");
    }
}
