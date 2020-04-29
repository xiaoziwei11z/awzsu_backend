package com.awzsu.qa.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.utils.IdWorker;
import com.awzsu.qa.dao.QuestionDao;
import com.awzsu.qa.pojo.Question;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private IdWorker idWorker;

    public PageResult<Question> search(int page, int size, Map searchMap) {
        if(searchMap.containsKey("labelid") && searchMap.get("labelid").equals("0")){
            searchMap.remove("labelid");
        }

        PageRequest pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"replytime"));
        Page<Question> pageList = questionDao.findAll(new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("labelid"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("labelid").as(String.class), searchMap.get("labelid"));
                    list.add(predicate);
                }
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("state"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("state").as(String.class), searchMap.get("state"));
                    list.add(predicate);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageRequest);
        return new PageResult<Question>(pageList.getTotalElements(),pageList.getContent());
    }

    public void save(Question question, String id, String name) {
        question.setId(idWorker.nextId()+"");
        question.setUserid(id);
        question.setUsername(name);
        question.setVisits(0);
        question.setReplys(0);
        Date date = new Date();
        question.setCreatetime(date);
        question.setUpdatetime(date);
        question.setReplyname("");
        question.setReplytime(date);
        question.setState("1");
        questionDao.save(question);
    }

    public Question findById(String id) {
        questionDao.updateVisitsById(id);
        return questionDao.findById(id).get();
    }

    public void done(String id) {
        questionDao.updateStateById(id);
    }

    public PageResult<Question> searchByUserid(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Question> pageQuestion = questionDao.findByUserid(userid,pageable);
        return new PageResult<Question>(pageQuestion.getTotalElements(),pageQuestion.getContent());
    }

    public PageResult<Question> searchFollow(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Question> pageQuestion = questionDao.searchFollow(userid,pageable);
        return new PageResult<Question>(pageQuestion.getTotalElements(),pageQuestion.getContent());
    }

    public void follow(String id, String userid) {
        questionDao.follow(id,userid);
    }

    public void cancelFollow(String id, String userid) {
        questionDao.cancelFollow(id,userid);
    }

    public Boolean findIsFollow(String id, String userid) {
        int count = questionDao.findIsFollow(id,userid);
        if(count == 0){
            return false;
        }else{
            return true;
        }
    }
}
