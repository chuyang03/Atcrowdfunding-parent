package com.atguigu.atcrowdfunding.manager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.manager.service.CertService;
import com.atguigu.atcrowdfunding.manager.service.CerttypeService;
import com.atguigu.atcrowdfunding.util.AjaxResult;

@Controller
@RequestMapping("/certtype")
public class CerttypeController {

	@Autowired
	private CerttypeService certtypeService;
	
	@Autowired
	private CertService certService ;
	
	@RequestMapping("/index")
	public String index(Map<String, Object> map){
		//查询所有资质
		List<Cert> queryAllCert = certService.queryAllCert();		
		map.put("allCert", queryAllCert);
		
		
		//查询资质与账户类型之间关系(表示之前给账户类型分配过资质)
		//从t_account_type_cert这张表中查询出来的数据表示，该用户拥有哪些资质，并将对应资质后面的复选框打勾
		List<Map<String,Object>> certAccttypeList =  certtypeService.queryCertAccttype();
		map.put("certAccttypeList", certAccttypeList);
		
		return "certtype/index";
	}
	
	//在分类管理界面中点击复选框，相应的在数据库中插入数据
	@ResponseBody
	@RequestMapping("/insertAcctTypeCert")
	public Object insertAcctTypeCert( Integer certid, String accttype ) {
		AjaxResult result = new AjaxResult();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("certid", certid);
			paramMap.put("accttype", accttype);
			
			int count = certtypeService.insertAcctTypeCert(paramMap);
			result.setSuccess(count==1);
		} catch ( Exception e ) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		
		return result;
	}

	//取消分类管理的复选框
	@ResponseBody
	@RequestMapping("/deleteAcctTypeCert")
	public Object deleteAcctTypeCert( Integer certid, String accttype ) {
		AjaxResult result = new AjaxResult();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("certid", certid);
			paramMap.put("accttype", accttype);
			
			int count = certtypeService.deleteAcctTypeCert(paramMap);
			result.setSuccess(count==1);
		} catch ( Exception e ) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		
		return result;
	}

}
