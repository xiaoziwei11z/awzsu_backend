package com.awzsu.label.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.utils.IdWorker;
import com.awzsu.label.dao.LabelDao;
import com.awzsu.label.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LabelService {
    @Autowired
    private LabelDao labelDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    public List<Label> findAll() {
        return labelDao.findByState("1");
    }

    public Label findById(String id) {
        Label label = (Label) redisTemplate.opsForHash().get("label","label_"+id);
        if(label == null){
            label = labelDao.findById(id).get();
            redisTemplate.opsForHash().put("label","label_"+id,label);
        }
        return label;
    }

    public PageResult<Label> search(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Label> pageLabel = labelDao.pageFindByState("1", pageRequest);
        return new PageResult<Label>(pageLabel.getTotalElements(),pageLabel.getContent());
    }

    public void deleteById(String id) {
        redisTemplate.opsForHash().delete("label","label_"+id);
        labelDao.deleteStateById(id);
    }

    public void save(Label label) {
        label.setId(idWorker.nextId()+"");
        Date date = new Date();
        label.setCreatetime(date);
        label.setUpdatetime(date);
        label.setFans(0l);
        label.setState("1");
        labelDao.save(label);
    }

    public void updateById(String id, Label label) {
        redisTemplate.opsForHash().delete("label","label_"+id);
        labelDao.updateById(id,label.getLabelname(),new Date());
    }

    public List<Label> searchFollow(String userid) {
        return labelDao.searchFollow(userid);
    }

    public void follow(String id, String userid) {
        labelDao.follow(id,userid);
    }

    public void cancelFollow(String id, String userid) {
        labelDao.cancelFollow(id,userid);
    }
}
