package com.atguigu.atcrowdfunding.manager.controller;


import com.atguigu.atcrowdfunding.bean.Permission;
import com.atguigu.atcrowdfunding.manager.service.PermissionService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    //去到许可树主页面
    @RequestMapping("/index")
    public String index(){

        return "permission/index";
    }

    //去到许可树添加页面
    @RequestMapping("/toAdd")
    public String toAdd(){

        return "permission/add";
    }

    //去到许可树修改页面
    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id, Map map){

        Permission permission = permissionService.getPermissionById(id);

        map.put("permission", permission);
        return "permission/update";
    }

    @ResponseBody
    @RequestMapping("/doUpdate")
    public Object doUpdate(Permission permission){

        AjaxResult result = new AjaxResult();

        try {

            int count = permissionService.updatePermission(permission);

            result.setSuccess(count == 1);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("修改许可树数据失败！");
        }
        return result;
    }

    //异步，删除许可
    @ResponseBody
    @RequestMapping("/deletePermission")
    public Object deletePermission(Integer id){

        AjaxResult result = new AjaxResult();

        try {

            int count = permissionService.deletePermission(id);

            result.setSuccess(count == 1);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("删除许可树数据失败！");
        }
        return result;
    }

    //异步返回许可树数据
    @ResponseBody
    @RequestMapping("/doAdd")
    public Object doAdd(Permission permission){

        AjaxResult result = new AjaxResult();

        try {

            int count = permissionService.savePermission(permission);

            result.setSuccess(count == 1);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("添加许可树数据失败！");
        }
        return result;
    }


    //最后优化一次性查询出所有许可树数据，使用map存数据，比使用嵌套集合遍历快速
    @ResponseBody
    @RequestMapping("/loadData")
    public Object loadData(){

        AjaxResult result = new AjaxResult();

        try {

            List<Permission> root = new ArrayList<>();

            List<Permission> childrenPermissions = permissionService.queryAllPermission();

            Map<Integer, Permission> map = new HashMap<>();
            for (Permission child: childrenPermissions){  //假如100次循环

                map.put(child.getId(), child);
            }

            for (Permission permission: childrenPermissions){   //100次循环

                Permission child = permission;
                if (child.getPid() == 0){
                    //根节点
                    root.add(permission);
                }else {

                    //直接根据child的pid，从map中找出他的父节点
                    Permission parent = map.get(child.getPid());

                    //每个permission中都有一个list集合属性来存储孩子节点，
                    // 当遍历完所有节点时，所有的父节点中都封装了一个孩子节点集合属性
                    parent.getChildren().add(child);
                }
            }

            //上面的一系列操作就是在封装json数据，将这个数据回传给前端页面

            result.setSuccess(true);
            result.setData(root);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("加载许可树数据失败！");
        }
        return result;
    }

    //一次性查询出所有许可树
    /*@ResponseBody
    @RequestMapping("/loadData")
    public Object loadData(){

        AjaxResult result = new AjaxResult();

        try {

            List<Permission> root = new ArrayList<>();

            List<Permission> childrenPermissions = permissionService.queryAllPermission();

            for (Permission permission: childrenPermissions){

                Permission child = permission;
                if (child.getPid() == 0){
                    //根节点
                    root.add(permission);
                }else {

                    for (Permission innerPermission: childrenPermissions){

                        //找到父节点
                        if (child.getPid() == innerPermission.getId()){
                            Permission parent = innerPermission;
                            parent.getChildren().add(child);

                            break;
                        }

                    }
                }
            }

            result.setSuccess(true);
            result.setData(root);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("加载许可树数据失败！");
        }
        return result;
    }

     */


    /*


    @ResponseBody
    @RequestMapping("/loadData")
    public Object loadData(){


        AjaxResult result = new AjaxResult();

        try {

            List<Permission> root = new ArrayList<>();

            //父
            Permission permission = permissionService.getRootPermission();
            root.add(permission);

            //子
            List<Permission> children = permissionService.getChildrenPermissionByPid(permission.getId());

            //设置父子关系
            permission.setChildren(children);


            for (Permission child: children){
                child.setOpen(true);

                List<Permission> innerchildren = permissionService.getChildrenPermissionByPid(child.getId());

                child.setChildren(innerchildren);
            }

            result.setSuccess(true);
            result.setData(root);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("加载许可树数据失败！");
        }
        return result;
    }
     */



    /*
    //采用递归实现许可树
    @ResponseBody
    @RequestMapping("/loadData")
    public Object loadData(){


        AjaxResult result = new AjaxResult();

        try {

            List<Permission> root = new ArrayList<>();

            //父
            Permission permission = permissionService.getRootPermission();
            root.add(permission);

            queryChildrenPermission(permission);

            result.setSuccess(true);
            result.setData(root);

        }catch (Exception e){
            result.setSuccess(false);
            e.printStackTrace();
            result.setMessage("加载许可树数据失败！");
        }
        return result;
    }



    private void queryChildrenPermission(Permission permission){

        List<Permission> children = permissionService.getChildrenPermissionByPid(permission.getId());

        //设置父子关系
        permission.setChildren(children);

        for (Permission child: children){

            queryChildrenPermission(child);
        }

    }

    */
}
