package com.awzsu.activity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.awzsu.activity.dao.ActivityDao;
import com.awzsu.activity.pojo.Activity;
import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    public PageResult<Activity> search(int page, int size, Map searchMap) {
        PageRequest pageRequest = PageRequest.of(page-1,size);
        Page<Activity> pageList = activityDao.findAll(new Specification<Activity>() {
            @Override
            public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("name"))) {
                    Predicate predicate = criteriaBuilder.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%");
                    list.add(predicate);
                }
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("state"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("state").as(String.class), searchMap.get("state"));
                    list.add(predicate);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageRequest);
        return new PageResult<Activity>(pageList.getTotalElements(),pageList.getContent());
    }

    public Activity findById(String id) {
        Activity activity = (Activity) redisTemplate.opsForHash().get("activity","activity_"+id);
        if(activity==null){
            activity = activityDao.findById(id).get();
            redisTemplate.opsForHash().put("activity","activity_"+id,activity);
        }
        return activity;
    }

    public void deleteById(String id) {
        redisTemplate.opsForHash().delete("activity","activity_"+id);
        activityDao.deleteById(id);
    }

    public void save(Activity activity) {
        activity.setId(idWorker.nextId()+"");
        activityDao.save(activity);
    }

    public void updateById(String id, Activity activity) {
        redisTemplate.opsForHash().delete("activity","activity_"+id);
        activityDao.save(activity);
    }

    public PageResult<Activity> searchFollow(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<Activity> pageActivity = activityDao.searchFollow(userid,pageable);
        return new PageResult<Activity>(pageActivity.getTotalElements(),pageActivity.getContent());
    }

    public void follow(String id, String userid) {
        activityDao.follow(id,userid);
    }

    public void cancelFollow(String id, String userid) {
        activityDao.cancelFollow(id,userid);
    }

    public Boolean findIsFollow(String id, String userid) {
        int count = activityDao.findIsFollow(id,userid);
        if(count == 0){
            return false;
        }else{
            return true;
        }
    }

}
