package com.awzsu.activity.dao;

import com.awzsu.activity.pojo.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface ActivityDao extends JpaRepository<Activity,String>,JpaSpecificationExecutor<Activity> {
    @Query(nativeQuery = true,value = "SELECT a.id,a.name,a.content,a.image,a.holdingtime,a.address,a.enrolltime,a.contactname,a.contactphone,a.contactemail,a.state " +
            "FROM tb_activity a,tb_activity_user b WHERE a.state='1' AND a.id=b.activityid AND b.userid=?1")
    Page<Activity> searchFollow(String userid, Pageable pageable);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_activity_user(activityid,userid) VALUES(?1,?2)")
    void follow(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM tb_activity_user WHERE activityid=?1 AND userid=?2")
    void cancelFollow(String id, String userid);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM tb_activity_user WHERE activityid=?1 AND userid=?2")
    Integer findIsFollow(String id, String userid);
}
