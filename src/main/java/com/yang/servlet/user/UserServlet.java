package com.yang.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.yang.pojo.Role;
import com.yang.pojo.User;
import com.yang.service.role.RoleService;
import com.yang.service.role.RoleServiceImpl;
import com.yang.service.user.UserService;
import com.yang.service.user.UserServiceImpl;
import com.yang.utils.Constants;
import com.yang.utils.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

//实现servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从session里面拿id：
        String method = req.getParameter("method");
        if (method.equals("savepwd")) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify")) {
            this.pwdModify(req, resp);
        } else if (method.equals("query")) {
            this.query(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newPassword = req.getParameter("newpassword");
        System.out.println("UserServlet : " + newPassword);
        boolean flag = false;
        System.out.println(attribute + " " + newPassword);
        if (attribute != null && !StringUtils.isNullOrEmpty(newPassword)) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) attribute).getId(), newPassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功，请退出使用新密码登录");
                System.out.println(req.getSession());
                req.getSession().removeAttribute(Constants.USER_SESSION);
                System.out.println(req.getSession());
            } else {
                req.setAttribute("message", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //验证旧密码,session中有旧密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的Map:结果集
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (o == null) {//session失效或者过期
            resultMap.put("result", "seesionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {//输入的密码为空
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) o).getUserPassword();//Session中用户的密码
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JsonArray:阿里巴巴的工具类,转换格式
            /*
            resultMap = [{}...]
            Json格式:{key:value}
             */
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) {
        /*
        查询用户列表
         */
//        从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String tmp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        //获取用户列表
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        List<User> userList = null;
        List<Role> roleList = null;
        //第一次走这个请求一定是第一页，页面大小固定
        int pageSize = 5;//可以把这个写到配置文件，方便后期维护
        int currentPageNo = 1;
        int queryUserRole = 0;
        if (queryUserName == null) {
            queryUserName = "";
        }
        if (tmp != null && !tmp.equals("")) {
            queryUserRole = Integer.parseInt(tmp);//给查询赋值 0,1,2,3
        }
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.parseInt(pageIndex);
            } catch (NumberFormatException e) {
                try {
                    resp.sendRedirect("error.jsp");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        //获取用户的总数(分页：上一页，下一页)
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if(totalCount<1){
            currentPageNo=1;
        }else if(currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }
        //获取用户列表展示
        System.out.println(queryUserName+" "+queryUserRole);
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        roleList = roleService.getRoleList();
        System.out.println("totalCount:"+totalCount);
        System.out.println("currentPageNo:"+currentPageNo);
        System.out.println("pageSize:"+pageSize);
        System.out.println("totalPageCount:"+totalPageCount);
        req.setAttribute("userList",userList);
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        for (User user : userList) {
            System.out.println(user.getUserCode());
        }
        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

    }
}
