package com.atguigu.atcrowdfunding.manager.controller;

import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Page;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping("/index")
    public String index(){

        return "process/index";
    }

    @RequestMapping("/showimg")
    public String showimg(){

        return "process/showimg";
    }

    //显示流程管理列表
    @ResponseBody
    @RequestMapping("/doIndex")
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

    //上传流程定义文件
    @ResponseBody
    @RequestMapping("/deploy")
    public Object deploy(HttpServletRequest request){

        AjaxResult result = new AjaxResult();

        try {

            //将请求包装,这个是表单提交请求
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

            MultipartFile processDefFile = multipartHttpServletRequest.getFile("processDefFile");

            //部署流程定义文件; processDefFile.getOriginalFilename()获取的是原始上传文件的名字
            repositoryService.createDeployment()
                    .addInputStream(processDefFile.getOriginalFilename(), processDefFile.getInputStream())
                    .deploy();

            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);

            result.setMessage("上传流程定义失败");
            e.printStackTrace();
        }

        return result;
    }

    //删除流程定义
    @ResponseBody
    @RequestMapping("/doDelete")
    public Object doDelete(String id){

        AjaxResult result = new AjaxResult();

        try {

            //根据流程定义id查询出流程定义
            ProcessDefinition processDefinition =
                    repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();

            //获取流程定义的部署id，进行级联删除，true表示级联删除。将相关数据都删除
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);

            result.setMessage("删除流程定义失败");
            e.printStackTrace();
        }

        return result;
    }


    @ResponseBody
    @RequestMapping("/showimgProDef")
    public void showimgProDef(String id, HttpServletResponse response) throws IOException {

        //根据流程定义id查询出流程定义
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();

        //processDefinition.getDiagramResourceName() 获取的是流程定义对应的图片名字
        InputStream resourceAsStream =
                repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());

        OutputStream outputStream = response.getOutputStream();

        //将输入流直接写到输出流中，返回给前端二进制数据，显示图片
        IOUtils.copy(resourceAsStream, outputStream);
    }
}
