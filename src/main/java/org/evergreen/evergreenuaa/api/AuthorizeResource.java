package org.evergreen.evergreenuaa.api;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.evergreen.evergreenuaa.entity.Auth;
import org.evergreen.evergreenuaa.entity.User;
import org.evergreen.evergreenuaa.entity.dto.LoginDto;
import org.evergreen.evergreenuaa.entity.dto.UserDto;
import org.evergreen.evergreenuaa.exception.DuplicateProblemException;
import org.evergreen.evergreenuaa.service.UserService;
import org.evergreen.evergreenuaa.utils.JwtUtil;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RequestMapping("/authorize")
@RestController
public class AuthorizeResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 注册新用户，验证是否存在，并赋予角色
     * @param userDto
     * @return 注册是否成功的布尔值
     */
    @PostMapping("/register")
    public boolean register(@Valid @RequestBody UserDto userDto) {
        //1.检查username,email,mobile都是唯一的，因此要查询数据库以确保唯一性
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblemException("用户名重复");
        }
        if (userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblemException("Email重复");
        }
        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblemException("手机号码重复");
        }
        val user = User.builder()
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .mobile(userDto.getMobile())
                .password(userDto.getPassword())
                .build();
        //2.我们要把userDto转成User对象，并赋予一个角色（ROLE_USER）,然后保存
        return userService.register(user) != null;
    }

    @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        return userService.login(loginDto.getUsername(),loginDto.getPassword());
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization,
                             @RequestParam String refreshToken) throws AccessDeniedException {
        val PREFIX = "Bearer";
        val accessToken = authorization.replace(PREFIX, "");
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateAccessTokenWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.createAccessTokenWithRefreshToken(refreshToken), refreshToken);
        } else {
            throw new AccessDeniedException("访问被拒绝");
        }

    }
}
