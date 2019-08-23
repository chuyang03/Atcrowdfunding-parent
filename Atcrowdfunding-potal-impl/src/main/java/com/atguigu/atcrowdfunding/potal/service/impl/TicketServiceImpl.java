package com.atguigu.atcrowdfunding.potal.service.impl;

import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.potal.dao.TicketMapper;
import com.atguigu.atcrowdfunding.potal.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public Ticket getTicketByMemberId(Integer id) {
        return ticketMapper.getTicketByMemberId(id);
    }

    @Override
    public void saveTicket(Ticket ticket) {

        ticketMapper.saveTicket(ticket);
    }

    @Override
    public void updatePstep(Ticket ticket) {

        ticketMapper.updatePstep(ticket);
    }
}
