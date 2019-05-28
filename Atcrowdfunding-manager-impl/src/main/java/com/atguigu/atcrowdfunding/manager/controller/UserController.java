package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import com.atguigu.atcrowdfunding.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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
//    @ResponseBody
//    @RequestMapping("/index")
//    public Object index(@RequestParam(value = "pageno", required = false, defaultValue = "1") Integer pageno,
//                        @RequestParam(value = "pagesize", required = false, defaultValue = "10")Integer pagesize){
//
//        AjaxResult result = new AjaxResult();
//        try {
//            Page page = userService.queryPage(pageno, pagesize);
//            result.setSuccess(true);
//            result.setPage(page);
//
//        }catch (Exception e){
//
//            result.setSuccess(false);
//            e.printStackTrace();
//            result.setMessage("查询用户失败！");
//        }
//
//
//
//        return result;  //将对象序列化为JSON字符串，以流的形式返回
//    }


    //条件查询，没输入条件时查询的是所有数据，有查询条件时，查询按照条件查询符合要求的数据
    @ResponseBody
    @RequestMapping("/index")
    public Object index(@RequestParam(value = "pageno", required = false, defaultValue = "1") Integer pageno,
                        @RequestParam(value = "pagesize", required = false, defaultValue = "10")Integer pagesize,
                        String queryText){

        Map paramMap = new HashMap();
        paramMap.put("pageno", pageno);
        paramMap.put("pagesize", pagesize);

        AjaxResult result = new AjaxResult();
        try {

            if (StringUtil.isNotEmpty(queryText)){

                //解决模糊查询%的问题
                if (queryText.contains("%")){

                    //  "\\\\%"为什么需要四个反斜杠，java中转义后，变成两个反斜杠，replaceAll的底层是正则表达式，
                    //  又需要转义一次，到sql中的时候变成 \%
                    queryText = queryText.replaceAll("%", "\\\\%");
                }

                //如果查询框不为空，则吧查询条件放在map里面当作参数传到查询方法中
                paramMap.put("queryText", queryText);     //放进map的值是\%
            }

            Page page = userService.queryPage(paramMap);
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
