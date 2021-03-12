package com.example.BattleRoyal;

import com.example.BattleRoyal.bean.UserTableEntity;
import com.example.BattleRoyal.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Mirage
 * @date 2021/3/3
 */
@WebServlet(name = "register", value = "/register-servlet")
public class LoginServlet extends HttpServlet {
//    192.168.0.161

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        String hostAddress = inetAddress.getHostAddress();
        System.out.println(getIpAddr(req));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserTableEntity user = new UserTableEntity();
        user.setEmail(req.getParameter("email"));
        user.setUserName(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        resp.getWriter().println(UserDao.getInstance().save(user));
    }


    /**
     * 第一种方法
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
