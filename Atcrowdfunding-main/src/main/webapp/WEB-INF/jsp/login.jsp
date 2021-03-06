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
                <option value="member" selected>会员</option>
                <%--selected关键字表示默认是管理--%>
                <option value="user" >管理</option>
            </select>
        </div>
        <div class="checkbox">
            <label>
                <input id="rememberme" type="checkbox" value="1"> 记住我
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
<script type="text/javascript" src="${APP_PATH }/jquery/layer/layer.js"></script>
<script>
    function dologin() {
        //根据id选择器获得选择器，然后取值
        var floginacct = $("#floginacct");
        var fuserpswd = $("#fuserpswd");
        var ftype = $("#ftype");

        // alert(fuserpswd.val());

        //表单数据校验
        //对于表单数据而言不能用null进行判断，如果文本框什么都不输入，获取的是""（空字符串）
        //$.trim() 函数是去除字符串两端的空格
        if ($.trim(floginacct.val()) == ""){
            // alert("用户账号不能为空，请输入用户名");

            layer.msg("用户账号不能为空，请输入用户名!", {time:1000, icon:5, shift:6}, function(){
                //回调函数

                //val（）函数无参数时是获取数据，有参数时是赋值
                floginacct.val("");
                //使光标定位到当前文本框
                floginacct.focus();

            });


            //return false 表示流程不往后执行
            return false;
        }

        //判断【记住我】这个复选框是否被选中;$("#rememberme")表示jquery对象；$("#rememberme")[0]表示DOM对象
        var flag = $("#rememberme")[0].checked;

        var loadingIndex = -1;
        //使用ajax发送异步请求
        $.ajax({
            type : "POST",
            //data为json数据类型
            data : {
                "loginacct" : floginacct.val(),
                "userpswd" : fuserpswd.val(),
                "type" : ftype.val(),

                //选中复选框【记住我】，值就是1
                "rememberme": flag ? "1":"0"
            },
            url : "${APP_PATH}/doLogin.do",
            beforeSend : function(){

                loadingIndex = layer.msg("处理中", {icon: 16});

                //一般作表单数据校验
                return true;
            },
            success : function(result){

                //result的值是这个：{"success":true}或者{"message":"登陆失败！", "success":false}
                if (result.success) {

                    if ("member"==result.data){

                        //如果验证用户是会员用户，跳转到会员用户界面
                        window.location.href = "${APP_PATH }/member.htm";
                    } else if ("user"==result.data){

                        //密码验证成功后跳转到主页面
                        window.location.href = "${APP_PATH }/main.htm";
                    } else {

                        layer.msg("登陆用户类型不合法！", {time:1000, icon:5, shift:6});
                    }

                }else {
                    layer.msg(result.message, {time:1000, icon:5, shift:6});
                }

            },
            error : function(){

                layer.msg("登陆失败！", {time:1000, icon:5, shift:6});
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