package org.evergreen.evergreenuaa.entity.dto;

import lombok.Data;
import org.evergreen.evergreenuaa.annotation.PasswordMatch;
import org.evergreen.evergreenuaa.annotation.ValidEmail;
import org.evergreen.evergreenuaa.annotation.ValidPassword;
import org.evergreen.evergreenuaa.constant.Constants;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;

@PasswordMatch
@Data
public class UserDto implements Serializable {

    @NotNull
    @NotBlank
    @Size(min = 4, max = 50,message = "用户名长度必须在4到50个字符之间")
    private String username;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    private String matchPassword;

    @NotNull
    @ValidEmail
    private String email;

    @NotNull
    @Pattern(regexp = Constants.PATTERN_MOBILE)
    private String mobile;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 50,message = "用户名长度必须在4到50个字符之间")
    private String name;
}