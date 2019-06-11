function showMenu(){
    //获取整个访问url地址
    var href = window.location.href;
    //获取本机ip
    var host = window.location.host;

    var index = href.indexOf(host);

    var path = href.substring(index+host.length);

    //var contextPath = "${APP_PATH}";     //获取的是localhost：8080

    var alink = $(".list-group a[href*='"+path+"']");

    //设置选中的那个菜单变红
    alink.css("color", "red");

    alink.parent().parent().parent().removeClass("tree-closed");

    alink.parent().parent().show();

}