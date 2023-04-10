package org.evergreen.evergreenuaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.evergreen.evergreenuaa.security.filter.RestAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Import(SecurityProblemSupport.class)
@Order(99)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .requestMatchers(req -> req.mvcMatchers("/api/**", "/admin/**", "/authorize/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exp -> exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .authorizeRequests(req -> req
                        .antMatchers("/authorize/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.ignoringAntMatchers("/authorize/**", "/admin/**", "/api/**"))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers( "/error/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
//                .passwordEncoder(myPasswordEncoder())
                .withUser("user")
//                .password("{bcrypt}$2a$10$mNKRQQ5GTW8ndJcSSAVX8OxLoDOJG088RwdZ8cmMqfWjBQqwY8YTG")
                .password(myPasswordEncoder().encode("User@123"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("sec_user")
//                .password("{SHA-1}{zE7LED7/JzjyOIAjnbTgUU+F2ryReSEr3SlCxmAHINs=}5c998a9f7abb066dba3c1bbdcbd9f1d529bc9c65")
                .password(myPasswordEncoder().encode("User@123"))
                .roles("USER");
    }

    PasswordEncoder myPasswordEncoder() {
        val idForDefault = "bcrypt";
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForDefault, (new BCryptPasswordEncoder()));
        encoders.put("SHA-1", (new MessageDigestPasswordEncoder("SHA-1")));
        return new DelegatingPasswordEncoder(idForDefault,encoders);
    }

    private AuthenticationSuccessHandler jsonAuthenticationSuccessHandler() {
        return (req,res,auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(auth));
            log.debug("认证成功~~~");
        };
    }

    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter(objectMapper);
        restAuthenticationFilter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler());
        restAuthenticationFilter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler());
        restAuthenticationFilter.setAuthenticationManager(authenticationManager());
        restAuthenticationFilter.setFilterProcessesUrl("/authorize/login");

        return restAuthenticationFilter;
    }

    private AuthenticationFailureHandler jsonAuthenticationFailureHandler() throws IOException {
        return (req, res, exp) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("UTF-8");
            Map<String,String> errData = new HashMap<>();
            errData.put("title", "认证失败");
            errData.put("details", exp.getMessage());
            res.getWriter().println(objectMapper.writeValueAsString(errData));
            log.debug("认证失败~~~");
        };
    }

    private LogoutSuccessHandler jsonLogoutSuccessHandler() {
        return (req, res, auth) -> {
            if (auth != null && auth.getDetails() != null) {
                req.getSession().invalidate();
            }
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println();
            log.debug("成功退出登录");
        };
    }

}
