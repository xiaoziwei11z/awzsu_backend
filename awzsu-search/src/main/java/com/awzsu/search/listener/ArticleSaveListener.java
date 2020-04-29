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
@RabbitListener(queues = "article_save")
public class ArticleSaveListener {
    @Autowired
    private ArticleService articleService;

    @RabbitHandler
    public void save(String articleJson){
        Article article = JSON.parseObject(articleJson, Article.class);
        articleService.save(article);
    }
}
