package com.atguigu.atcrowdfunding.potal.service;

import com.atguigu.atcrowdfunding.bean.Member;

import java.util.Map;

public interface MemberService {


    Member queryMemberLogin(Map<String, Object> paramMap);
}
