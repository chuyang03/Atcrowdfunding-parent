package com.atguigu.atcrowdfunding.listener;

import com.atguigu.atcrowdfunding.bean.Permission;
import com.atguigu.atcrowdfunding.manager.service.PermissionService;
import com.atguigu.atcrowdfunding.util.Const;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StartSystemListener implements ServletContextListener {

    //服务器启动时，创建application对象时需要执行的对象
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        //将项目上下文路径（request.getContextPath()）放到application域中
        ServletContext application = sce.getServletContext();
        String contextPath = application.getContextPath();
        application.setAttribute("APP_PATH", contextPath);


        //2.服务器一启动就加载所有许可路径，并加入到application域中，就不需要每次都到数据库中取

        ApplicationContext ioc = WebApplicationContextUtils.getWebApplicationContext(application);
        PermissionService permissionService = ioc.getBean(PermissionService.class);

        List<Permission> allPermissions = permissionService.queryAllPermission();


        //所有许可的的所有访问路径
        Set<String> allUris = new HashSet<>();

        for (Permission permission: allPermissions){

            allUris.add("/"+permission.getUrl());
        }

        application.setAttribute(Const.ALL_PERMISSION_URI, allUris);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
