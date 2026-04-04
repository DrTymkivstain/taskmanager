package com.example.taskmanager.filter;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@WebFilter("/admin/*")
public class RoleFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(RoleFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;

        User user = (User) req.getAttribute("currentUser");

        if (user == null || user.getRole() != Role.ADMIN) {
            logger.warn("Unauthorized admin access attempt by user: {}",
                    (user != null ? user.getEmail() : "Anonymous"));
            ((HttpServletResponse) response).sendError(403, "Admin access required");
            return;
        }

        chain.doFilter(request, response);
    }
}