package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.potal.service.MemberService;
import com.atguigu.atcrowdfunding.potal.service.TicketService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/authcert")
public class AuthcertController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private MemberService memberService;

    @RequestMapping("/index")
    public String index(){

        return "authcert/index";
    }

    //显示某个会员申请认证的信息，是给予审批通过还是拒绝
    @RequestMapping("/show")
    public String show(Integer memberid,Map<String,Object> map){

        Member member = memberService.getMemberById(memberid);

        //一个会员可能对应多个资质，资质里面的属性使用map来封装
        List<Map<String,Object>> list = memberService.queryCertByMemberid(memberid);

        map.put("member", member);
        map.put("certimgs", list);

        return "authcert/show";
    }

    //显示需要审批的页面数据
    @ResponseBody
    @RequestMapping("/pageQuery")
    public Object pageQuery(@RequestParam(value = "pageno",required = false,defaultValue = "1") Integer pageno,
                            @RequestParam(value = "pagesize",required = false,defaultValue = "10") Integer pagesize){

        AjaxResult result = new AjaxResult();

        try {

            Page page = new Page(pageno, pagesize);
            //1.查询后台backuser委托组的任务
            TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey("auth")
                                    .taskCandidateGroup("backuser");

            List<Task> taskList = taskQuery.listPage(page.getStartIndex(), pagesize);

            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

            for (Task task: taskList) {

                Map<String, Object> map = new HashMap<>();
                map.put("taskid", task.getId());
                map.put("taskName", task.getName());

                //2.根据任务查询流程定义(流程定义名称,流程定义版本)
                //task.getProcessDefinitionId()  在任务表中查询任务所在的流程定义的id，然后根据流程定义id查询流程定义
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(task.getProcessDefinitionId()).singleResult();

                map.put("procDefName", processDefinition.getName());
                map.put("procDefVersion", processDefinition.getVersion());

                //3.根据任务查询流程实例(根据流程实例的id查询流程单,查询用户信息)

                Member member = ticketService.queryMemberByPiid(task.getProcessInstanceId());
                map.put("member", member);

                data.add(map);
            }

            //因为前端是从page对象中取数据的，所以需要封装到page对象中
            page.setDatas(data);

            //应该表示backuser这个委托组有多少任务
            Long count = taskQuery.count();

            page.setTotalsize(count.intValue());

            result.setPage(page);

            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("任务查询列表失败");
        }

        return result;
    }



    //审批通过
    @ResponseBody
    @RequestMapping("/pass")
    public Object pass( String taskid, Integer memberid ) {
        AjaxResult result = new AjaxResult();

        try {
            taskService.setVariable(taskid, "flag", true);
            taskService.setVariable(taskid, "memberid", memberid);
            // 传递参数，让流程继续执行；当这个任务完成的时候，流程实例就跳转到监听器
            taskService.complete(taskid);

            result.setSuccess(true);
        } catch ( Exception e ) {
            e.printStackTrace();
            result.setSuccess(false);
        }

        return result;
    }

    //审批拒绝
    @ResponseBody
    @RequestMapping("/refuse")
    public Object refuse(String taskid, Integer memberid) {
        AjaxResult result = new AjaxResult();

        try {
            taskService.setVariable(taskid, "flag", false);
            taskService.setVariable(taskid, "memberid", memberid);
            // 传递参数，让流程继续执行
            taskService.complete(taskid);
            result.setSuccess(true);
        } catch ( Exception e ) {
            e.printStackTrace();
            result.setSuccess(false);
        }

        return result;
    }
}
