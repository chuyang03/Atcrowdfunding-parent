package com.atguigu.atcrowdfunding.interceptor;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.util.Const;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

public class LoginInterceptor extends HandlerInterceptorAdapter {


    //定义完拦截器之后，需要在springmvc配置文件中配置一下<mvc:interceptor>
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1.定义哪些路径是不需要拦截的（将这些路径称为白名单）
        Set<String> uri = new HashSet<>();
        uri.add("/user/reg.do");
        uri.add("/user/reg.htm");
        uri.add("/index.htm");
        uri.add("/login.htm");
        uri.add("/doLogin.do");
        uri.add("/logout.do");

        //获取请求路径
        String servletPath = request.getServletPath();

        if (uri.contains(servletPath)){
            //如果请求路径在白名单里就放行
            return true;
        }


        //2.判断用户是否登陆，如果登陆就放行
        //首先获取用户
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Const.LOGIN_USER);
        Member member = (Member) session.getAttribute(Const.LOGIN_MEMBER);

        if (user!=null || member!=null){
            //放行
            return true;
        }else {
            //如果用户没有登陆，则跳转到登陆界面
            response.sendRedirect(request.getContextPath()+"/login.htm");

            //继续执行 return false;     执行后，后面的拦截器将不再执行
            return false;
        }
    }
}
