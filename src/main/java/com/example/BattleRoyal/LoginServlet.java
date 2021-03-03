package com.example.BattleRoyal;

import com.example.BattleRoyal.bean.UserTableEntity;
import com.example.BattleRoyal.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mirage
 * @date 2021/3/3
 */
@WebServlet(name = "register", value = "/register-servlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserTableEntity user = new UserTableEntity();
        user.setEmail(req.getParameter("email"));
        user.setUserName(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        resp.getWriter().println(UserDao.getInstance().save(user));
    }
}
