package com.awzsu.search.dao;

import com.awzsu.search.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleDao extends ElasticsearchRepository<Article,String> {
    Page<Article> findByTitleOrContent(String title, String content, Pageable pageable);
}
