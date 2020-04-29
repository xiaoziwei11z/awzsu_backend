package com.awzsu.user.dao;

import com.awzsu.user.pojo.Friends;
import com.awzsu.user.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<User,String>,JpaSpecificationExecutor<User> {
    public User findByPhoneAndRoleid(String phone,String roleid);

    public User findByPhoneAndRoleidNot(String phone,String roleid);

    @Query(nativeQuery = true,value = "SELECT permissionid FROM tb_role_permission WHERE roleid=?")
    public List<String> findPermissionByRoleid(String roleid);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_follow(followedid,userid,islike) VALUES(?1,?2,'1')")
    void follow(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO tb_follow(followedid,userid,islike) VALUES(?1,?2,'0')")
    void dislike(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM  tb_follow WHERE followedid=?1 AND userid=?2 AND islike='1'")
    void cancelFollow(String id, String userid);

    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM tb_follow WHERE followedid=?1 AND userid=?2 AND islike='1'")
    Integer findIsFollow(String id, String userid);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_user SET follows=follows+?2 WHERE id=?1")
    void updateFollows(String id, Integer follows);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE tb_user SET fans=fans+?2 WHERE id=?1")
    void updateFans(String id, Integer fans);

    @Query(nativeQuery = true,value = "SELECT a.id,a.roleid,a.phone,a.password,a.image,a.name,a.detail,a.sex,a.birthday,a.address,a.school,a.company,a.site,a.createtime,a.updatetime,a.follows,a.fans " +
            "FROM tb_user a,tb_follow b WHERE a.id=b.followedid AND b.userid=?1 AND b.islike='1'")
    Page<User> searchFollow(String userid, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT a.id,a.roleid,a.phone,a.password,a.image,a.name,a.detail,a.sex,a.birthday,a.address,a.school,a.company,a.site,a.createtime,a.updatetime,a.follows,a.fans " +
            "FROM tb_user a,tb_follow b WHERE a.id=b.userid AND b.followedid=?1 AND b.islike='1'")
    Page<User> searchFans(String userid, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT followedid FROM tb_follow WHERE userid=?1")
    List<String> findRelatedId(String userid);

}
