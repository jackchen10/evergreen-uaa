package org.evergreen.evergreenuaa.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.evergreen.evergreenuaa.constant.Constants;
import org.evergreen.evergreenuaa.entity.Auth;
import org.evergreen.evergreenuaa.entity.Role;
import org.evergreen.evergreenuaa.entity.User;
import org.evergreen.evergreenuaa.repository.RoleRepository;
import org.evergreen.evergreenuaa.repository.UserRepository;
import org.evergreen.evergreenuaa.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 登录认证服务
     * @param username
     * @param password
     * @return Auth对象
     * @throws AuthenticationException
     */
    public Auth login(String username, String password) throws AuthenticationException {
        return userRepository.findOptionalByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new Auth(
                        jwtUtil.createAccessToken(user),
                        jwtUtil.createRefreshToken(user)
                )).orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
    }

    public User register(User user) {
        return repository.findOptionalByAuthority(Constants.ROLE_USER)
                .map(role -> {
                    Set<Role> roleSet = new HashSet<>();
                    roleSet.add(role);
                    val userToSave = user.withAuthorities(roleSet)
                            .withPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(userToSave);
                })
                .orElseThrow(() -> new BadCredentialsException("找不到用户名"));
    }

    /**
     * 判断用户名是否存在
     * @param username
     * @return 检查数据库结果 boolean
     */
    public boolean isUsernameExisted(String username) {
        return userRepository.countByUsername(username) > 0;
    }

    /**
     * 判断email是否存在
     * @param email
     * @return 检查数据库结果 boolean
     */
    public boolean isEmailExisted(String email) {
        return userRepository.countByEmail(email) > 0;
    }

    /**
     * 判断手机号是否存在
     * @param mobile
     * @return 检查数据库结果 boolean
     */
    public boolean isMobileExisted(String mobile) {
        return userRepository.countByMobile(mobile) > 0;
    }
}
