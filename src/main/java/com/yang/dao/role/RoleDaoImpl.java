package com.yang.dao.role;

import com.yang.dao.BaseDao;
import com.yang.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
    @Override
    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        List<Role> roleList = new ArrayList<>();
        if (connection != null) {
            String sql = "select * from smbms_role";
            Object[] Params = {};
            resultSet = BaseDao.execute(connection, pstm, resultSet, sql, Params);
            while (resultSet.next()) {
                Role _role = new Role();
                _role.setId(resultSet.getInt("id"));
                _role.setRoleName(resultSet.getString("roleName"));
                _role.setRoleCode(resultSet.getString("roleCode"));
                roleList.add(_role);
            }
            BaseDao.closeResource(null, pstm, resultSet);
        }
        return roleList;
    }
}
