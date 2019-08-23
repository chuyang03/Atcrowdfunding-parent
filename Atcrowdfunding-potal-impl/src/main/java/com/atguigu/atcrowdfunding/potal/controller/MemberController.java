package com.atguigu.atcrowdfunding.potal.controller;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.potal.service.MemberService;
import com.atguigu.atcrowdfunding.potal.service.TicketService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TicketService ticketService;

    //跳转到实名认证步骤中账户类型选择页面
    @RequestMapping("/accttype")
    public String accttype(){

        return "member/accttype";
    }

    //当点击未实名认证按钮时，判断之前是否已经操作过一些流程，如果已经操作了一部分流程，则直接跳转到正在执行的流程
    @RequestMapping("/apply")
    public String apply(HttpSession session){

        Member member = (Member) session.getAttribute(Const.LOGIN_MEMBER);

        //查询流程单的时候，在数据库查询的时候是根据memberid和status两个字段进行查询的
        Ticket ticket = ticketService.getTicketByMemberId(member.getId());

        //如果流程审批单没有数据，说明该用户还没有进行过实名认证流程
        if(ticket == null ){
            ticket  = new Ticket(); //封装数据
            ticket.setMemberid(member.getId());
            ticket.setPstep("apply");
            ticket.setStatus("0");

            //将流程进行的情况保存到数据库
            ticketService.saveTicket(ticket);

        }else{
            String pstep = ticket.getPstep();

            if("accttype".equals(pstep)){

                return "redirect:/member/basicinfo.htm";
            } else if("basicinfo".equals(pstep)){

                //根据当前用户查询账户类型,然后根据账户类型查找需要上传的资质

                return "redirect:/member/uploadCert.htm";
            }else if("uploadcert".equals(pstep)){

                return "redirect:/member/checkemail.htm";
            }else if("checkemail".equals(pstep)){

                return "redirect:/member/checkauthcode.htm";
            }


        }

        return "member/accttype";
    }

    //跳转到实名认证信息填写页面
    @RequestMapping("/basicinfo")
    public String basicinfo(){

        return "member/basicinfo";
    }

    //跳转到实名认证资质上传页面
    @RequestMapping("/uploadCert")
    public String uploadCert(){

        return "member/uploadCert";
    }


    //更新账户类型
    @ResponseBody
    @RequestMapping("/updateAcctType")
    public Object updateAcctType(String accttype, HttpSession session){

        AjaxResult result = new AjaxResult();

        try {

            //获取登陆会员用户信息
            Member loginMember = (Member)session.getAttribute(Const.LOGIN_MEMBER);
            //根据前台页面选择的账户类型更新session域中该用户对应的账户类型
            loginMember.setAccttype(accttype);

            //根据更新的会员用户信息更新数据库中该用户的账户类型
            memberService.updateAcctType(loginMember);

            Ticket ticket = ticketService.getTicketByMemberId(loginMember.getId());
            ticket.setPstep("accttype");
            //更新实名认证流程步骤
            ticketService.updatePstep(ticket);

            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();

            result.setSuccess(false);
        }


        return result;
    }

    @ResponseBody
    @RequestMapping("/updateBasicinfo")
    public Object updateBasicinfo(HttpSession session, Member member){  //使用对象接收传入的三个参数

        AjaxResult result = new AjaxResult();

        try {

            //获取登陆会员用户信息
            Member loginMember = (Member)session.getAttribute(Const.LOGIN_MEMBER);
            //根据前台页面选择的账户类型更新session域中该用户对应的账户类型
            //根据输入参数更新内存中的数据，然后在写入数据库，可以保持数据一致
            loginMember.setRealname(member.getRealname());
            loginMember.setCardnum(member.getCardnum());
            loginMember.setTel(member.getTel());

            //根据更新的会员用户信息更新数据库中该用户的账户类型
            memberService.updateBasicinfo(loginMember);

            Ticket ticket = ticketService.getTicketByMemberId(loginMember.getId());
            ticket.setPstep("basicinfo");
            //更新实名认证流程步骤
            ticketService.updatePstep(ticket);

            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();

            result.setSuccess(false);
        }


        return result;
    }
}
