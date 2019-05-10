package com.atguigu.atcrowdfunding.bean;

/**
 * 
 * 
 * @author wcyong
 * 
 * @date 2019-05-09
 */
public class Role {
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}