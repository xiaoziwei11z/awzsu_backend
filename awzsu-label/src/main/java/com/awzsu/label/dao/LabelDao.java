package com.awzsu.label.dao;

import com.awzsu.label.pojo.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface LabelDao extends JpaRepository<Label,String>,JpaSpecificationExecutor<Label> {
    public List<Label> findByState(String state);

    @Query(nativeQuery = true,
            value = "SELECT id,labelname,createtime,updatetime,fans,state FROM tb_label WHERE state=?1",
            countQuery = "SELECT COUNT(id) FROM tb_label WHERE state=?")
    public Page<Label> pageFindByState(String state, Pageable pageable);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_label SET state='0' WHERE id=?")
    public void deleteStateById(String id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_label SET labelname=?2,updatetime=?3 WHERE id=?1")
    public void updateById(String id, String labelname, Date date);

    @Query(nativeQuery = true,value = "SELECT a.id,a.labelname,a.createtime,a.updatetime,a.fans,a.state " +
            "FROM tb_label a,tb_label_user b WHERE a.state='1' AND a.id=b.labelid AND b.userid=?1")
    List<Label> searchFollow(String userid);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_label_user(labelid,userid) VALUES(?1,?2)")
    void follow(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM tb_label_user WHERE labelid=?1 AND userid=?2")
    void cancelFollow(String id, String userid);
}
