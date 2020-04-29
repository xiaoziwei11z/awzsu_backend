package com.awzsu.user.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_friends")
@Data
public class Friends {
    @Id
    private String id;
    private String userid;
    private String topic;
    private String image;
    private String name;
    private String sex;
    private Date birthday;
    private Integer age;
    private String address;
    private String company;
    private Date createtime;
    private Date updatetime;
}
