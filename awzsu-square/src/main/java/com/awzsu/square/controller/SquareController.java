package com.awzsu.square.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.square.pojo.Speech;
import com.awzsu.square.service.SquareService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/square")
@CrossOrigin
public class SquareController {
    @Autowired
    private SquareService squareService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增动态
     * @param speech
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Speech speech){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        squareService.save(speech,claims.getId(),(String)claims.get("name"));
        return new Result(true, StatusCode.OK,"发表成功");
    }

    /**
     * 根据parentid分页查询
     * @param page
     * @param size
     * @param parentid
     * @return
     */
    @GetMapping("/search/{page}/{size}/{parentid}")
    public Result searchByParentid(@PathVariable int page, @PathVariable int size, @PathVariable String parentid){
        PageResult<Speech> pageResult = squareService.searchByParentid(page,size,parentid);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        return new Result(true,StatusCode.OK,"查询成功",squareService.findById(id));
    }

    /**
     * 根据id和parentid删除
     * @param id
     * @param parentId
     * @return
     */
    @DeleteMapping("/{id}/{parentId}")
    public Result deleteById(@PathVariable String id,@PathVariable String parentId){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        squareService.deleteById(id,parentId);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 动态点赞
     * @param id
     * @return
     */
    @PutMapping("/thumbup/{id}")
    public Result thumbup(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        String userid = claims.getId();
        if(redisTemplate.opsForHash().get("thumbup","userid_"+userid+"_"+id)!=null){
            return new Result(false,StatusCode.REPERROR,"不能重复点赞");
        }
        squareService.thumbup(id);
        redisTemplate.opsForHash().put("thumbup","userid_"+userid+"_"+id,1);
        return new Result(true,StatusCode.OK,"点赞成功");
    }

    /**
     * 根据用户id查询
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/user/{page}/{size}/{userid}")
    public Result searchByUserid(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Speech> pageResult = squareService.searchByUserid(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }
}
