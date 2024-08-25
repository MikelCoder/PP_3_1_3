package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;
    private final UserServiceImpl userService;


    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserServiceImpl userService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Конфигурация безопасности HTTP-запросов
        http
                .authorizeRequests()  // Включает авторизацию запросов
                .antMatchers("/admin", "/admin/**").hasAuthority("ADMIN")  // Ограничивает доступ к URL, начинающимся с /admin, только пользователям с ролью ADMIN
                .antMatchers("/user", "/user/**").hasAnyAuthority("ADMIN", "USER")  // Ограничивает доступ к URL, начинающимся с /user, пользователям с ролями ADMIN или USER
                .antMatchers("/login").permitAll()  // Разрешает доступ ко всем на страницу логина
                .anyRequest().authenticated()  // Требует аутентификации для всех остальных запросов
                .and()
                .formLogin().successHandler(successUserHandler)  // Настраивает форму логина и использует обработчик успешного входа
                .permitAll()  // Разрешает доступ к форме логина для всех пользователей
                .and()
                .logout()
                .permitAll();  // Разрешает выход из системы для всех пользователей
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();  // Определяет Bean для кодирования паролей с использованием BCrypt
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Настраивает аутентификацию с использованием пользовательского сервиса
        auth.userDetailsService(
                username -> Optional.of(userService.findUserByLogin(username))  // Загружает пользователя по имени (логину)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"))  // Если пользователь не найден, выбрасывает исключение
        ).passwordEncoder(getPasswordEncoder());  // Указывает кодировщик паролей
    }
}
