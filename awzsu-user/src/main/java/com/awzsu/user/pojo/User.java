package com.awzsu.user.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_user")
@Data
public class User {
    @Id
    private String id;
    private String roleid;
    private String phone;
    private String password;
    private String image;
    private String name;
    private String detail;
    private String sex;
    private Date birthday;
    private String address;
    private String school;
    private String company;
    private String site;
    private Date createtime;
    private Date updatetime;
    private Integer follows;
    private Integer fans;
}
