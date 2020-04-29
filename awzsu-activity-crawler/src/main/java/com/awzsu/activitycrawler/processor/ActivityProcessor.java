package com.awzsu.activitycrawler.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class ActivityProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("https://huiyi.csdn.net/activity/product/goods_list\\?project_id=[0-9]+").all());
        String name = page.getHtml().xpath("//*[@class=\"act-head\"]/dl/dt/h2/text()").get();
        String content = page.getHtml().xpath("//*[@class=\"intro dec\"]").get();
        String image = page.getHtml().xpath("//img[@class=\"ads\"]/@src").get();
        String holdingtime = page.getHtml().xpath("//*[@class=\"addr-time\"]/ul/li[1]/text()").get();
        String address = page.getHtml().xpath("//*[@class=\"addr-time\"]/ul/li[2]/text()").get();
        String contactname = page.getHtml().xpath("//span[@class=\"mark-contacter\"]/text()").get();
        String contactMark = page.getHtml().xpath("//div[@class=\"contact mark\"]/p/text()").get();

        if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(content) && StringUtils.isNotBlank(image) && StringUtils.isNotBlank(holdingtime)
                && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(contactname) && StringUtils.isNotBlank(contactMark)){
            page.putField("name",name);
            page.putField("content",content);
            page.putField("image",image);
            page.putField("holdingtime",holdingtime.replace("时间：",""));
            page.putField("address",address.replace("地点：",""));
            page.putField("contactname",contactname.replace("联系人：",""));
            page.putField("contactphone",contactMark.substring(contactMark.indexOf("电话：")+3,contactMark.indexOf("电话：")+14));
            page.putField("contactemail",contactMark.substring(contactMark.indexOf("邮箱：")+3));
        }else {
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setSleepTime(100).setRetryTimes(3);
    }
}
