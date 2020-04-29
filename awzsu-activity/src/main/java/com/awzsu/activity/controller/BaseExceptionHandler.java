package com.awzsu.activity.controller;

import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        log.error("Exception:",e);
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }

}
