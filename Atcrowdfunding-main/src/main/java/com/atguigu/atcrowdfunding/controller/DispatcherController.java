package com.atguigu.atcrowdfunding.controller;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.Const;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DispatcherController {

    @Autowired
    private UserService userService;

    //寻找网站的首页面
    @RequestMapping("/index")
    public String index(){

        return "index";
    }

    //去到登陆页面
    @RequestMapping("/login")
    public String login(){

        return "login";
    }

    //去到注册页面
    @RequestMapping("/register")
    public String register(){

        return "register";
    }

    @RequestMapping("/main")
    public String main(){

        return "main";
    }

    //登陆用户
    @RequestMapping("/doLogin")
    public String doLogin(String loginacct, String userpswd, String type, HttpSession session){

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginacct", loginacct);
        paramMap.put("userpswd", userpswd);
        paramMap.put("type", type);

        User user = userService.queryUserLogin(paramMap);

        session.setAttribute(Const.LOGIN_USER, user);

        //重定向操作可以使得下次刷新浏览器不会重复登陆，即重复提交表单（重定向到另一个页面了）
        return "redirect:/main.htm";
    }
}
