package com.awzsu.article.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document
public class Comment {
    @Id
    private String _id;
    private String articleid;
    private String userid;
    private String username;
    private String content;
    private Date createtime;
}
