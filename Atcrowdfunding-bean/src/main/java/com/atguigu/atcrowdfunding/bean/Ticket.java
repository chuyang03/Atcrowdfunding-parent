package com.atguigu.atcrowdfunding.bean;

/**
 *
 * 流程审批单，这个实体类对应的表可以表示当前会员用户正在执行流程实例的哪一步
 */
public class Ticket {

    private Integer id;
    private Integer memberid;

    private String piid;
    private String status;
    private String authcode;
    private String pstep;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberid() {
        return memberid;
    }

    public void setMemberid(Integer memberid) {
        this.memberid = memberid;
    }

    public String getPiid() {
        return piid;
    }

    public void setPiid(String piid) {
        this.piid = piid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getPstep() {
        return pstep;
    }

    public void setPstep(String pstep) {
        this.pstep = pstep;
    }
}
