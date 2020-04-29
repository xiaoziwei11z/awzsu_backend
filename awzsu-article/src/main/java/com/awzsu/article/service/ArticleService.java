package com.awzsu.article.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.awzsu.article.bo.ArticleBo;
import com.awzsu.article.client.LabelClient;
import com.awzsu.article.client.UserClient;
import com.awzsu.article.dao.ArticleDao;
import com.awzsu.article.dao.CommentDao;
import com.awzsu.article.pojo.Article;
import com.awzsu.article.pojo.Comment;
import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.utils.IdWorker;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private LabelClient labelClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ArticleBo findById(String id, Claims claims) {
        if(claims==null){
            articleDao.UpdateVisitsById(id);
        }else{
            List<String> permissionList = (List<String>) claims.get("permissionList");
            if(permissionList.contains("1")){
                articleDao.UpdateVisitsById(id);
            }
        }
        Article article = articleDao.findById(id).get();
        ArticleBo articleBo = new ArticleBo();
        BeanUtils.copyProperties(article,articleBo);
        Result userResult = userClient.findById(articleBo.getUserid());
        Object data = userResult.getData();
        if(data!=null){
            JSONObject user = (JSONObject) JSON.toJSON(data);
            articleBo.setUsername(user.getString("name"));
        }
        return articleBo;
    }

    public PageResult<ArticleBo> search(int page, int size, Map searchMap) {
        PageRequest pageRequest = PageRequest.of(page-1,size);
        Page<Article> pageArticle = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("labelid"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("labelid").as(String.class), searchMap.get("labelid"));
                    list.add(predicate);
                }else{
                    //查询全部state为1的标签
                    Result labelResult = labelClient.findAll();
                    Object data = labelResult.getData();
                    JSONArray labelJsonArray = (JSONArray) JSON.toJSON(data);

                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("labelid").as(String.class));
                    for(int i=0;i<labelJsonArray.size();i++){
                        in.value(labelJsonArray.getJSONObject(i).getString("id"));
                    }
                    list.add(criteriaBuilder.and(in));
                }
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("title"))) {
                    Predicate predicate = criteriaBuilder.like(root.get("title").as(String.class), "%" + searchMap.get("title") + "%");
                    list.add(predicate);
                }
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("state"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("state").as(String.class), searchMap.get("state"));
                    list.add(predicate);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageRequest);
        List<Article> articleList = pageArticle.getContent();
        //添加labelname属性
        List<ArticleBo> articleBoList = articleList.stream().map(article -> {
            ArticleBo articleBo = new ArticleBo();
            BeanUtils.copyProperties(article,articleBo);
            Result labelResult = labelClient.findById(articleBo.getLabelid());
            Object data = labelResult.getData();
            if(data!=null){
                JSONObject label = (JSONObject) JSON.toJSON(data);
                articleBo.setLabelname(label.getString("labelname"));
            }
            return articleBo;
        }).collect(Collectors.toList());
        return new PageResult<ArticleBo>(pageArticle.getTotalElements(),articleBoList);
    }

    public void examine(String id, String state) {
        articleDao.updateState(id,state);

        if(state.equals("2")){
            Article article = articleDao.findById(id).get();
            ArticleBo articleBo = new ArticleBo();
            BeanUtils.copyProperties(article, articleBo);
            Result userResult = userClient.findById(articleBo.getUserid());
            Object data = userResult.getData();
            if (data != null) {
                JSONObject user = (JSONObject) JSON.toJSON(data);
                articleBo.setUsername(user.getString("name"));
            }
            rabbitTemplate.convertAndSend("article_save",JSON.toJSONString(articleBo));
        }
    }

    public void deleteById(String id) {
        articleDao.deleteById(id);

        rabbitTemplate.convertAndSend("article_delete",id);
    }

    public PageResult<ArticleBo> hotlist(int page, int size, String labelid) {
        Pageable pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"visits"));
        Page<Article> pageArticle = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(labelid) && !"0".equals(labelid)) {
                    predicateList.add(criteriaBuilder.equal(root.get("labelid").as(String.class),labelid));
                }
                predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class),"2"));
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        },pageRequest);
        List<Article> articleList = pageArticle.getContent();
        List<ArticleBo> articleBoList = articleList.stream().map(article -> {
            ArticleBo articleBo = new ArticleBo();
            BeanUtils.copyProperties(article,articleBo);
            Result userResult = userClient.findById(articleBo.getUserid());
            Object data = userResult.getData();
            if(data!=null){
                JSONObject user = (JSONObject) JSON.toJSON(data);
                articleBo.setUsername(user.getString("name"));
            }
            return articleBo;
        }).collect(Collectors.toList());
        return new PageResult<ArticleBo>(pageArticle.getTotalElements(),articleBoList);
    }

    public PageResult<ArticleBo> newlist(int page, int size, String labelid) {
        Pageable pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Article> pageArticle = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(labelid) && !"0".equals(labelid)) {
                    predicateList.add(criteriaBuilder.equal(root.get("labelid").as(String.class),labelid));
                }
                predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class),"2"));
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        },pageRequest);
        List<Article> articleList = pageArticle.getContent();
        List<ArticleBo> articleBoList = articleList.stream().map(article -> {
            ArticleBo articleBo = new ArticleBo();
            BeanUtils.copyProperties(article,articleBo);
            Result userResult = userClient.findById(articleBo.getUserid());
            Object data = userResult.getData();
            if(data!=null){
                JSONObject user = (JSONObject) JSON.toJSON(data);
                articleBo.setUsername(user.getString("name"));
            }
            return articleBo;
        }).collect(Collectors.toList());
        return new PageResult<ArticleBo>(pageArticle.getTotalElements(),articleBoList);
    }

    public void save(Article article, String id) {
        article.setId(idWorker.nextId()+"");
        article.setUserid(id);
        Date date = new Date();
        article.setCreatetime(date);
        article.setUpdatetime(date);
        article.setVisits(0);
        article.setThumbups(0);
        article.setComments(0);
        article.setState("1");
        articleDao.save(article);
    }

    public PageResult<Comment> searchCommentsByArticleid(int page, int size, String articleid) {
        PageRequest pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Comment> pageList = commentDao.findByArticleid(articleid,pageRequest);
        return new PageResult<Comment>(pageList.getTotalElements(),pageList.getContent());
    }

    public void saveComment(Comment comment, String id, String name) {
        comment.set_id(idWorker.nextId()+"");
        comment.setUserid(id);
        comment.setUsername(name);
        comment.setCreatetime(new Date());
        commentDao.save(comment);
        articleDao.updateCommentsById(comment.getArticleid());
    }

    public void importIndex() {
        List<Article> articleList = articleDao.findByState("2");
        List<ArticleBo> articleBoList = articleList.stream().map(article -> {
            ArticleBo articleBo = new ArticleBo();
            BeanUtils.copyProperties(article, articleBo);
            Result userResult = userClient.findById(articleBo.getUserid());
            Object data = userResult.getData();
            if (data != null) {
                JSONObject user = (JSONObject) JSON.toJSON(data);
                articleBo.setUsername(user.getString("name"));
            }
            return articleBo;
        }).collect(Collectors.toList());

        rabbitTemplate.convertAndSend("article_saveAll", JSON.toJSONString(articleBoList));
    }

    public PageResult<Article> searchByUserid(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Article> pageArticle = articleDao.findByUserid(userid,pageable);
        return new PageResult<Article>(pageArticle.getTotalElements(),pageArticle.getContent());
    }

    public PageResult<Article> searchFollow(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Article> pageArticle = articleDao.searchFollow(userid,pageable);
        return new PageResult<Article>(pageArticle.getTotalElements(),pageArticle.getContent());
    }

    public void follow(String id, String userid) {
        articleDao.follow(id,userid,new Date());
    }

    public void cancelFollow(String id, String userid) {
        articleDao.cancelFollow(id,userid);
    }

    public Boolean findIsFollow(String id, String userid) {
        int count = articleDao.findIsFollow(id,userid);
        if(count == 0){
            return false;
        }else{
            return true;
        }
    }
}
