<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <link rel="stylesheet" href="${APP_PATH }/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${APP_PATH }/css/font-awesome.min.css">
    <link rel="stylesheet" href="${APP_PATH }/css/main.css">
    <style>
        .tree li {
            list-style-type: none;
            cursor:pointer;
        }
        table tbody tr:nth-child(odd){background:#F4F4F4;}
        table tbody td:nth-child(even){color:#C00;}
    </style>
</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <div><a class="navbar-brand" style="font-size:32px;" href="#">众筹平台 - 用户维护</a></div>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li style="padding-top:8px;">
                    <div class="btn-group">
                        <button type="button" class="btn btn-default btn-success dropdown-toggle" data-toggle="dropdown">
                            <i class="glyphicon glyphicon-user"></i> ${sessionScope.user.username} <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#"><i class="glyphicon glyphicon-cog"></i> 个人设置</a></li>
                            <li><a href="#"><i class="glyphicon glyphicon-comment"></i> 消息</a></li>
                            <li class="divider"></li>
                            <li><a href="index.html"><i class="glyphicon glyphicon-off"></i> 退出系统</a></li>
                        </ul>
                    </div>
                </li>
                <li style="margin-left:10px;padding-top:8px;">
                    <button type="button" class="btn btn-default btn-danger">
                        <span class="glyphicon glyphicon-question-sign"></span> 帮助
                    </button>
                </li>
            </ul>
            <form class="navbar-form navbar-right">
                <input type="text" class="form-control" placeholder="Search...">
            </form>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
            <div class="tree">

                <jsp:include page="/WEB-INF/jsp/common/menu.jsp"></jsp:include>
            </div>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="glyphicon glyphicon-th"></i> 数据列表</h3>
                </div>
                <div class="panel-body">
                    <form class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input id="queryText" class="form-control has-success" type="text" placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button id="queryBtn" type="button" class="btn btn-warning"><i class="glyphicon glyphicon-search"></i> 查询</button>
                    </form>
                    <button type="button" id="deleteBatchBtn" class="btn btn-danger" style="float:right;margin-left:10px;"><i class=" glyphicon glyphicon-remove"></i> 删除</button>
                    <button type="button" class="btn btn-primary" style="float:right;" onclick="window.location.href='${APP_PATH}/user/toAdd.htm'"><i class="glyphicon glyphicon-plus"></i> 新增</button>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr >
                                <th width="30">#</th>
                                <th width="30"><input id="allCheckbox" type="checkbox"></th>
                                <th>账号</th>
                                <th>名称</th>
                                <th>邮箱地址</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>
                            <tbody>

                            <!--这个地方进行异步数据填充-->
                            </tbody>
                            <tfoot>
                            <tr >
                                <td colspan="6" align="center">
                                    <ul class="pagination">

                                        <!--这个地方进行异步数据填充-->
                                    </ul>
                                </td>
                            </tr>

                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${APP_PATH }/jquery/jquery-2.1.1.min.js"></script>
<script src="${APP_PATH }/bootstrap/js/bootstrap.min.js"></script>
<script src="${APP_PATH }/script/docs.min.js"></script>
<script type="text/javascript" src="${APP_PATH }/jquery/layer/layer.js"></script>

<script type="text/javascript">
<%--    页面加载完就执行的函数--%>
    $(function () {
        $(".list-group-item").click(function(){
            if ( $(this).find("ul") ) {
                $(this).toggleClass("tree-closed");
                if ( $(this).hasClass("tree-closed") ) {
                    $("ul", this).hide("fast");
                } else {
                    $("ul", this).show("fast");
                }
            }
        });

        queryPageUser(1);

        showMenu();
    });



    $("tbody .btn-success").click(function(){
        window.location.href = "assignRole.html";
    });
    $("tbody .btn-primary").click(function(){
        window.location.href = "edit.html";
    });


    //点击上一页的跳转函数
    function pageChange(pageno) {

        //window.location.href = "${APP_PATH }/user/index.do?pageno="+pageno;

        queryPageUser(pageno);
    }


    //ajax请求中传输的json数据

    var jsonObj = {
        "pageno" : 1,
        "pagesize" : 10
    };

    var loadingIndex = -1;
    function queryPageUser(pageno) {

        jsonObj.pageno = pageno;
        //异步请求方式，当页面加载完还没有取到数据，直接发送异步请求
        $.ajax({
            type : "POST",
            data : jsonObj,
            url : "${APP_PATH}/user/doIndex.do",
            beforeSend(){

                loadingIndex = layer.load(2, {time : 10*1000});
                return true;
            },

            success : function(result){
                layer.close(loadingIndex);

                if (result.success){
                    var page = result.page;
                    var data = page.datas;

                    var content = '';
                    //往静态页面中填充数据,   function(i, n)中i表示索引，n表示数组或者集合中的元素，在这里n表示一个user对象
                    $.each(data, function(i, n){

                        content+='     <tr>';

                        content+='         <td>'+(i+1)+'</td>';
                        content+='         <td><input type="checkbox" id="'+n.id+'" name="'+n.loginacct+'"></td>';
                        content+='         <td>'+n.loginacct+'</td>';
                        content+='         <td>'+n.username+'</td>';
                        content+='         <td>'+n.email+'</td>';
                        content+='         <td>';
                        content+='             <button type="button" onclick="window.location.href=\'${APP_PATH }/user/assignRole.htm?id='+n.id+'\'" class="btn btn-success btn-xs"><i class=" glyphicon glyphicon-check"></i></button>';
                        content+='             <button type="button" onclick="window.location.href=\'${APP_PATH}/user/toUpdate.htm?id='+n.id+'\'" class="btn btn-primary btn-xs"><i class=" glyphicon glyphicon-pencil"></i></button>';
                        content+='             <button type="button" onclick="deleteUser('+n.id+',\''+n.loginacct+'\')" class="btn btn-danger btn-xs"><i class=" glyphicon glyphicon-remove"></i></button>';
                        content+='         </td>';
                        content+='     </tr>';

                    });

                    $("tbody").html(content);

                    //页面导航条显示，上一页，下一页
                    var contentBar = '';

                    if(page.pageno==1){

                        contentBar += '<li class="disabled"><a href="#">上一页</a></li>';
                    }else{
                        contentBar += '<li><a href="#" onclick="pageChange('+(page.pageno-1)+')">上一页</a></li>';
                    }

                    for(var i=1; i<=page.totalno; i++){
                        contentBar += '<li';

                        if (page.pageno==i){
                            //这个地方拼串的时候，字符串前面有一个空格，否则显示出来的样式有问题
                            contentBar += ' class="active"';
                        }

                        contentBar += '><a href="#" onclick="pageChange('+i+')">'+i+'</a></li>';

                    }


                    if(page.pageno==page.totalno){

                        contentBar += '<li class="disabled"><a href="#">下一页</a></li>';
                    }else{
                        contentBar += '<li><a href="#" onclick="pageChange('+(page.pageno+1)+')">下一页</a></li>';
                    }

                    $(".pagination").html(contentBar);

                } else {

                    layer.msg(result.message, {time:1000, icon:5, shift:6});
                }
            },

            error : function(){

                layer.msg("加载数据失败！", {time:1000, icon:5, shift:6});
            }
        });
    }

    $("#queryBtn").click(function(){
        var queryText = $("#queryText").val();

        //添加json数据
        jsonObj.queryText = queryText;
        //在此调用这个函数的时候，传入的json数据有三个参数
        queryPageUser(1);
    });


    function deleteUser(id,loginacct) {


        //点击删除按钮弹出框显示，是否删除
        layer.confirm("确认删除【"+loginacct+"】用户吗？",  {icon: 3, title:'提示'}, function(cindex){
            layer.close(cindex);
            $.ajax({

                type : "POST",
                data : {
                    "id" : id
                },
                url : "${APP_PATH}/user/doDelete.do",
                beforeSend : function(){

                    return true;
                },
                success : function(result){

                    if(result.success){
                        window.location.href = "${APP_PATH}/user/index.htm";
                    }else{
                        layer.msg("删除用户失败", {time:1000, icon:5, shift:6});

                    }

                },
                error : function(){

                    layer.msg("删除用户失败", {time:1000, icon:5, shift:6});
                }

            });

        }, function(cindex){
            layer.close(cindex);
        });

    }

    $("#allCheckbox").click(function () {

        var checkStatus = this.checked;
        // alert(checkStatus);
        //根据标签选择找到表单中，input标签中type属性等于checkbox的标签有多少个
        //  $("tbody tr td input[type='checkbox']") 获取到所有复选框，然后让这些复选框的checked的值和顶头的复选框选项一样
        //attr("checked", checkStatus)函数的作用就是给复选框的checked属性赋值，让其与当前选中的顶头复选框的值一样，即选中全部
        $("tbody tr td input[type='checkbox']").prop("checked", checkStatus);

    });


    $("#deleteBatchBtn").click(function () {

        //input:checked这个表示找到input标签里面的checked属性，只有复选框选中的时候才有这个属性
        var selectCheckbox = $("tbody tr td input:checked");

        //判断是否选中删除的用户,selectCheckbox是一个数组
        if (selectCheckbox.length == 0){

            layer.msg("请选择至少一个用户进行删除！请选择用户！", {time:1000, icon:5, shift:6});

            return false;
        }

        /*
        var idStr = "";


        $.each(selectCheckbox, function (i, n) {

            if (i!=0){

                idStr += "&";
            }

            idStr += "id="+n.id;
        });

         */

        var jsonObj = {};
        $.each(selectCheckbox, function (i, n) {
           jsonObj["datas["+i+"].id"] = n.id;
           jsonObj["datas["+i+"].loginacct"] = n.name;
        });



        //点击删除按钮弹出框显示，是否删除
        layer.confirm("确认删除这些用户吗？",  {icon: 3, title:'提示'}, function(cindex){
            layer.close(cindex);
            $.ajax({

                type : "POST",
                // data : idStr,      //url?id=5&id=6

                data : jsonObj,     //json对象

                url : "${APP_PATH}/user/doDeleteBatch.do",
                beforeSend : function(){

                    return true;
                },
                success : function(result){

                    if(result.success){
                        window.location.href = "${APP_PATH}/user/index.htm";
                    }else{
                        layer.msg("删除用户失败", {time:1000, icon:5, shift:6});

                    }

                },
                error : function(){

                    layer.msg("删除用户失败", {time:1000, icon:5, shift:6});
                }

            });

        }, function(cindex){
            layer.close(cindex);
        });

    });

</script>
<!-- 引用共有的js中的函数-->
<script type="text/javascript" src="${APP_PATH }/script/menu.js"></script>
</body>
</html>
