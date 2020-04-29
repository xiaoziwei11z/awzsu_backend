package com.awzsu.search.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.search.dao.ArticleDao;
import com.awzsu.search.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;

    public void saveAll(List<Article> articleList) {
        articleDao.saveAll(articleList);
    }

    public void save(Article article) {
        articleDao.save(article);
    }

    public void deleteById(String id) {
        articleDao.deleteById(id);
    }

    public PageResult<Article> searchIndex(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1,size);
        Page<Article> pageArticle = articleDao.findByTitleOrContent(keyword,keyword,pageRequest);
        return new PageResult<Article>(pageArticle.getTotalElements(),pageArticle.getContent());
    }
}
