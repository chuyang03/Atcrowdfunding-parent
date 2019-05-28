package com.atguigu.atcrowdfunding.manager.service;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.util.Page;

import java.util.Map;

public interface UserService {

    User queryUserLogin(Map<String, Object> paramMap);

    Page queryPage(Integer pageno, Integer pagesize);


    //往用户表里添加用户，测试数据
    int saveUser(User user);
}
