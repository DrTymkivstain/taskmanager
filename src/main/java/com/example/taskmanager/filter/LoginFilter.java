package com.example.taskmanager.filter;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
@WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        String method = req.getMethod();

        boolean isRegistration = path.startsWith("/users") && method.equals("POST");
        boolean isLogin = path.startsWith("/login") && method.equals("POST");

        if (isRegistration || isLogin) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Unauthorized: Please log in\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        if(req.getServletPath().contains("/admin") && !user.getRole().equals(Role.ADMIN)){
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\": \"Forbidden: Admin rights required\"}");
            return;
        }

        req.setAttribute("currentUser", user);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

