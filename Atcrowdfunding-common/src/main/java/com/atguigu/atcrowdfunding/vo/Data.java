package com.atguigu.atcrowdfunding.vo;

import com.atguigu.atcrowdfunding.bean.User;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private List<User> userList = new ArrayList<>();

    private List<User> datas = new ArrayList<>();

    private List<Integer> ids = new ArrayList<>();

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getDatas() {
        return datas;
    }

    public void setDatas(List<User> datas) {
        this.datas = datas;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
