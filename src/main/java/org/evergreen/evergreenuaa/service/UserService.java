package org.evergreen.evergreenuaa.service;

import lombok.RequiredArgsConstructor;
import org.evergreen.evergreenuaa.entity.Auth;
import org.evergreen.evergreenuaa.repository.UserRepository;
import org.evergreen.evergreenuaa.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Auth login(String username, String password) throws AuthenticationException {
        return userRepository.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
    }
}
