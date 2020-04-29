package com.awzsu.activity.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tb_activity")
@Data
public class Activity implements Serializable {
    @Id
    private String id;
    private String name;
    private String content;
    private String image;
    private String holdingtime;
    private String address;
    private Date enrolltime;
    private String contactname;
    private String contactphone;
    private String contactemail;
    private String state;
}
