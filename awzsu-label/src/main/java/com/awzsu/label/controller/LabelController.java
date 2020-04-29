package com.awzsu.label.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.label.pojo.Label;
import com.awzsu.label.service.LabelService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/label")
public class LabelController {
    @Autowired
    private LabelService labelService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 查找全部标签
     * @return
     */
    @GetMapping
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",labelService.findAll());
    }

    /**
     * 根据id查找标签
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        return new Result(true, StatusCode.OK,"查询成功",labelService.findById(id));
    }

    /**
     * 分页查询标签
     */
    @GetMapping("/search/{page}/{size}")
    public Result search(@PathVariable int page, @PathVariable int size){
        PageResult<Label> pageResult = labelService.search(page,size);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 删除标签，逻辑为数据库state置为0
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id){
        labelService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 新增标签
     */
    @PostMapping
    public Result save(@RequestBody Label label){
        labelService.save(label);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 根据id更新标签
     * @param id
     * @param label
     * @return
     */
    @PutMapping("/{id}")
    public Result updateById(@PathVariable String id,@RequestBody Label label){
        labelService.updateById(id,label);
        return new Result(true,StatusCode.OK,"更新成功");
    }

    /**
     * 查询用户关注的标签
     * @param userid
     * @return
     */
    @GetMapping("/follow/{userid}")
    public Result searchFollow(@PathVariable String userid){
        List<Label> labelList = labelService.searchFollow(userid);
        return new Result(true,StatusCode.OK,"查询成功",labelList);
    }

    /**
     * 用户关注标签
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
        labelService.follow(id,userid);
        return new Result(true,StatusCode.OK,"关注成功");
    }

    /**
     * 用户取消关注标签
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
        labelService.cancelFollow(id,userid);
        return new Result(true,StatusCode.OK,"取消关注成功");
    }
}
