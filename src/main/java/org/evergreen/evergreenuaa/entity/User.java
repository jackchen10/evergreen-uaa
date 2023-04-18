package org.evergreen.evergreenuaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@With
@Data
@Entity
@Table(name = "evergreen_users")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Column(length = 14, unique = false)
    private String mobile;

    @JsonIgnore
    @Column(name = "password_hash",length = 280, nullable = false)
    private String password;

    @Column(length = 255, unique = true, nullable = false)
    private String email;

    @Column(length = 50)
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired;

    /**
     * 是否启用两步认证
     */
    @Builder.Default
    @NotNull
    @Column(name = "using_mfa",nullable = false)
    private boolean useringMfa = false;

    /**
     * 启用两步认证的key
     */
    @JsonIgnore
    @Column(name = "mfa_key", nullable = false)
    private String mfaKey;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "evergreen_users_roles",
            joinColumns = {@JoinColumn(name = "user_id",referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> authorities;

}
