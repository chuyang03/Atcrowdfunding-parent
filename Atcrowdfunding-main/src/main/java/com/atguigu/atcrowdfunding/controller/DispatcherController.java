package com.atguigu.atcrowdfunding.controller;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.Permission;
import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.potal.service.MemberService;
import com.atguigu.atcrowdfunding.util.AjaxResult;
import com.atguigu.atcrowdfunding.util.Const;
import com.atguigu.atcrowdfunding.util.MD5Util;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class DispatcherController {

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    //寻找网站的首页面
    @RequestMapping("/index")
    public String index(){

        return "index";
    }

    //这是主页面的登陆功能，点击登录，去到登陆页面
    //登陆的时候先去cookie中查询用户信息，如果cookie中有该用户信息，则直接跳转到该用户对应权限的页面，而不需要输入用户名和密码登陆
    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpSession session){

        //判断是否需要自动登录
        //如果之前登录过，cookie中存放了用户信息，需要获取cookie中的信息，并进行数据库的验证

        boolean needLogin = true;
        String logintype = null ;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){ //如果客户端禁用了Cookie，那么无法获取Cookie信息

            for (Cookie cookie : cookies) {

                if("logincode".equals(cookie.getName())){

                    String logincode = cookie.getValue();
                    System.out.println("获取到Cookie中的键值对"+cookie.getName()+"===== " + logincode);
                    //loginacct=admin&userpwd=21232f297a57a5a743894a0e4a801fc3&logintype=member
                    String[] split = logincode.split("&");
                    if(split.length == 3){
                        String loginacct = split[0].split("=")[1];
                        String userpwd = split[1].split("=")[1];
                        logintype = split[2].split("=")[1];

                        Map<String,Object> paramMap = new HashMap<String,Object>();
                        paramMap.put("loginacct", loginacct);
                        paramMap.put("userpswd", userpwd);
                        paramMap.put("type", logintype);

                        if("user".equals(logintype)){


                            User dbLogin = userService.queryUserLogin(paramMap);

                            if(dbLogin!=null){
                                session.setAttribute(Const.LOGIN_USER, dbLogin);
                                needLogin = false ;
                            }


                            //加载当前登录用户的所拥有的许可权限.

                            //User user = (User)session.getAttribute(Const.LOGIN_USER);

                            List<Permission> myPermissions = userService.queryPermissionsByUserid(dbLogin.getId()); //当前用户所拥有的许可权限

                            Permission permissionRoot = null;

                            Map<Integer,Permission> map = new HashMap<Integer,Permission>();

                            Set<String> myUris = new HashSet<String>(); //用于拦截器拦截许可权限

                            for (Permission innerpermission : myPermissions) {
                                map.put(innerpermission.getId(), innerpermission);

                                myUris.add("/"+innerpermission.getUrl());
                            }

                            session.setAttribute(Const.MY_URIS, myUris);


                            for (Permission permission : myPermissions) {
                                //通过子查找父
                                //子菜单
                                Permission child = permission ; //假设为子菜单
                                if(child.getPid() == null ){
                                    permissionRoot = permission;
                                }else{
                                    //父节点
                                    Permission parent = map.get(child.getPid());
                                    parent.getChildren().add(child);
                                }
                            }


                            session.setAttribute("permissionRoot", permissionRoot);


                        }else if("member".equals(logintype)){


                            Member dbLogin = memberService.queryMemberLogin(paramMap);

                            if(dbLogin!=null){
                                session.setAttribute(Const.LOGIN_MEMBER, dbLogin);
                                needLogin = false ;
                            }
                        }

                    }
                }
            }
        }

        //是否需要登陆，等于true时需要登陆；如果等于false，那么表示cookie中保存了该用户的信息
        if(needLogin){
            return "login";
        }else{
            if("user".equals(logintype)){
                return "redirect:/main.htm";
            }else if("member".equals(logintype)){
                return "redirect:/member.htm";
            }
        }

        return "login";
    }

    //去到注册页面
    @RequestMapping("/register")
    public String register(){

        return "register";
    }

    //去到会员用户主页面
    @RequestMapping("/member")
    public String member(HttpSession session){

        return "member/member";
    }

    //去到后台管理主页面
    @RequestMapping("/main")
    public String main(HttpSession session){

        return "main";
    }

    //退出登录
    @RequestMapping("/logout")
    public String logout(HttpSession session){


        //用户退出登录时销毁seeion对象，或者清空session域
        session.invalidate();

        //重定向到首页面
        return "redirect:/index.htm";
    }

    //异步请求登陆，使用ajax
    //@ResponseBody  结合Jkson组件，将返回结果转换为字符串，将JSON以流的形式返回给客户端
    @ResponseBody
    @RequestMapping("/doLogin")
    public Object doLogin(String loginacct, String userpswd, String type, String rememberme,
                          HttpSession session, HttpServletResponse response){

        AjaxResult result = new AjaxResult();

        try{
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("loginacct", loginacct);
            //将密码加密
            paramMap.put("userpswd", MD5Util.digest(userpswd));
            paramMap.put("type", type);

            //如果是会员用户
            if ("member".equals(type)){

                //从会员表中查询会员用户的信息
                Member member = memberService.queryMemberLogin(paramMap);
                session.setAttribute(Const.LOGIN_MEMBER, member);

                //判断是否选中【记住我】，如果选中了，则将该用户的信息，用户名、密码等写入到cookei中
                if ("1".equals(rememberme)){

                    String logincode = "\"loginacct="+member.getLoginacct()+"&userpswd="+member.getUserpswd()+"&logintype=member\"";

                    System.out.println("放到cookie中的用户信息："+logincode);

                    Cookie cookie = new Cookie("logincode", logincode);

                    cookie.setMaxAge(60*60*24*14);  //两周时间cookie过期，单位秒
                    cookie.setPath("/");  //表示任何请求路径都可以访问cookie


                    response.addCookie(cookie);
                }

            }else if ("user".equals(type)){

                User user = userService.queryUserLogin(paramMap);

                session.setAttribute(Const.LOGIN_USER, user);

                //判断是否选中【记住我】，如果选中了，登陆成功后就会将该用户的信息，用户名、密码等写入到cookei中
                if ("0".equals(rememberme)){

                    String logincode = "\"loginacct="+user.getLoginacct()+"&userpswd="+user.getUserpswd()+"&logintype=user\"";

                    System.out.println("放到cookie中的用户信息："+logincode);

                    Cookie cookie = new Cookie("logincode", logincode);

                    cookie.setMaxAge(60*60*24*14);  //两周时间cookie过期，单位秒
                    cookie.setPath("/");  //表示任何请求路径都可以访问cookie


                    response.addCookie(cookie);
                }

                //---------------------------------------------
                //加载当前登陆用户所拥有的许可权限
//            User user = (User)session.getAttribute(Const.LOGIN_USER);

                List<Permission> myPermissions = userService.queryPermissionsByUserid(user.getId());

                Permission permissionRoot = null;

                Map<Integer, Permission> map = new HashMap<>();

                //拦截器拦截许可权限
                Set<String> myUris = new HashSet<>();

                for (Permission child: myPermissions){  //假如100次循环

                    map.put(child.getId(), child);

                    myUris.add("/"+child.getUrl()); //用户所拥有的权限的访问路径
                }

                //把登陆用户的拥有访问权限的访问路径集合放到session域中
                session.setAttribute(Const.MY_URIS, myUris);

                for (Permission permission: myPermissions){   //100次循环

                    Permission child = permission;
                    if (child.getPid() == 0){
                        //根节点
                        permissionRoot = permission;
                    }else {

                        //直接根据child的pid，从map中找出他的父节点
                        Permission parent = map.get(child.getPid());

                        //每个permission中都有一个list集合属性来存储孩子节点，
                        // 当遍历完所有节点时，所有的父节点中都封装了一个孩子节点集合属性
                        parent.getChildren().add(child);
                    }
                }

                session.setAttribute("permissionRoot", permissionRoot);
                //---------------------------------------------

            }else {


            }

            //将用户类型返回到前台页面，如果管理用户跳转到后台，如果是会员用户，跳转到会员页面
            result.setData(type);
            result.setSuccess(true);
            //返回的值是一个json类型数据
            //{"success":true}
        }catch (Exception e){
            result.setMessage("登陆失败！");
            e.printStackTrace();
            result.setSuccess(false);

            //{"message":"登陆失败！", "success":false}
        }

        return result;
        //返回的值是一个json类型数据,result的值就是一个json数据，如下所示
        //{"success":true}
    }


    //同步请求  登陆用户
//    @RequestMapping("/doLogin")
//    public String doLogin(String loginacct, String userpswd, String type, HttpSession session){
//
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("loginacct", loginacct);
//        paramMap.put("userpswd", userpswd);
//        paramMap.put("type", type);
//        System.out.println(paramMap.get("loginacct"));
//        System.out.println(paramMap.get("userpswd"));
//
//        User user = userService.queryUserLogin(paramMap);
//
//        session.setAttribute(Const.LOGIN_USER, user);
//
//        //重定向操作可以使得下次刷新浏览器不会重复登陆，即重复提交表单（重定向到另一个页面了）
//        return "redirect:/main.htm";
//    }
}
