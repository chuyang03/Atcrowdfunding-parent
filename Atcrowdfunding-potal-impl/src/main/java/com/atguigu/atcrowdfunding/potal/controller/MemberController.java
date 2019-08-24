package com.atguigu.atcrowdfunding.potal.controller;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;
import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.manager.service.CertService;
import com.atguigu.atcrowdfunding.potal.listener.PassListener;
import com.atguigu.atcrowdfunding.potal.listener.RefuseListener;
import com.atguigu.atcrowdfunding.potal.service.MemberService;
import com.atguigu.atcrowdfunding.potal.service.TicketService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Const;
import com.atguigu.atcrowdfunding.vo.Data;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CertService certService;

    //创建流程实例
    @Autowired
    private RepositoryService repositoryService;

    //启动流程实例
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    //跳转到实名认证步骤中账户类型选择页面
    @RequestMapping("/accttype")
    public String accttype(){

        return "member/accttype";
    }

    //申请流程记忆功能，根据上一次流程进行的进度直接跳转到执行过流程的下一步
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
                List<Cert> certList = certService.queryCertByAccttype(member.getAccttype());

                session.setAttribute("certList", certList);

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

    //跳转到实名认证邮箱验证页面
    @RequestMapping("/checkemail")
    public String checkemail(){

        return "member/checkemail";
    }

    //验证码核对
    @RequestMapping("/checkauthcode")
    public String checkauthcode(){

        return "member/checkauthcode";
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

    //基本信息填写
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

            //流程步骤记忆
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


    //上传资质图片
    @ResponseBody
    @RequestMapping("/doUploadCert")
    public Object doUploadCert(HttpSession session, Data data){  //使用对象接收传入的两个参数，certimges是一个集合，传入的参数certid和fileImg是MembertCert中的两个属性

        AjaxResult result = new AjaxResult();

        try {

            //获取登陆会员用户信息
            Member loginMember = (Member)session.getAttribute(Const.LOGIN_MEMBER);


            //文件上传路径有问题?
            String realPath = session.getServletContext().getRealPath("/pics");

            List<MemberCert> certimgs = data.getCertimgs();
            for (MemberCert memberCert: certimgs) {

                //拿到资质图片
                MultipartFile fileImg = memberCert.getFileImg();
                //拿到上传图片的扩展名，如.jpg
                String extName = fileImg.getOriginalFilename()
                        .substring(fileImg.getOriginalFilename().lastIndexOf("."));

                String fileName = UUID.randomUUID().toString()+extName;

                String filePath = realPath+"/cert"+"/"+fileName;


                //资质文件上传,将fileImg上传到指定路径
                fileImg.transferTo(new File("/Users/chuyangyang/IdeaProjects/Atcrowdfunding-parent/Atcrowdfunding-main/src/main/webapp/pics/cert/"+fileName));

                //封装数据
                memberCert.setIconpath(fileName);
                memberCert.setMemberid(loginMember.getId());

            }
            //保存会员与资质关系数据
            certService.saveMemberCert(certimgs);


            //流程步骤记忆
            Ticket ticket = ticketService.getTicketByMemberId(loginMember.getId());
            ticket.setPstep("uploadcert");
            //更新实名认证流程步骤
            ticketService.updatePstep(ticket);

            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();

            result.setSuccess(false);
        }


        return result;
    }


    //启动邮箱验证流程实例
    @ResponseBody
    @RequestMapping("/startProcess")
    public Object startProcess(HttpSession session,String email){

        AjaxResult result = new AjaxResult();

        try {

            //获取登陆会员用户信息
            Member loginMember = (Member)session.getAttribute(Const.LOGIN_MEMBER);

            //如果用户输入的邮箱和注册的邮箱不一致，则更新用户邮箱
            if (!loginMember.getEmail().equals(email)){

                //更新的是session域的用户邮箱
                loginMember.setEmail(email);
                //更新数据库用户邮箱
                memberService.updateEmail(loginMember);
            }

            //先部署流程定义才能查得到
            //启动实名认证流程中的邮箱验证实例，系统自动发送邮件，生成验证码，验证邮箱地址是否合法，（模拟：银行卡验证）
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey("auth").singleResult();

            //生成随机验证码
            StringBuilder authcode = new StringBuilder();
            for (int i = 0; i < 4; i++) {

                authcode.append(new Random().nextInt(10));
            }

            //准备流程实例中需要的所有变量，将这些变量封装到map集合里
            Map<String,Object> mapVariables = new HashMap<>();
            mapVariables.put("toEmail", email);
            mapVariables.put("authcode", authcode);
            mapVariables.put("loginacct", loginMember.getLoginacct());
            mapVariables.put("passListener", new PassListener());
            mapVariables.put("refuseListener", new RefuseListener());

            //使用实际的邮箱出错？可能需要使用配置的邮箱服务器？暂时没有解决
            // 启动流程实例
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceById(processDefinition.getId(), mapVariables);

            //流程步骤记忆
            Ticket ticket = ticketService.getTicketByMemberId(loginMember.getId());
            ticket.setPstep("checkemail");
            ticket.setPiid(processInstance.getId());
            ticket.setAuthcode(authcode.toString());

            //更新实名认证流程步骤
            ticketService.updatePiidAndPstep(ticket);

            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();

            result.setSuccess(false);
        }


        return result;
    }

    //验证验证码，完成邮箱验证流程实例
    @ResponseBody
    @RequestMapping("/finishApply")
    public Object finishApply( HttpSession session, String authcode) {
        AjaxResult result = new AjaxResult();

        try {

            // 获取登录会员信息
            Member loginMember = (Member)session.getAttribute(Const.LOGIN_MEMBER);


            //让当前系统用户完成:验证码审核任务.
            Ticket ticket = ticketService.getTicketByMemberId(loginMember.getId()) ;
            if(ticket.getAuthcode().equals(authcode)){
                //完成审核验证码任务;  taskAssignee根据委托人查找任务
                Task task = taskService.createTaskQuery().processInstanceId(ticket.getPiid())
                        .taskAssignee(loginMember.getLoginacct()).singleResult();
                //完成任务
                taskService.complete(task.getId());

                //更新用户申请状态;0-表示未实名认证；1-表示实名认证申请中；2-表示已实名认证
                loginMember.setAuthstatus("1");
                memberService.updateAuthstatus(loginMember);


                //记录流程步骤:
                ticket.setPstep("finishapply");
                ticketService.updatePstep(ticket);
                result.setSuccess(true);
            }else{
                result.setSuccess(false);
                result.setMessage("验证码不正确,请重新输入!");
            }
        } catch( Exception e ) {
            e.printStackTrace();
            result.setSuccess(false);
        }

        return result;

    }
}
