package com.awzsu.article.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="tb_article")
@Data
public class Article implements Serializable {
    @Id
    private String id;
    private String labelid;
    private String userid;
    private String title;
    private String content;
    private Date createtime;
    private Date updatetime;
    private Integer visits;
    private Integer thumbups;
    private Integer comments;
    private String state;
}
