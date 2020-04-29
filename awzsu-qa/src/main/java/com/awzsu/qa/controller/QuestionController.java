package com.awzsu.qa.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.qa.pojo.Question;
import com.awzsu.qa.service.QuestionService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 按条件分页查询问题
     * @param page
     * @param size
     * @param searchMap
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable int page, @PathVariable int size, @RequestBody Map searchMap){
        PageResult<Question> pageResult = questionService.search(page,size,searchMap);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 新增问题
     * @param question
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Question question){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        questionService.save(question,claims.getId(),(String)claims.get("name"));
        return new Result(true, StatusCode.OK,"发表成功");
    }

    /**
     * 根据id查询问题
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Question question = questionService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",question);
    }

    /**
     * 根据id解决问题
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result done(@PathVariable String id){
        questionService.done(id);
        return new Result(true,StatusCode.OK,"更新成功");
    }

    /**
     * 根据用户id查询问题
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/user/{page}/{size}/{userid}")
    public Result searchByUserid(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Question> pageResult = questionService.searchByUserid(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 根据用户id查询收藏问题
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/follow/{page}/{size}/{userid}")
    public Result searchFollow(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Question> pageResult = questionService.searchFollow(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 收藏问题
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
        questionService.follow(id,userid);
        return new Result(true,StatusCode.OK,"关注成功");
    }

    /**
     * 取消收藏问题
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
        questionService.cancelFollow(id,userid);
        return new Result(true,StatusCode.OK,"取消关注成功");
    }

    /**
     * 查询用户是否收藏该问题
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
        Boolean isFollow = questionService.findIsFollow(id,userid);
        return new Result(true,StatusCode.OK,"查询成功",isFollow);
    }
}
