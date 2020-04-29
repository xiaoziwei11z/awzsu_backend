package com.awzsu.user.service;

import com.awzsu.common.entity.PageResult;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.common.utils.IdWorker;
import com.awzsu.user.dao.FriendsDao;
import com.awzsu.user.dao.UserDao;
import com.awzsu.user.pojo.Friends;
import com.awzsu.user.pojo.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private FriendsDao friendsDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void sms(String phone) {
        String smscode = RandomStringUtils.randomNumeric(6);
        redisTemplate.opsForValue().set("sms_"+phone,smscode,5, TimeUnit.MINUTES);

        Map<String,String> smsMap = new HashMap<>();
        smsMap.put("phone",phone);
        smsMap.put("smscode",smscode);
        rabbitTemplate.convertAndSend("sms",smsMap);
    }

    public void register(User user, String smscode) {
        String redisSmscode = (String) redisTemplate.opsForValue().get("sms_" + user.getPhone());
        if(StringUtils.isBlank(redisSmscode)){
            throw new RuntimeException("请先获取验证码");
        }
        if(!redisSmscode.equals(smscode)){
            throw new RuntimeException("验证码输入不正确");
        }
        redisTemplate.delete("sms_"+user.getPhone());

        user.setId(idWorker.nextId()+"");
        user.setRoleid("1");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setImage("");
        user.setDetail("");
        user.setSex("");
        user.setAddress("");
        user.setSchool("");
        user.setCompany("");
        user.setSite("");
        Date date = new Date();
        user.setCreatetime(date);
        user.setUpdatetime(date);
        user.setFollows(0);
        user.setFans(0);
        userDao.save(user);
    }

    public User login(User loginUser) {
        User user = userDao.findByPhoneAndRoleid(loginUser.getPhone(),"1");
        if(user!=null && passwordEncoder.matches(loginUser.getPassword(),user.getPassword())){
            return user;
        }
        return null;
    }

    public List<String> findPermissionByRoleid(String roleid) {
        return userDao.findPermissionByRoleid(roleid);
    }

    public User findById(String id) {
        return userDao.findById(id).get();
    }

    public void save(User user) {
        userDao.save(user);
    }

    public void follow(String id, String userid) {
        userDao.follow(id,userid);
        userDao.updateFollows(userid,1);
        userDao.updateFans(id,1);
    }


    public void dislike(String id, String userid) {
        userDao.dislike(id,userid);
    }

    public void cancelFollow(String id, String userid) {
        userDao.cancelFollow(id,userid);
        userDao.updateFollows(userid,-1);
        userDao.updateFans(id,-1);
    }

    public Boolean findIsFollow(String id, String userid) {
        int count = userDao.findIsFollow(id,userid);
        if(count == 0){
            return false;
        }else{
            return true;
        }
    }

    public PageResult<User> searchFollow(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<User> pageUser = userDao.searchFollow(userid,pageable);
        return new PageResult<User>(pageUser.getTotalElements(),pageUser.getContent());
    }

    public PageResult<User> searchFans(int page, int size, String userid) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<User> pageUser = userDao.searchFans(userid,pageable);
        return new PageResult<User>(pageUser.getTotalElements(),pageUser.getContent());
    }

    public void saveFriends(Friends friends) throws Exception {
        friends.setAge(getAge(friends.getBirthday()));
        friends.setId(idWorker.nextId()+"");
        Date date = new Date();
        friends.setCreatetime(date);
        friends.setUpdatetime(date);
        friendsDao.save(friends);
    }

    private int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            }else{
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    public PageResult<Friends> searchFriendsWithUserid(int page, int size, String userid) {
        List<String> relatedIdList = userDao.findRelatedId(userid);
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Friends> pageFriends = null;
        if(!relatedIdList.isEmpty()){
            pageFriends = friendsDao.findByUseridNotIn(relatedIdList,pageable);
        }else{
            pageFriends = friendsDao.findAll(pageable);
        }
        return new PageResult<Friends>(pageFriends.getTotalElements(),pageFriends.getContent());
    }

    public PageResult<Friends> searchFriends(int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<Friends> pageFriends = friendsDao.findAll(pageable);
        return new PageResult<Friends>(pageFriends.getTotalElements(),pageFriends.getContent());
    }

    public PageResult<User> search(int page, int size, Map searchMap) {
        PageRequest pageRequest = PageRequest.of(page-1,size,new Sort(Sort.Direction.DESC,"createtime"));
        Page<User> pageList = userDao.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("roleid"))) {
                    Predicate predicate = criteriaBuilder.equal(root.get("roleid").as(String.class), searchMap.get("roleid"));
                    list.add(predicate);
                }
                if (StringUtils.isNotBlank((CharSequence) searchMap.get("phone"))) {
                    Predicate predicate = criteriaBuilder.like(root.get("phone").as(String.class), "%"+searchMap.get("phone")+"%");
                    list.add(predicate);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageRequest);
        return new PageResult<User>(pageList.getTotalElements(),pageList.getContent());
    }

    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    public void saveAdmin(User user) {
        user.setId(idWorker.nextId()+"");
        user.setRoleid("2");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Date date = new Date();
        user.setCreatetime(date);
        user.setUpdatetime(date);
        userDao.save(user);
    }

    public User adminLogin(User loginUser) {
        User user = userDao.findByPhoneAndRoleidNot(loginUser.getPhone(),"1");
        if(user!=null && passwordEncoder.matches(loginUser.getPassword(),user.getPassword())){
            return user;
        }
        return null;
    }
}
