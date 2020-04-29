package com.awzsu.qa.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "tb_reply")
public class Reply {
    @Id
    private String id;
    private String questionid;
    private String userid;
    private String username;
    private String content;
    private Date createtime;
    private Date updatetime;
}
