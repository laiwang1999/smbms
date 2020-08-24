package com.yang.service.user;

import com.yang.pojo.User;

import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String password);

    //根据用户id修改密码
    public boolean updatePwd(int userCode, String userPassword);

    //查询记录数
    public int getUserCount(String username,int userRole);
    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName,int queryUserRole,int currentPage,int pageSize);
    public boolean add(User user);
}
