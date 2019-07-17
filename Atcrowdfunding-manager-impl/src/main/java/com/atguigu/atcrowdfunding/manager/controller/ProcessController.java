package com.atguigu.atcrowdfunding.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("process")
public class ProcessController {

    @RequestMapping("index")
    public String index(){

        return "process/index";
    }
}
