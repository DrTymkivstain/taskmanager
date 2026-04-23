package com.example.taskmanager.filter;

import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandlingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        catch (AppException e) {
            logger.warn("Business error", e);
            if (!resp.isCommitted()) {
                resp.reset();
                resp.setStatus(e.getStatusCode());
                JsonUtil.sendJson(resp, Map.of("error", e.getMessage()));
            }
        } catch (Exception e) {
            logger.error("Internal server error ", e);
            if (!resp.isCommitted()) {
                resp.reset();
                resp.setStatus(500);
                JsonUtil.sendJson(resp, Map.of("error", "Internal Server Error. Please try again later or contact support." + e.getMessage()));
            }
        }
    }
}
