package com.awzsu.qa.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.qa.pojo.Reply;
import com.awzsu.qa.service.ReplyService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/reply")
public class ReplyController {
    @Autowired
    private ReplyService replyService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 根据问题id分页查询回答
     * @param page
     * @param size
     * @param questionid
     * @return
     */
    @GetMapping("/search/{page}/{size}/{questionid}")
    public Result searchByQuestionid(@PathVariable int page,@PathVariable int size,@PathVariable String questionid){
        PageResult<Reply> pageResult = replyService.searchByQuestionid(page,size,questionid);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 新增回答
     * @param reply
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Reply reply){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        replyService.save(reply,claims.getId(),(String)claims.get("name"));
        return new Result(true, StatusCode.OK,"回答成功");
    }


}
