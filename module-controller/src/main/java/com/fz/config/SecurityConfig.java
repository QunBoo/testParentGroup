package com.fz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // 禁用 CSRF
                .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()  // 允许健康检查端点无需认证
                .antMatchers("/public/**").permitAll()  // 允许公共端点无需认证
                .antMatchers("/controller/**").permitAll()
                .anyRequest().authenticated()  // 正确：anyRequest 应该在最后
                .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}adminpassword") // 在生产环境中使用加密密码
                .roles("ADMIN");
    }
}
