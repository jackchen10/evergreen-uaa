package org.evergreen.evergreenuaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.evergreen.evergreenuaa.security.filter.RestAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling(exp -> exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .authorizeRequests(req -> req
                        .antMatchers("/authorize/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.ignoringAntMatchers("/api/**", "/admin/**", "/authorize/**"))
                .formLogin(form -> {
                    try {
                        form.loginPage("/login")
                                .usernameParameter("username")
                                .successHandler(jsonAuthenticationSuccessHandler())
                                .failureHandler(jsonAuthenticationFailureHandler())
                                .permitAll();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .logout(logout -> logout.logoutUrl("/perform_logout")
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            val objectMapper = new ObjectMapper();
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String,String> errData = new HashMap<>();
                            errData.put("title", "退出登录成功");
                            errData.put("details", authentication.getDetails().toString());
                            response.getWriter().println(objectMapper.writeValueAsString(errData));
                            log.debug("退出登录成功~~~");
                        })))
                .rememberMe(rememberMe -> rememberMe
                        .key("someSecret")
                        .tokenValiditySeconds(86400))
                .httpBasic(Customizer.withDefaults());
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

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().mvcMatchers("/static/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("{bcrypt}$2a$10$mNKRQQ5GTW8ndJcSSAVX8OxLoDOJG088RwdZ8cmMqfWjBQqwY8YTG")
                .roles("USER", "ADMIN")
                .and()
                .withUser("sec_user")
                .password("{SHA-1}{zE7LED7/JzjyOIAjnbTgUU+F2ryReSEr3SlCxmAHINs=}5c998a9f7abb066dba3c1bbdcbd9f1d529bc9c65")
                .roles("USER");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        val idForDefault = "bcrypt";
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForDefault, (new BCryptPasswordEncoder()));
        encoders.put("SHA-1", (new MessageDigestPasswordEncoder("SHA-1")));
        return new DelegatingPasswordEncoder(idForDefault,encoders);
    }

}
