package com.awzsu.qa.dao;

import com.awzsu.qa.pojo.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReplyDao extends JpaRepository<Reply,String>,JpaSpecificationExecutor<Reply> {
    Page<Reply> findByQuestionid(String questionid, Pageable pageable);
}
