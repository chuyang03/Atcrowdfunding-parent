package com.atguigu.atcrowdfunding.interceptor;

import com.atguigu.atcrowdfunding.bean.Permission;
import com.atguigu.atcrowdfunding.manager.service.PermissionService;
import com.atguigu.atcrowdfunding.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//权限控制拦截器,每个用户登陆后只可以访问该用户所拥有权限下的访问路径
public class AuthInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        //1.获取所有权限
        List<Permission> allPermissions = permissionService.queryAllPermission();

        //所有许可的的所有访问路径
        Set<String> allUris = new HashSet<>();

        for (Permission permission: allPermissions){

            allUris.add("/"+permission.getUrl());
        }

        //2.判断请求路径是否在所有许可范围内
        String servletPath = request.getServletPath();
        if (allUris.contains(servletPath)){

            //3.判断请求路径是否在用户所拥有许可的路径中
            Set<String> myUris = (Set<String>) request.getSession().getAttribute(Const.MY_URIS);
            if (myUris.contains(servletPath)){
                //在许可路径下，放行
                return true;
            }else {
                response.sendRedirect(request.getContextPath()+"/login.htm");
                return false;
            }
        }else {
            //不在拦截范围内放行
            return true;
        }


    }
}
