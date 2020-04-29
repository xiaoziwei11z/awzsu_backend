package com.awzsu.square.dao;

import com.awzsu.square.pojo.Speech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpeechDao extends MongoRepository<Speech,String> {
    public Page<Speech> findByParentid(String parentid, PageRequest pageRequest);
    public List<Speech> findByParentid(String parentid);

    Page<Speech> findByUserid(String userid, Pageable pageable);
}
