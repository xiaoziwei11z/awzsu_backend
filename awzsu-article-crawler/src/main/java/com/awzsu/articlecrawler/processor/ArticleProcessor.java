package com.awzsu.articlecrawler.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class ArticleProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("https://blog.csdn.net/[a-z 0-9 -]+/article/details/[0-9]+").all());
        String title = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[1]/h1").get();
        String content = page.getHtml().xpath("//*[@id=\"content_views\"]").get();

        if(StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content)){
            page.putField("title",title);
            page.putField("content",content);
        }else {
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setSleepTime(100).setRetryTimes(3);
    }
}
