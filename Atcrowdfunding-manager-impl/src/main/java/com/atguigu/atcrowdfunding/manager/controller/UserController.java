package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //同步请求，显示用户列表
//    @RequestMapping("/index")
//    public String index(@RequestParam(value = "pageno", required = false, defaultValue = "1") Integer pageno,
//                        @RequestParam(value = "pagesize", required = false, defaultValue = "10")Integer pagesize,
//                        Map map){
//
//        Page page = userService.queryPage(pageno, pagesize);
//
//        map.put("page", page);
//
//        return "user/index";
//    }



    //异步请求方式，去到主页面
    @RequestMapping("/toIndex")
    public String toIndex(){


        return "user/index";
    }

    //异步请求
    @ResponseBody
    @RequestMapping("/index")
    public Object index(@RequestParam(value = "pageno", required = false, defaultValue = "1") Integer pageno,
                        @RequestParam(value = "pagesize", required = false, defaultValue = "10")Integer pagesize){

        AjaxResult result = new AjaxResult();
        try {
            Page page = userService.queryPage(pageno, pagesize);
            result.setSuccess(true);
            result.setPage(page);

        }catch (Exception e){

            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("查询用户失败！");
        }



        return result;  //将对象序列化为JSON字符串，以流的形式返回
    }
}
