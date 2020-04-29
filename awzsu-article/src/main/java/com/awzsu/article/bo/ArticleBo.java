package com.awzsu.article.bo;

import com.awzsu.article.pojo.Article;
import lombok.Data;

@Data
public class ArticleBo extends Article {
    private String labelname;
    private String username;
}
