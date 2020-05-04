package com.hss.util;

import com.hss.bean.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据爬取
 */
public class HtmlParseUtil {

    public static List<Content> parseJD(String keyword) throws IOException {
        //获取请求 http://search.jd.com/Search?keyword=java
        //前提，需要联网 ajax不能获取
        String url = "http://search.jd.com/Search?keyword=" + keyword + "&enc=utf-8";
        //解析网页 (Jsoup返回Document就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有js中可以使用的方法，这里都能用！
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());
        //获取所有Li元素
        Elements elements = element.getElementsByTag("li");

        List<Content> contentList = new ArrayList<Content>();
        //获取元素中的内容，这里el就是每个li标签了！
        for (Element el : elements) {
            //关于这种图片特别多的网站，所有图片都是延迟加载的！
            String imgUrl = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content(title,imgUrl,price);
            contentList.add(content);
        }
        return contentList;
    }
}
