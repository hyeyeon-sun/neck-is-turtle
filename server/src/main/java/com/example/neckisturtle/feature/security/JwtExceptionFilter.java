package com.example.neckisturtle.feature.security;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
        } catch (JwtException ex) {
            System.out.println("여기까지 타고감 ? 1111");
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, ex);
            System.out.println("여기까지 타고감 ? 2222");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void setErrorResponse(HttpStatus status, HttpServletResponse res, Throwable ex) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
//        res.setContentType("application/json; charset=UTF-8");
        res.setHeader("Content-type", "application/json; charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("my-header", "hello");
        res.setHeader("access-control-allow-origin", "*");
        res.setHeader("vary", "Origin");
        res.setHeader("vary", "Access-Control-Request-Method");
        res.setHeader("vary", "Access-Control-Request-Headers");

        JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(401, false, ex.getMessage());
        res.getWriter().write(jwtExceptionResponse.convertToJson());
    }
}
