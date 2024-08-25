package ru.kata.spring.boot_security.demo.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String redirectUrl = determineRedirectUrl(authentication);
        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Authentication authentication) {
        Collection<? extends SimpleGrantedAuthority> authorities = (Collection<? extends SimpleGrantedAuthority>) authentication.getAuthorities();

        // Проверяем, есть ли у пользователя роль ADMIN
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "/admin"; // Перенаправление для администраторов
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            return "/user"; // Перенаправление для пользователей
        } else {
            return "/"; // По умолчанию, если ни одной роли нет
        }
    }
}