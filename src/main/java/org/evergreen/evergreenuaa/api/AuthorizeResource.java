package org.evergreen.evergreenuaa.api;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.evergreen.evergreenuaa.entity.Auth;
import org.evergreen.evergreenuaa.entity.dto.LoginDto;
import org.evergreen.evergreenuaa.entity.dto.UserDto;
import org.evergreen.evergreenuaa.service.UserService;
import org.evergreen.evergreenuaa.utils.JwtUtil;
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

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserDto userDto) {
        return userDto;
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
