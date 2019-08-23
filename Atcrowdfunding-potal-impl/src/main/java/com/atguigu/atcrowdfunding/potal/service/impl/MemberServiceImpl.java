package com.atguigu.atcrowdfunding.potal.service.impl;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.potal.dao.MemberMapper;
import com.atguigu.atcrowdfunding.potal.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;


    @Override
    public Member queryMemberLogin(Map<String, Object> paramMap) {

        return memberMapper.queryMemberLogin(paramMap);
    }

    @Override
    public void updateAcctType(Member loginMember) {

        memberMapper.updateAcctType(loginMember);
    }

    @Override
    public void updateBasicinfo(Member loginMember) {

        memberMapper.updateBasicinfo(loginMember);
    }
}
