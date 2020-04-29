package com.awzsu.article.dao;

import com.awzsu.article.pojo.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentDao extends MongoRepository<Comment,String> {
    Page<Comment> findByArticleid(String articleid, Pageable pageable);
}
