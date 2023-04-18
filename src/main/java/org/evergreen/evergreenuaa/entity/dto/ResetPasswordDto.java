package org.evergreen.evergreenuaa.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evergreen.evergreenuaa.entity.MfaType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailOrMobile;

    private MfaType mfaType;
}
