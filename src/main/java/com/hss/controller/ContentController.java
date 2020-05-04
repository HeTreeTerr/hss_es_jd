package com.hss.controller;

import com.hss.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping(value = "/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword){
        try {
            return contentService.parseContent(keyword);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping(value = "/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keyword") String keyword,
                                           @PathVariable("pageNo") Integer pageNo,
                                           @PathVariable("pageSize") Integer pageSize){
        try {
            return contentService.searchPage(keyword,pageNo,pageSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
