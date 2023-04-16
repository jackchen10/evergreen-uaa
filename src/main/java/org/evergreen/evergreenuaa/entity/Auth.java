package org.evergreen.evergreenuaa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth implements Serializable {
    private String accessToken;
    private String refreshToken;

}
