package com.atguigu.atcrowdfunding.potal.dao;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.Ticket;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TicketMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(Ticket record);


    Ticket selectByPrimaryKey(Integer id);

    List<Ticket> selectAll();


    Ticket getTicketByMemberId(Integer memberid);

    void saveTicket(Ticket ticket);

    void updatePstep(Ticket ticket);

    void updatePiidAndPstep(Ticket ticket);

    Member queryMemberByPiid(String processInstanceId);

    void updateStatus(Member member);
}
