package com.yang.dao.user;

import com.yang.pojo.Role;
import com.yang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到登录的用户
    public User getLoginUser(Connection connection, String userCode);

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;

    //根据用户名或者角色，查询用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException;

    //获取用户列表
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException;

    public int add(Connection connection, User user) throws SQLException;
}

