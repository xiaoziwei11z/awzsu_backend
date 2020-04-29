package com.awzsu.square.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.utils.IdWorker;
import com.awzsu.square.dao.SpeechDao;
import com.awzsu.square.pojo.Speech;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SquareService {
    @Autowired
    private SpeechDao speechDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(Speech speech,String userid,String username) {
        speech.set_id(idWorker.nextId()+"");
        speech.setUserid(userid);
        speech.setUsername(username);
        speech.setVisits(0);
        speech.setThumbups(0);
        speech.setComments(0);
        speech.setCreatetime(new Date());
        speech.setState("1");

        if(speech.getParentid()!=null && !speech.getParentid().isEmpty() && !speech.getParentid().equals("0")){
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(speech.getParentid()));
            Update update = new Update();
            update.inc("comments",1);
            mongoTemplate.updateFirst(query,update,"speech");
        }

        speechDao.save(speech);
    }

    public PageResult<Speech> searchByParentid(int page, int size, String parentid) {
        PageRequest pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Speech> pageList = speechDao.findByParentid(parentid,pageRequest);
        return new PageResult<Speech>(pageList.getTotalElements(),pageList.getContent());
    }

    public Speech findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.inc("visits",1);
        mongoTemplate.updateFirst(query,update,"speech");
        return speechDao.findById(id).get();
    }

    public void deleteById(String id,String parentId) {
        List<Speech> commentList = speechDao.findByParentid(id);
        speechDao.deleteById(id);
        speechDao.deleteAll(commentList);

        if(!parentId.equals("0")){
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(parentId));
            Update update = new Update();
            update.inc("comments",-1);
            mongoTemplate.updateFirst(query,update,"speech");
        }
    }

    public void thumbup(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.inc("thumbups",1);
        mongoTemplate.updateFirst(query,update,"speech");
    }

    public PageResult<Speech> searchByUserid(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Speech> pageSpeech = speechDao.findByUserid(userid,pageable);
        return new PageResult<Speech>(pageSpeech.getTotalElements(),pageSpeech.getContent());
    }
}
