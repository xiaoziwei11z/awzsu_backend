package com.awzsu.search.listener;

import com.alibaba.fastjson.JSON;
import com.awzsu.search.pojo.Article;
import com.awzsu.search.service.ArticleService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RabbitListener(queues = "article_saveAll")
public class ArticleSaveAllListener {
    @Autowired
    private ArticleService articleService;

    @RabbitHandler
    public void saveAll(String articleListJson){
        List<Article> articleList = JSON.parseArray(articleListJson,Article.class);
        articleService.saveAll(articleList);
    }
}
