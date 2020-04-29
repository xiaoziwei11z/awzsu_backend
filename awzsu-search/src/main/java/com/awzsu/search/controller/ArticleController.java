package com.awzsu.search.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.search.pojo.Article;
import com.awzsu.search.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 根据关键字搜索
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/index/{keyword}/{page}/{size}")
    public Result searchIndex(@PathVariable String keyword,@PathVariable int page,@PathVariable int size){
        PageResult<Article> pageResult = articleService.searchIndex(keyword,page,size);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }
}
