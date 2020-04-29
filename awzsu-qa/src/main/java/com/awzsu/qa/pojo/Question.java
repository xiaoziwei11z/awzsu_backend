package com.awzsu.qa.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_question")
@Data
public class Question {
    @Id
    private String id;
    private String labelid;
    private String userid;
    private String username;
    private String title;
    private String content;
    private Integer visits;
    private Integer replys;
    private Date createtime;
    private Date updatetime;
    private String replyname;
    private Date replytime;
    private String state;
}
