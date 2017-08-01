package com.company.demo.web.auth;

import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.auth.CubaAuthProvider;

import javax.servlet.*;
import java.io.IOException;
import java.util.Locale;

public class NoOpAuthProvider implements CubaAuthProvider {
    @Override
    public void authenticate(String login, String password, Locale messagesLocale) throws LoginException {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }
}