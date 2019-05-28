package com.atguigu.atcrowdfunding.util;


import com.atguigu.atcrowdfunding.bean.User;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public class Page {

    private Integer pageno; //当前页码
    private Integer pagesize; //页面多少条数据
    private List datas;
    private Integer totalsize; //总共多少条数据
    private Integer totalno;  // 总共多少页


    public Page(Integer pageno, Integer pagesize){

        if (pageno<=0){
            pageno = 1;
        }else {
            this.pageno = pageno;
        }

        if (pageno<=0){
            pageno = 10;
        }else {
            this.pagesize = pagesize;
        }

    }

    public Integer getPageno() {
        return pageno;
    }

    public void setPageno(Integer pageno) {
        this.pageno = pageno;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public List getDatas() {
        return datas;
    }

    public void setDatas(List<User> datas) {
        this.datas = datas;
    }

    public Integer getTotalsize() {
        return totalsize;
    }

    public void setTotalsize(Integer totalsize) {
        this.totalsize = totalsize;

        //计算总页码
        this.totalno = (totalsize%pagesize == 0)?(totalsize/pagesize):(totalsize/pagesize+1);
    }

    public Integer getTotalno() {
        return totalno;
    }

    private void setTotalno(Integer totalno) {
        this.totalno = totalno;
    }

    public Integer getStartIndex(){
        return (this.pageno-1)*pagesize;
    }
}
