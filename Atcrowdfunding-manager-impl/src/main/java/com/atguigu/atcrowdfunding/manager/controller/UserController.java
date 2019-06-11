package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import com.atguigu.atcrowdfunding.util.StringUtil;
import com.atguigu.atcrowdfunding.vo.Data;
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
    @RequestMapping("/index")
    public String index(){


        return "user/index";
    }

    //去到新增用户页面
    @RequestMapping("/toAdd")
    public String toAdd(){


        return "user/add";
    }

    //添加用户
    @ResponseBody
    @RequestMapping("/doAdd")
    public Object doAdd(User user){

        AjaxResult result = new AjaxResult();
        try {
            int count = userService.saveUser(user);
            result.setSuccess(count == 1);

        }catch (Exception e){

            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("添加用户失败！");
        }



        return result;  //将对象序列化为JSON字符串，以流的形式返回
    }

    //编辑用户页面
    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id, Map map){

        //表单数据回显，当点击修改用户时，将用户数据取到显示在表单中
        User user = userService.getUserById(id);

        //将取出来的user封装到map中，返回给前端页面，以便能在jsp中取到user数据
        map.put("user", user);

        return "user/update";
    }


    //更新用户信息
    @ResponseBody
    @RequestMapping("/doUpdate")
    public Object doUpdate(User user){

        AjaxResult result = new AjaxResult();
        try {
            int count = userService.updateUser(user);
            result.setSuccess(count == 1);

        }catch (Exception e){

            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("更新用户失败！");
        }



        return result;  //将对象序列化为JSON字符串，以流的形式返回
    }

    //删除用户
    @ResponseBody
    @RequestMapping("/doDelete")
    public Object doDelete(Integer id){

        AjaxResult result = new AjaxResult();
        try {
            int count = userService.deleteUser(id);
            result.setSuccess(count == 1);

        }catch (Exception e){

            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("删除用户失败！");
        }



        return result;  //将对象序列化为JSON字符串，以流的形式返回
    }


    //传入的参数是一个json数据
    //批量删除用户
    @ResponseBody
    @RequestMapping("/doDeleteBatch")
    public Object doDeleteBatch(Data data){

        AjaxResult result = new AjaxResult();
        try {
            int count = userService.deleteBatchUserByVO(data);
            //count == id.length 表示操作数据库的数据条数和接受到的id的条数一样返回true
            result.setSuccess(count == data.getDatas().size());

        }catch (Exception e){

            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("批量删除用户失败！");
        }



        return result;  //将对象序列化为JSON字符串，以流的形式返回
    }


    //批量删除,这种方式传入的是一个拼接的字符串
//    @ResponseBody
//    @RequestMapping("/doDeleteBatch")
//    public Object doDeleteBatch(Integer[] id){
//
//        AjaxResult result = new AjaxResult();
//        try {
//            int count = userService.deleteBatchUser(id);
//            //count == id.length 表示操作数据库的数据条数和接受到的id的条数一样返回true
//            result.setSuccess(count == id.length);
//
//        }catch (Exception e){
//
//            result.setSuccess(false);
//            e.printStackTrace();
//            result.setMessage("批量删除用户失败！");
//        }
//
//
//
//        return result;  //将对象序列化为JSON字符串，以流的形式返回
//    }


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
    @RequestMapping("/doIndex")
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
