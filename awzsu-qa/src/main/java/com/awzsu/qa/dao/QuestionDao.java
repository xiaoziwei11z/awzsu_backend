package com.awzsu.qa.dao;

import com.awzsu.qa.pojo.Question;
import com.awzsu.qa.pojo.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface QuestionDao extends JpaRepository<Question,String>,JpaSpecificationExecutor<Question> {
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_question SET visits=visits+1 WHERE id=?")
    void updateVisitsById(String id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_question SET replys=replys+1,replyname=?1,replytime=?2 WHERE id=?3")
    void updateByReply(String name,Date date,String questionid);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_question SET state='2' WHERE id=?")
    void updateStateById(String id);

    Page<Question> findByUserid(String userid, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT a.id,a.labelid,a.userid,a.username,a.title,a.content,a.visits,a.replys,a.createtime,a.updatetime,a.replyname,a.replytime,a.state " +
            "FROM tb_question a,tb_question_user b WHERE a.id=b.questionid AND b.userid=?1")
    Page<Question> searchFollow(String userid, Pageable pageable);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_question_user(questionid,userid) VALUES(?1,?2)")
    void follow(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM tb_question_user WHERE questionid=?1 AND userid=?2")
    void cancelFollow(String id, String userid);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM tb_question_user WHERE questionid=?1 AND userid=?2")
    Integer findIsFollow(String id, String userid);
}
