package com.awzsu.qa.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.utils.IdWorker;
import com.awzsu.qa.dao.QuestionDao;
import com.awzsu.qa.dao.ReplyDao;
import com.awzsu.qa.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ReplyService {
    @Autowired
    private ReplyDao replyDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private QuestionDao questionDao;

    public PageResult<Reply> searchByQuestionid(int page, int size, String questionid) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, new Sort(Sort.Direction.DESC, "createtime"));
        Page<Reply> pageReply = replyDao.findByQuestionid(questionid,pageRequest);
        return new PageResult<Reply>(pageReply.getTotalElements(),pageReply.getContent());
    }

    public void save(Reply reply, String id, String name) {
        reply.setId(idWorker.nextId()+"");
        reply.setUserid(id);
        reply.setUsername(name);
        Date date = new Date();
        reply.setCreatetime(date);
        reply.setUpdatetime(date);
        replyDao.save(reply);
        questionDao.updateByReply(name,date,reply.getQuestionid());
    }
}
