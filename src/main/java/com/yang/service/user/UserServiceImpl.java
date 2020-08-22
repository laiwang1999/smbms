package com.yang.service.user;

import com.yang.dao.BaseDao;
import com.yang.dao.user.UserDao;
import com.yang.dao.user.UserDaoImpl;
import com.yang.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    //业务层都会调用dao层，所以我们要引入dao层
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;
        connection = BaseDao.getConnection();
        //通过业务层调用对应的具体的数据库操作
        user = userDao.getLoginUser(connection, userCode);
        BaseDao.closeResource(connection, null, null);
        return user;
    }

    @Override
    public boolean updatePwd(int userCode, String userPassword) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean flag = false;
        System.out.println("updatePwd : " + userPassword);
        //修改密码
        try {
            if (userDao.updatePwd(connection, userCode, userPassword) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String username, int userRole) {

        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPage, int pageSize) {
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPage, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    @Test
    public void test() {
        UserServiceImpl userService = new UserServiceImpl();
        int userCount = userService.getUserCount(null, 0);
        System.out.println("userCount:" + userCount);
    }
}
