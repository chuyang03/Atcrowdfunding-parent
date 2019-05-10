package com.atguigu.atcrowdfunding.potal.controller;

import com.atguigu.atcrowdfunding.potal.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @Autowired
    private TestService testService;


    @RequestMapping("/test")
    public String test(){

        System.out.println("TestController");

        testService.insert();
        System.out.println("finish");
        return "success";
    }
}
