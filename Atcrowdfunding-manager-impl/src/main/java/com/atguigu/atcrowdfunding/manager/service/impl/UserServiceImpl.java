package com.atguigu.atcrowdfunding.manager.service.impl;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.exception.LoginFailException;
import com.atguigu.atcrowdfunding.manager.dao.UserMapper;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.Const;
import com.atguigu.atcrowdfunding.util.MD5Util;
import com.atguigu.atcrowdfunding.util.Page;
import com.atguigu.atcrowdfunding.vo.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User queryUserLogin(Map<String, Object> paramMap) {

        User user = userMapper.queryUserLogin(paramMap);

        if (user == null){
            throw new LoginFailException("用户账号或者密码不正确！");
        }
        return user;
    }

//    @Override
//    public Page queryPage(Integer pageno, Integer pagesize) {
//
//        Page page = new Page(pageno, pagesize);
//
//        Integer startIndex = page.getStartIndex();
//
//        List<User> userList = userMapper.queryList(startIndex, pagesize);
//
//        page.setDatas(userList);
//
//        Integer totalsize = userMapper.queryCount();
//
//        page.setTotalsize(totalsize);
//
//        return page;
//    }

    @Override
    public int saveUser(User user) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        Date date = new Date();

        //将日期格式转换为字符串
        String createtime = sdf.format(date);
        user.setCreatetime(createtime);

        user.setUserpswd(MD5Util.digest(Const.PASSWORD));

        return userMapper.insert(user);
    }

    @Override
    public Page queryPage(Map<String, Object> paramMap) {

        Page page = new Page((Integer) paramMap.get("pageno"), (Integer) paramMap.get("pagesize"));

        Integer startIndex = page.getStartIndex();
        paramMap.put("startIndex", startIndex);

        List<User> userList = userMapper.queryList(paramMap);

        page.setDatas(userList);

        Integer totalsize = userMapper.queryCount(paramMap);

        page.setTotalsize(totalsize);

        return page;
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBatchUser(Integer[] ids) {

        int totalCount = 0;
        for (Integer id:ids) {
            int count = userMapper.deleteByPrimaryKey(id);

            //保证返回的操作数据的条数和传进来的id值的个数一样
            totalCount += count;
        }

        if (totalCount!=ids.length){
            throw new RuntimeException("批量删除失败");
        }
        return totalCount;
    }

    @Override
    public int deleteBatchUserByVO(Data data) {


        return userMapper.deleteBatchUserByVO(data);
    }
}
