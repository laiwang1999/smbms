package com.yang.servlet.user;

import com.yang.pojo.User;
import com.yang.service.user.UserService;
import com.yang.service.user.UserServiceImpl;
import com.yang.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    //Servlet 控制层 调用 业务层 代码
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("login-servlet start-------------");
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中的密码进行对比，调用业务层
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);//这里已经把登录的人查出来了
        if (user != null) {//查有此人，可以登录
            //将用户的信息放在session中
            req.getSession().setAttribute(Constants.USER_SESSION, user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        } else {//无法登录,查无此人
            //转发回登录页面，顺带提示用户名或密码错误
            req.setAttribute("error", "用户名或密码错误");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
