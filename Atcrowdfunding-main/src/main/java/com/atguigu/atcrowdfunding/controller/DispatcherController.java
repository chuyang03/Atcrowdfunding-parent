package com.atguigu.atcrowdfunding.controller;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Const;
import com.atguigu.atcrowdfunding.util.MD5Util;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    //去到主页面
    @RequestMapping("/main")
    public String main(){

        return "main";
    }

    //退出登录
    @RequestMapping("/logout")
    public String logout(HttpSession session){


        //用户退出登录时销毁seeion对象，或者清空session域
        session.invalidate();

        //重定向到首页面
        return "redirect:/index.htm";
    }

    //异步请求登陆，使用ajax
    //@ResponseBody  结合Jkson组件，将返回结果转换为字符串，将JSON以流的形式返回给客户端
    @ResponseBody
    @RequestMapping("/doLogin")
    public Object doLogin(String loginacct, String userpswd, String type, HttpSession session){

        AjaxResult result = new AjaxResult();

        try{
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("loginacct", loginacct);
            //将密码加密
            paramMap.put("userpswd", MD5Util.digest(userpswd));
            paramMap.put("type", type);
//            System.out.println(paramMap.get("loginacct"));
//            System.out.println(paramMap.get("userpswd"));

            User user = userService.queryUserLogin(paramMap);

            session.setAttribute(Const.LOGIN_USER, user);

            result.setSuccess(true);
            //返回的值是一个json类型数据
            //{"success":true}
        }catch (Exception e){
            result.setMessage("登陆失败！");
            e.printStackTrace();
            result.setSuccess(false);

            //{"message":"登陆失败！", "success":false}
        }

        return result;
        //返回的值是一个json类型数据,result的值就是一个json数据，如下所示
        //{"success":true}
    }


    //同步请求  登陆用户
//    @RequestMapping("/doLogin")
//    public String doLogin(String loginacct, String userpswd, String type, HttpSession session){
//
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("loginacct", loginacct);
//        paramMap.put("userpswd", userpswd);
//        paramMap.put("type", type);
//        System.out.println(paramMap.get("loginacct"));
//        System.out.println(paramMap.get("userpswd"));
//
//        User user = userService.queryUserLogin(paramMap);
//
//        session.setAttribute(Const.LOGIN_USER, user);
//
//        //重定向操作可以使得下次刷新浏览器不会重复登陆，即重复提交表单（重定向到另一个页面了）
//        return "redirect:/main.htm";
//    }
}
