package com.rbt.test.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);


        if (auth != null && auth.startsWith("ApiKey")) {
            var key = getApiKeyFromRequest(request);
            if (isApiKeyValid(key)) {
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("write"));
                var authentication = new AnonymousAuthenticationToken(key, "admin", authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getApiKeyFromRequest(HttpServletRequest request) {
        String apiKey = request.getHeader("Authorization");

        if (StringUtils.hasText(apiKey)) {
            return apiKey.substring(7);
        }
        return apiKey;
    }

    private Boolean isApiKeyValid(String key){
        return Objects.equals(key, "DemoKey"); // Should be replaced to work with real life api keys
    }
}
