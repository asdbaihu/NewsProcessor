package us.codecraft.webmagic.samples;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.FileTxtPipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Iterator;
/**
 * @author code4crafter@gmail.com <br>
 */
public class MiningcatProcessor implements PageProcessor {

	public static final String URL_LIST = "http://news\\.cnyes\\.com/rollnews/list*";
			//+ "list_\\d+\\.shtml";
	
    public static final String URL_POST = "http://news\\.cnyes\\.com/Content/\\d+/\\w+\\.shtml*";
    
    private Site site;

    @Override
    public void process(Page page) {
    	
    	if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"listPage\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"pagination\"]").links().regex("list_\\d+\\.shtml").all());
            //文章页
        } else {
            page.putField("title", page.getHtml().xpath("//div[@class='newsContent bg_newsPage_Lblue']/h1/text()").toString());
            page.putField("content", page.getHtml().xpath("//div[@id='newsText']/tidyText()").toString());
            page.putField("date",
                    page.getHtml().xpath("//div[@class='info']/text()").toString());
        }
                      //http://news.cnyes.com/Content/20151019/20151019172015877563010.shtml?c=detail
                      //page.putField("title",page.getHtml().xpath("//title").toString());
        //page.putField("content",page.getHtml().smartContent().toString());
    }

    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me().setDomain("cnyes.com").setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(1000);;
        }
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new MiningcatProcessor())
        .addUrl("http://news.cnyes.com/rollnews/list.shtml")
        .addPipeline(new FileTxtPipeline("/Users/DK"))
        .thread(5)
        .run();
        //.addPipeline(new FilePipeline("/Users/DK"))
    }
}
