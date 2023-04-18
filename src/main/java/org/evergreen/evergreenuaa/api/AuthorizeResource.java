package org.evergreen.evergreenuaa.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.evergreen.evergreenuaa.entity.Auth;
import org.evergreen.evergreenuaa.entity.MfaType;
import org.evergreen.evergreenuaa.entity.User;
import org.evergreen.evergreenuaa.entity.dto.LoginDto;
import org.evergreen.evergreenuaa.entity.dto.SendTotpDto;
import org.evergreen.evergreenuaa.entity.dto.TotpVerificationDto;
import org.evergreen.evergreenuaa.entity.dto.UserDto;
import org.evergreen.evergreenuaa.exception.*;
import org.evergreen.evergreenuaa.service.EmailService;
import org.evergreen.evergreenuaa.service.SmsService;
import org.evergreen.evergreenuaa.service.UserCacheService;
import org.evergreen.evergreenuaa.service.UserService;
import org.evergreen.evergreenuaa.utils.JwtUtil;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/authorize")
@RestController
public class AuthorizeResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;
    private final SmsService smsService;
    private final EmailService emailService;

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

   /* @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        return userService.login(loginDto.getUsername(),loginDto.getPassword());
    }*/

    @PostMapping("/token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        return userService.findOptionalByUsernameAndPassword(loginDto.getUsername(),loginDto.getPassword())
                .map(user -> {
                    //升级密码编码
                    userService.updatePassword(user, loginDto.getPassword());
                    //验证
                    if (!user.isEnabled()) {
                        throw new UserNotEnabledException();
                    }
                    if (!user.isAccountNonExpired()) {
                        throw new UserAccountExpiredException();
                    }
                    if (!user.isAccountNonLocked()) {
                        throw new UserAccountLockedException();
                    }
                    if (!user.isCredentialsNonExpired()) {
                        throw new MyBadCredentialException();
                    }
                    //判断useingMfa，如果是false，直接返回Token
                    if (!user.isUseringMfa()) {
                        try {
                            return ResponseEntity.ok().body(userService.login(loginDto.getUsername(),loginDto.getPassword()));
                        } catch (AuthenticationException e) {
                            log.error("认证登录失败，用户名：{}，异常原因：{}",loginDto.getUsername(),e.getMessage());
                        }
                    }
                    //使用多因子认证
                    val mfaId = userCacheService.cacheUser(user);
                    // "X-Authenticate": "mfa", "realm=" + mfaId
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .header("X-Authenticate", "mfa", "realm=" + mfaId)
                            .build();
                })
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

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

    @PutMapping("/totp")
    public void sendTotp(@Valid @RequestBody SendTotpDto sendTotpDto) {
        userCacheService.retrieveUser(sendTotpDto.getMfaId())
                .flatMap(user -> userService.createTotp(user).map(code -> Pair.of(user, code)))
                .ifPresent(pair -> {
                    log.debug("totp: {}", pair.getSecond());
                    if (sendTotpDto.getMfaType() == MfaType.SMS) {
                        smsService.sendMsg(pair.getFirst().getMobile(), pair.getSecond());
                    } else {
                        emailService.sendEmail(pair.getFirst().getEmail(), pair.getSecond());
                    }
                });
    }

    @PostMapping("/totp")
    public Auth verifyTotp(@Valid @RequestBody TotpVerificationDto totpVerificationDto) {
        return userCacheService.verifyTotp(totpVerificationDto.getMfaId(), totpVerificationDto.getCode())
                .map(User::getUsername)
                .flatMap(userService::findOptionalByUsername)
                .map(userService::loginWithTotp)
                .orElseThrow(InvalidTotpException::new);
    }
}
