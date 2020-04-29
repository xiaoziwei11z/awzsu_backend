package com.awzsu.square.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Speech {
    @Id
    private String _id;
    private String parentid;
    private String userid;
    private String username;
    private String content;
    private Integer visits;
    private Integer thumbups;
    private Integer comments;
    private Date createtime;
    private String state;
}
