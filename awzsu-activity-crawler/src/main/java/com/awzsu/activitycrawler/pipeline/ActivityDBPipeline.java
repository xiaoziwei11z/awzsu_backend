package com.awzsu.activitycrawler.pipeline;

import com.awzsu.activitycrawler.dao.ActivityDao;
import com.awzsu.activitycrawler.pojo.Activity;
import com.awzsu.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;

@Component
public class ActivityDBPipeline implements Pipeline {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Activity activity = new Activity();
        activity.setId(idWorker.nextId()+"");
        activity.setName(resultItems.get("name"));
        activity.setContent(resultItems.get("content"));
        activity.setImage(resultItems.get("image"));
        activity.setHoldingtime(resultItems.get("holdingtime"));
        activity.setAddress(resultItems.get("address"));
        activity.setEnrolltime(new Date());
        activity.setContactname(resultItems.get("contactname"));
        activity.setContactphone(resultItems.get("contactphone"));
        activity.setContactemail(resultItems.get("contactemail"));
        activity.setState("1");
        activityDao.save(activity);
    }
}
