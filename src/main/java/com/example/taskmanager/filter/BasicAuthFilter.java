package com.example.taskmanager.filter;


import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.exception.ValidationException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebFilter("/users/*")
public class BasicAuthFilter implements Filter {
    public static final String POST = "POST";
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.userService = new UserService(new UserDaoJdbcImpl());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (isRegistrationRequest(req)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = req.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                sendUnauthorized(res);
                return;
            }

            String[] credentials = extractCredentials(authHeader);
            User user = userService.authenticate(credentials[0], credentials[1]);

            req.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);

        } catch (AppException e) {
            sendJsonError(res, e);
        }
    }


    private boolean isRegistrationRequest(HttpServletRequest req) {
        return POST.equalsIgnoreCase(req.getMethod()) &&
                (req.getPathInfo() == null || req.getPathInfo().equals("/"));
    }

    private void sendUnauthorized(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setHeader("WWW-Authenticate", "Basic realm=\"TaskManager\"");
        res.setContentType("application/json; charset=utf-8");
        res.getWriter().write("{\"error\": \"Full authentication is required to access this resource\"}");
    }

    private void sendJsonError(HttpServletResponse res, AppException e) throws IOException {
        res.setStatus(e.getStatusCode());
        res.setContentType("application/json; charset=utf-8");
        res.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
    }

    private String[] extractCredentials(String authHeader) {
        try {
            String base64 = authHeader.substring(6).trim();
            String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            String[] values = decoded.split(":", 2);
            if (values.length < 2) throw new ValidationException("Invalid basic auth format");
            return values;
        } catch (Exception e) {
            throw new ValidationException("Failed to decode authentication header");
        }
    }
}
