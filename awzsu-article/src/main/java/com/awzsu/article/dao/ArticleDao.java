package com.awzsu.article.dao;

import com.awzsu.article.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ArticleDao extends JpaRepository<Article,String>,JpaSpecificationExecutor<Article>{
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_article SET state=?2 WHERE id=?1")
    public void updateState(String id,String state);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_article SET visits=visits+1 WHERE id=?")
    void UpdateVisitsById(String id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_article SET comments=comments+1 WHERE id=?")
    void updateCommentsById(String articleid);

    List<Article> findByState(String state);

    Page<Article> findByUserid(String userid, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT a.id,a.labelid,a.userid,a.title,a.content,a.createtime,a.updatetime,a.visits,a.thumbups,a.comments,a.state " +
            "FROM tb_article a,tb_article_user b WHERE a.state='2' AND a.id=b.articleid AND b.userid=?1")
    Page<Article> searchFollow(String userid, Pageable pageable);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_article_user(articleid,userid,followtime) VALUES(?1,?2,?3)")
    void follow(String id, String userid, Date followtime);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM tb_article_user WHERE articleid=?1 AND userid=?2")
    void cancelFollow(String id, String userid);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM tb_article_user WHERE articleid=?1 AND userid=?2")
    Integer findIsFollow(String id, String userid);
}
