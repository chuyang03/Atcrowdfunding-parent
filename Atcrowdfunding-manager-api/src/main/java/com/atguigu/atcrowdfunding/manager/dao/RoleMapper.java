package com.atguigu.atcrowdfunding.manager.dao;

import com.atguigu.atcrowdfunding.bean.Role;
import com.atguigu.atcrowdfunding.bean.RoleExample;
import com.atguigu.atcrowdfunding.bean.RolePermission;
import com.atguigu.atcrowdfunding.vo.Data;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleMapper {
    int countByExample(RoleExample example);

    int deleteByExample(RoleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByExample(RoleExample example);

    Role selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByExample(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> pageQuery(Map<String, Object> paramMap);

    int queryCount(Map<String, Object> paramMap);

    Role getRole(Integer id);

    int update(Role user);

    int delete(Integer uid);

    int batchDelete(Integer[] uid);

    int batchDeleteObj(Data datas);

    List<Role> queryAllRole();

    List<Integer> queryRoleidByUserid(Integer id);

    void saveUserRole(Integer userid, List<Integer> ids);

    void deleteUserRole(Integer userid, List<Integer> ids);

    void deleteRolePermissionRelationship(Integer roleid);

    int insertRolePermission(RolePermission rp);
}