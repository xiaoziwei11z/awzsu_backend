package com.awzsu.articlecrawler.pipeline;

import com.awzsu.articlecrawler.dao.ArticleDao;
import com.awzsu.articlecrawler.pojo.Article;
import com.awzsu.common.utils.HTMLUtil;
import com.awzsu.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;

@Component
public class ArticleDBPipeline implements Pipeline {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private IdWorker idWorker;

    private String labelid;

    public void setLabelid(String labelid) {
        this.labelid = labelid;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = HTMLUtil.delHTMLTag(resultItems.get("title"));
        String content = resultItems.get("content");

        Article article = new Article();
        article.setId(idWorker.nextId()+"");
        article.setLabelid(labelid);
        article.setUserid("1");
        article.setTitle(title);
        article.setContent(content);
        Date date = new Date();
        article.setCreatetime(date);
        article.setUpdatetime(date);
        article.setVisits(0);
        article.setThumbups(0);
        article.setComments(0);
        article.setState("2");
        articleDao.save(article);
    }
}
