package com.atguigu.atcrowdfunding.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartSystemListener implements ServletContextListener {

    //服务器启动时，创建application对象时需要执行的对象
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        //将项目上下文路径（request.getContextPath()）放到application域中
        ServletContext application = sce.getServletContext();
        String contextPath = application.getContextPath();
        application.setAttribute("APP_PATH", contextPath);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
