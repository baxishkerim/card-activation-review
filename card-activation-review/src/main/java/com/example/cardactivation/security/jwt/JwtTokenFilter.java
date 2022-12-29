package com.example.cardactivation.security.jwt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Component
//@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;

//    private final UserMapper userMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
//        log.debug("I`m in filter");
//        log.debug("token: " + token);
        if (token == null && !jwtTokenProvider.validateToken(token)) {
//            log.info("error token");
//            log.info("{" +
//                    "    \"status\": \"error\"," +
//                    "    \"data\": {" +
//                    "        \"code\": 2," +
//                    "        \"message\": \"Token validation failed.\"" +
//                    "    }" +
//                    "}");
            response.resetBuffer();
            response.setContentType("application/json;charset=UTF-8");
            response.getOutputStream().print("{\n" +
                    "    \"status\": \"error\",\n" +
                    "    \"data\": {\n" +
                    "        \"code\": 2,\n" +
                    "        \"message\": \"Token validation failed.\"\n" +
                    "    }\n" +
                    "}");
            response.flushBuffer();
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
