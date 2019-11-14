package com.exebar.poc.spring.boot.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RuntimeExceptionResolverFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            PrintWriter responseStream = response.getWriter();
            responseStream.write("{ message : " + ex.getMessage() + " }");
            responseStream.close();
        }
    }
}
