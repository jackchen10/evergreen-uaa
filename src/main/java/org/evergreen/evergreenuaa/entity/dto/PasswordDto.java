package org.evergreen.evergreenuaa.entity.dto;


import lombok.Data;
import org.evergreen.evergreenuaa.annotation.ValidPassword;

import java.io.Serializable;

@Data
public class PasswordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oldPassword;

    @ValidPassword
    private String newPassword;
}
