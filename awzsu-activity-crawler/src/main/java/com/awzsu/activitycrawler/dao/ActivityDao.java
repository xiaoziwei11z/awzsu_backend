package com.awzsu.activitycrawler.dao;

import com.awzsu.activitycrawler.pojo.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityDao extends JpaRepository<Activity,String>,JpaSpecificationExecutor<Activity> {
}
