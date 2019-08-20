package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
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
@RequestMapping("process")
public class ProcessController {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping("index")
    public String index(){

        return "process/index";
    }

    @ResponseBody
    @RequestMapping("doIndex")
    public Object doIndex(@RequestParam(value = "pageno", required = false, defaultValue = "1") Integer pageno,
                          @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize){

        AjaxResult result = new AjaxResult();

        try {

            Page page = new Page(pageno, pagesize);

            //创建流程定义查询对象
            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

            //page.getStartIndex()获取的是当前页的流程定义开始索引，listPage获取的是当前页的全部流程定义
            //这里出现一个问题
            //查询流程定义集合数据的时候，可能出现了自关联，导致Jackson组件无法将集合序列化为JSON串。
            List<ProcessDefinition> listPage = processDefinitionQuery.listPage(page.getStartIndex(), pagesize);

            //解决方案，将查询出来的流程定义集合，取出来封装到map集合里面，然后在包装成list集合。
            List<Map<String, Object>> mapListPage = new ArrayList<>();
            for (ProcessDefinition prcessDefinition: listPage) {

                Map<String, Object> pdMap = new HashMap<>();
                pdMap.put("id", prcessDefinition.getId());
                pdMap.put("name", prcessDefinition.getName());
                pdMap.put("key", prcessDefinition.getKey());
                pdMap.put("version", prcessDefinition.getVersion());

                mapListPage.add(pdMap);
            }

            //查询流程定义的数量
            Long totalsize = processDefinitionQuery.count();
            page.setDatas(mapListPage);

            page.setTotalsize(totalsize.intValue());

            result.setPage(page);
            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);

            result.setMessage("查询流程定义失败");
            e.printStackTrace();
        }

        return result;
    }
}
