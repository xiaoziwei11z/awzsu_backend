package com.awzsu.article.controller;

import com.awzsu.article.bo.ArticleBo;
import com.awzsu.article.pojo.Article;
import com.awzsu.article.pojo.Comment;
import com.awzsu.article.service.ArticleService;
import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 根据id查找文章
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        return new Result(true, StatusCode.OK,"查询成功",articleService.findById(id,claims));
    }

    /**
     * 按条件分页查询文章
     * @param page
     * @param size
     * @param searchMap
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable int page, @PathVariable int size, @RequestBody Map searchMap){
        PageResult<ArticleBo> pageResult = articleService.search(page,size,searchMap);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 文章审核
     */
    @PutMapping("/examine/{id}/{state}")
    public Result examine(@PathVariable String id,@PathVariable String state){
        articleService.examine(id,state);
        return new Result(true,StatusCode.OK,"审核成功");
    }

    /**
     * 根据id删除文章
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id){
        articleService.deleteById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /**
     * 分页查询最热列表
     * @param page
     * @param size
     * @param labelid
     * @return
     */
    @GetMapping("/hotlist/{page}/{size}/{labelid}")
    public Result hotlist(@PathVariable int page, @PathVariable int size,  @PathVariable String labelid){
        PageResult<ArticleBo> pageResult = articleService.hotlist(page,size,labelid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 分页查询最新列表
     * @param page
     * @param size
     * @param labelid
     * @return
     */
    @GetMapping("/newlist/{page}/{size}/{labelid}")
    public Result newlist(@PathVariable int page, @PathVariable int size,  @PathVariable String labelid){
        PageResult<ArticleBo> pageResult = articleService.newlist(page,size,labelid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 新增文章
     * @param article
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Article article){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        articleService.save(article,claims.getId());
        return new Result(true, StatusCode.OK,"发表成功");
    }

    /**
     * 根据文章id分页查询评论
     * @param page
     * @param size
     * @param articleid
     * @return
     */
    @GetMapping("/searchComments/{page}/{size}/{articleid}")
    public Result searchCommentsByArticleid(@PathVariable int page,@PathVariable int size,@PathVariable String articleid){
        PageResult<Comment> pageResult = articleService.searchCommentsByArticleid(page,size,articleid);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 新增评论
     * @param comment
     * @return
     */
    @PostMapping("/comment")
    public Result saveComment(@RequestBody Comment comment){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        articleService.saveComment(comment,claims.getId(),(String)claims.get("name"));
        return new Result(true,StatusCode.OK,"评论成功");
    }

    /**
     * 导入索引库
     * @return
     */
    @GetMapping("/index")
    public Result importIndex(){
        articleService.importIndex();
        return new Result(true,StatusCode.OK,"导入成功");
    }

    /**
     * 根据用户id分页查询文章
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/user/{page}/{size}/{userid}")
    public Result searchByUserid(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Article> pageResult = articleService.searchByUserid(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 查询用户关注文章
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/follow/{page}/{size}/{userid}")
    public Result searchFollow(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Article> pageResult = articleService.searchFollow(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 用户关注文章
     * @param id
     * @return
     */
    @PutMapping("/follow/{id}")
    public Result follow(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        String userid = claims.getId();
        articleService.follow(id,userid);
        return new Result(true,StatusCode.OK,"收藏成功");
    }

    /**
     * 用户取消关注文章
     * @param id
     * @return
     */
    @PutMapping("/cancelFollow/{id}")
    public Result cancelFollow(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        String userid = claims.getId();
        articleService.cancelFollow(id,userid);
        return new Result(true,StatusCode.OK,"取消收藏成功");
    }

    /**
     * 用户是否关注文章
     * @param id
     * @return
     */
    @GetMapping("/isFollow/{id}")
    public Result findIsFollow(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        String userid = claims.getId();
        Boolean isFollow = articleService.findIsFollow(id,userid);
        return new Result(true,StatusCode.OK,"查询成功",isFollow);
    }
}
