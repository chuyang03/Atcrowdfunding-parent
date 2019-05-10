<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="keys" content="">
    <meta name="author" content="">
    <link rel="stylesheet" href="${APP_PATH }/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${APP_PATH }/css/font-awesome.min.css">
    <link rel="stylesheet" href="${APP_PATH }/css/login.css">
    <style>

    </style>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <div><a class="navbar-brand" href="index.html" style="font-size:32px;">尚筹网-创意产品众筹平台</a></div>
        </div>
    </div>
</nav>

<div class="container">

    <!--经过数据业务层使用**.do, 不调用数据库只显示页面，则使用**.htm-->
    <form id="loginForm" action="${APP_PATH }/doLogin.do" method="post" class="form-signin" role="form">
        <%--将取出来的异常信息显示出来--%>
        ${exception.message }
        <h2 class="form-signin-heading"><i class="glyphicon glyphicon-log-in"></i> 用户登录</h2>
        <div class="form-group has-success has-feedback">
            <input type="text" class="form-control" id="floginacct" value="chuyang" name="loginacct" placeholder="请输入登录账号" autofocus>
            <span class="glyphicon glyphicon-user form-control-feedback"></span>
        </div>
        <div class="form-group has-success has-feedback">
            <input type="password" class="form-control" id="fuserpswd"value="123" name="userpswd" placeholder="请输入登录密码" style="margin-top:10px;">
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
        </div>
        <div class="form-group has-success has-feedback">
            <select id="ftype" class="form-control" name="type" >
                <option value="member">会员</option>
                <%--selected关键字表示默认是管理--%>
                <option value="user" selected>管理</option>
            </select>
        </div>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="remember-me"> 记住我
            </label>
            <br>
            <label>
                忘记密码
            </label>
            <label style="float:right">
                <a href="reg.html">我要注册</a>
            </label>
        </div>
        <a class="btn btn-lg btn-success btn-block" onclick="dologin()" > 登录</a>
    </form>
</div>
<script src="${APP_PATH }/jquery/jquery-2.1.1.min.js"></script>
<script src="${APP_PATH }/bootstrap/js/bootstrap.min.js"></script>
<script>
    function dologin() {
        //根据id选择器获得选择器，然后取值
        var floginacct = $("#floginacct");
        var fuserpswd = $("#fuserpswd");
        var ftype = $("#ftype");

        // alert(fuserpswd.val());

        //使用ajax发送异步请求
        $.ajax({
            type : "POST",
            //data为json数据类型
            data : {
                "loginacct" : floginacct.val(),
                "userpswd" : fuserpswd.val(),
                "type" : ftype.val()
            },
            url : "${APP_PATH}/doLogin.do",
            beforeSend : function(){
                //一般作表单数据校验
                return true;
            },
            success : function(result){

                //result的值是这个：{"success":true}或者{"message":"登陆失败！", "success":false}
                if (result.success) {
                    alert("ok");
                }else {
                    alert("not ok");
                }

            },
            error : function(){

                alert("error");
            }
        });

        //提交表单 然后找DispatcherController
        // $("#loginForm").submit();

        // var type = $(":selected").val();
        // if ( type == "user" ) {
        //     window.location.href = "main.html";
        // } else {
        //     window.location.href = "index.html";
        // }
    }
</script>
</body>
</html>