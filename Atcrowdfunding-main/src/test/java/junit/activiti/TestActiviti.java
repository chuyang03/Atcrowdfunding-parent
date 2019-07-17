package junit.activiti;

import com.atguigu.atcrowdfunding.activiti.listener.NoListener;
import com.atguigu.atcrowdfunding.activiti.listener.YesListener;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//对于流程实例，首先部署流程定义，然后在创建流程实例，最后在流程实例上进行相关操作
public class TestActiviti {


    ApplicationContext ioc = new ClassPathXmlApplicationContext("spring/spring-*.xml");
    ProcessEngine processEngine = (ProcessEngine) ioc.getBean("processEngine");

    //1.创建activiti数据库表  23张表
    @Test
    public void test01(){

        System.out.println("processEngine="+processEngine);
    }

    //2.部署流程定义
    @Test
    public void test02(){

        System.out.println("processEngine"+processEngine);

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("MyProcess9.bpmn").deploy();

        System.out.println("deploy="+deploy);
    }

    //3.查询部署流程定义
    @Test
    public void test03(){

        RepositoryService repositoryService = processEngine.getRepositoryService();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();

        for (ProcessDefinition processDefinition: processDefinitionList){
            System.out.println("id="+processDefinition.getId());
            System.out.println("key="+processDefinition.getKey());
            System.out.println("name="+processDefinition.getName());
            System.out.println("version="+processDefinition.getVersion());

            System.out.println("-------------------------");

        }


        long count = processDefinitionQuery.count(); //查询流程定义数
        System.out.println("count="+count);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");


        //获取最后一次部署的流程定义对象
        ProcessDefinition processDefinition = processDefinitionQuery.latestVersion().singleResult();

        System.out.println("id="+processDefinition.getId());
        System.out.println("key="+processDefinition.getKey());
        System.out.println("name="+processDefinition.getName());
        System.out.println("version="+processDefinition.getVersion());

        System.out.println("-------------###------------");

    }


    //4.创建流程实例
    @Test
    public void test04(){

        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().list();


        RuntimeService runtimeService = processEngine.getRuntimeService();
        for (ProcessDefinition processDefinition: list) {

            //启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
            System.out.println("processInstance="+processInstance);
        }


    }

    //5.查询流程实例的任务数据
    @Test
    public void test05(){


        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> taskList1 = taskQuery.taskAssignee("zhangsan").list();//拿到zhangsan的所有任务
        List<Task> taskList2 = taskQuery.taskAssignee("lisi").list();

        for (Task task:taskList1) {
            System.out.println("id="+task.getId());
            System.out.println("name="+task.getName());
            //zhangsan完成任务
            taskService.complete(task.getId());
        }

        System.out.println("---------------");
        for (Task task:taskList2) {
            System.out.println("id="+task.getId());
            System.out.println("name="+task.getName());
            //lisi完成任务
            taskService.complete(task.getId());
        }


        taskList1 = taskQuery.taskAssignee("zhangsan").list();//拿到zhangsan的所有任务
        taskList2 = taskQuery.taskAssignee("lisi").list();

        for (Task task:taskList1) {
            System.out.println("id="+task.getId());
            System.out.println("name="+task.getName());
            //zhangsan完成任务
            taskService.complete(task.getId());
        }

        System.out.println("---------------");
        for (Task task:taskList2) {
            System.out.println("id="+task.getId());
            System.out.println("name="+task.getName());
            //lisi完成任务
        }

    }

    //6.查询历史数据
    @Test
    public void test06(){
        HistoryService historyService = processEngine.getHistoryService();

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();

        //根据id查询已完成的id等于605的流程实例
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.processInstanceId("605").finished().singleResult();

        System.out.println("historicProcessInstance="+historicProcessInstance);
    }

    //6.任务领取
    @Test
    public void test07() {

        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        //这个组的所有任务
        List<Task> list = taskQuery.taskCandidateGroup("tl").list();

        //查询这个组里的某个人的任务有哪些
        long count = taskQuery.taskAssignee("zhangsan").count();
        System.out.println("zhangsan领取任务前的数量："+count);

        for (Task task: list){

            //给zhangsan分配任务
            taskService.claim(task.getId(), "zhangsan");

        }

        //zhangsan领取任务后有多少任务
        taskQuery = taskService.createTaskQuery();
        long count1 = taskQuery.taskAssignee("zhangsan").count();
        System.out.println("zhangsan领取任务后的数量："+count1);
    }


    //8.流程变量
    //如果存在流程变量，在启动流程实例的时候需要该流程变量赋值，否则启动流程变量会出错
    @Test
    public void test08(){

        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().list();


        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("tl", "zhangsan");
        varMap.put("pm", "lisi");

        for (ProcessDefinition processDefinition: list) {

            //启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), varMap);
            System.out.println("processInstance="+processInstance);
        }


    }

    //9.网关，排他网关
    @Test
    public void test09(){

        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().list();


        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("days", "2");

        for (ProcessDefinition processDefinition: list) {

            //启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), varMap);
            System.out.println("processInstance="+processInstance);
        }

    }


    @Test
    public void test091(){

        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskAssignee("zhangsan").list();

        for (Task task:list
             ) {

            taskService.complete(task.getId());
        }

    }


    //9.网关，并行网关
    @Test
    public void test10(){

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().singleResult();


        RuntimeService runtimeService = processEngine.getRuntimeService();


        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        System.out.println("processInstance="+processInstance);

    }

    //11.网关，包含网关（排他+并行）
    @Test
    public void test11(){

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().singleResult();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("days", "5");
        varMap.put("cost", "3000");

        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), varMap);
        System.out.println("processInstance="+processInstance);

    }

    //12.测试流程监听器
    @Test
    public void test12(){

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().latestVersion().singleResult();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("yesListener", new YesListener());
        varMap.put("noListener", new NoListener());

        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), varMap);
        System.out.println("processInstance="+processInstance);

    }

    @Test
    public void test121(){

        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskAssignee("zhangsan").list();

        for (Task task:list
        ) {

            //给某个任务设置流程变量，这个表示组长审批通过
            taskService.setVariable(task.getId(), "flag", "true");
            taskService.complete(task.getId());
        }

    }
}
