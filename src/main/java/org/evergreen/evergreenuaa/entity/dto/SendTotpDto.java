package org.evergreen.evergreenuaa.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evergreen.evergreenuaa.entity.MfaType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendTotpDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private MfaType mfaType = MfaType.SMS;

    @NotNull
    private String mfaId;
}
