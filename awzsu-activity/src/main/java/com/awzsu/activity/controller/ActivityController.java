package com.awzsu.activity.controller;

import com.awzsu.activity.pojo.Activity;
import com.awzsu.activity.service.ActivityService;
import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 按条件分页查找
     * @param page
     * @param size
     * @param searchMap
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable int page, @PathVariable int size, @RequestBody Map searchMap){
        PageResult<Activity> pageResult = activityService.search(page,size,searchMap);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 根据id查找
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        return new Result(true, StatusCode.OK,"查询成功",activityService.findById(id));
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id){
        activityService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 新增活动
     * @param activity
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Activity activity){
        activityService.save(activity);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 根据id更新
     * @param id
     * @param activity
     * @return
     */
    @PutMapping("/{id}")
    public Result updateById(@PathVariable String id,@RequestBody Activity activity){
        activityService.updateById(id,activity);
        return new Result(true,StatusCode.OK,"更新成功");
    }

    /**
     * 根据用户id查找收藏的活动
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/follow/{page}/{size}/{userid}")
    public Result searchFollow(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<Activity> pageResult = activityService.searchFollow(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 收藏活动
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
        activityService.follow(id,userid);
        return new Result(true,StatusCode.OK,"收藏成功");
    }

    /**
     * 取消收藏活动
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
        activityService.cancelFollow(id,userid);
        return new Result(true,StatusCode.OK,"取消收藏成功");
    }

    /**
     * 查询是否收藏活动
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
        Boolean isFollow = activityService.findIsFollow(id,userid);
        return new Result(true,StatusCode.OK,"查询成功",isFollow);
    }
}
