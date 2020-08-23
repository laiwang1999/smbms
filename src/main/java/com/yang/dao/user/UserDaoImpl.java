package com.yang.dao.user;

import com.mysql.cj.util.StringUtils;
import com.yang.dao.BaseDao;
import com.yang.pojo.Role;
import com.yang.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {


    @Override
    public User getLoginUser(Connection connection, String userCode) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            try {
                resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql, params);
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setUserPassword(resultSet.getString("userPassword"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setCreatedBy(resultSet.getInt("createdBy"));
                    user.setCreationDate(resultSet.getDate("creationDate"));
                    user.setModifyBy(resultSet.getInt("modifyBy"));
                    user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(null, preparedStatement, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;

    }

    @Override
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        System.out.println("UserDao : " + password);
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if (connection != null) {
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object[] params = {password, id};
            execute = BaseDao.execute(connection, preparedStatement, sql, params);
            BaseDao.closeResource(null, preparedStatement, null);
        }
        return execute;
    }

    //查询用户总数，最难的sql
    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<Object>();//存放我们的参数
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.username like ?");
                list.add("%" + username + "%");//index:0
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            //怎么把list转换成数组
            Object[] params = list.toArray();
            System.out.println("UserDaoImpl->getUserCount:" + sql.toString());//输出完成的sql语句
            rs = BaseDao.execute(connection, preparedStatement, rs, sql.toString(), params);
            while (rs.next()) {
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, preparedStatement, rs);
        }
        return count;
    }

    @Override
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName from smbms_user u, smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<>();//存放我们的参数
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.username like ?");
                list.add("%" + username + "%");//index:0
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            //在数据库中，分页使用limit start
            sql.append(" order by u.creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            //怎么把list转换成数组
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, preparedStatement, rs, sql.toString(), params);
            System.out.println(rs.toString());
            while (rs.next()) {
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("roleName"));

                userList.add(_user);
            }
            BaseDao.closeResource(null, preparedStatement, rs);
        }
        return userList;
    }

    //获取角色列表

}
