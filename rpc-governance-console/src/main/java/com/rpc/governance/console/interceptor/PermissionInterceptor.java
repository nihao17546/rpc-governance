package com.rpc.governance.console.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author nihao 2018/9/19
 */
public class PermissionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object obj = request.getSession().getAttribute("currentUser");
        if (obj == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            request.setCharacterEncoding("UTF-8");
            response.getWriter().append("未登录");
            return false;
        }
        if ("guest".equals(obj)) {
            String servletPath = request.getServletPath();
            if ("/changeWeight".equals(servletPath) || "/changeActive".equals(servletPath)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                request.setCharacterEncoding("UTF-8");
                response.getWriter().append("没有权限");
                return false;
            }
        }
        return true;
    }
}
