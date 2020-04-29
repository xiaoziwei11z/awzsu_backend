package com.awzsu.user.dao;

import com.awzsu.user.pojo.Friends;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FriendsDao extends JpaRepository<Friends,String>,JpaSpecificationExecutor<Friends>{
    Page<Friends> findByUseridNotIn(List<String> ids, Pageable pageable);
}
