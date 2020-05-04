package com.hss.util;

import com.hss.bean.Content;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlParseUtilTest {

    @Test
    void parseJD() {
        try {
            List<Content> contentList = HtmlParseUtil.parseJD("入门");
            for(Content content : contentList){
                System.out.println(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}