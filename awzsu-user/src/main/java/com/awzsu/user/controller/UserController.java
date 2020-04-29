package com.awzsu.user.controller;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.common.utils.JwtUtil;
import com.awzsu.user.pojo.Friends;
import com.awzsu.user.pojo.User;
import com.awzsu.user.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @GetMapping("/sms/{phone}")
    public Result sms(@PathVariable String phone){
        userService.sms(phone);
        return new Result(true, StatusCode.OK,"发送成功");
    }

    /**
     * 用户注册
     * @param user
     * @param smscode
     * @return
     */
    @PostMapping("/register/{smscode}")
    public Result register(@RequestBody User user,@PathVariable String smscode){
        userService.register(user,smscode);
        return new Result(true,StatusCode.OK,"注册成功");
    }

    /**
     * 用户登录
     * @param loginUser
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User loginUser){
        User user = userService.login(loginUser);
        if(user==null){
            return new Result(false,StatusCode.ERROR,"用户名或密码错误");
        }

        List<String> permissionList = userService.findPermissionByRoleid(user.getRoleid());
        String token = jwtUtil.createJWT(user.getId(), user.getPhone(), user.getRoleid(), permissionList,user.getName());
        Map<String,String> resultMap = new HashMap<String,String>();
        resultMap.put("token",token);
        resultMap.put("id",user.getId());
        resultMap.put("name",user.getName());
        resultMap.put("image",user.getImage());
        return new Result(true,StatusCode.OK,"登录成功",resultMap);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        return new Result(true,StatusCode.OK,"查询成功",userService.findById(id));
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping
    public Result save(@RequestBody User user){
        userService.save(user);
        return new Result(true,StatusCode.OK,"操作成功");
    }

    /**
     * 关注用户
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
        userService.follow(id,userid);
        return new Result(true,StatusCode.OK,"关注成功");
    }

    /**
     * 不喜欢用户
     * @param id
     * @return
     */
    @PutMapping("/dislike/{id}")
    public Result dislike(@PathVariable String id){
        Claims claims = (Claims) request.getAttribute("claims");
        if(claims==null){
            return new Result(false,StatusCode.ACCESSERROR,"无权访问");
        }
        String userid = claims.getId();
        userService.dislike(id,userid);
        return new Result(true,StatusCode.OK,"不喜欢成功");
    }

    /**
     * 取消关注用户
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
        userService.cancelFollow(id,userid);
        return new Result(true,StatusCode.OK,"取消关注成功");
    }

    /**
     * 查询是否关注用户
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
        Boolean isFollow = userService.findIsFollow(id,userid);
        return new Result(true,StatusCode.OK,"查询成功",isFollow);
    }

    /**
     * 查询关注列表
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/follow/{page}/{size}/{userid}")
    public Result searchFollow(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<User> pageResult = userService.searchFollow(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 查询粉丝列表
     * @param page
     * @param size
     * @param userid
     * @return
     */
    @GetMapping("/fans/{page}/{size}/{userid}")
    public Result searchFans(@PathVariable int page,@PathVariable int size,@PathVariable String userid){
        PageResult<User> pageResult = userService.searchFans(page,size,userid);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 发布交友信息
     * @param friends
     * @return
     * @throws Exception
     */
    @PostMapping("/friends")
    public Result saveFriends(@RequestBody Friends friends) throws Exception {
        userService.saveFriends(friends);
        return new Result(true,StatusCode.OK,"发布成功");
    }

    /**
     * 查询交友信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/friends/{page}/{size}")
    public Result searchFriends(@PathVariable int page,@PathVariable int size){
        Claims claims = (Claims) request.getAttribute("claims");
        PageResult<Friends> pageResult = null;
        if(claims!=null){
            String userid = claims.getId();
            pageResult = userService.searchFriendsWithUserid(page,size,userid);
        }else{
            pageResult = userService.searchFriends(page,size);
        }
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 分页条件查询用户
     * @param page
     * @param size
     * @param searchMap
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable int page, @PathVariable int size, @RequestBody Map searchMap){
        PageResult<User> pageResult = userService.search(page,size,searchMap);
        return new Result(true, StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id){
        userService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 创建管理员
     * @param user
     * @return
     */
    @PostMapping("/admin")
    public Result saveAdmin(@RequestBody User user){
        userService.saveAdmin(user);
        return new Result(true,StatusCode.OK,"操作成功");
    }

    /**
     * 管理员登录
     * @param loginUser
     * @return
     */
    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody User loginUser){
        User user = userService.adminLogin(loginUser);
        if(user==null){
            return new Result(false,StatusCode.ERROR,"用户名或密码错误");
        }

        List<String> permissionList = userService.findPermissionByRoleid(user.getRoleid());
        String token = jwtUtil.createJWT(user.getId(), user.getPhone(), user.getRoleid(), permissionList,user.getName());
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("token",token);
        resultMap.put("id",user.getId());
        resultMap.put("phone",user.getPhone());
        resultMap.put("permissions",permissionList);
        return new Result(true,StatusCode.OK,"登录成功",resultMap);
    }

}
