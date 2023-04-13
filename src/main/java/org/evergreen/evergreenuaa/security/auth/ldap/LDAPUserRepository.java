package org.evergreen.evergreenuaa.security.auth.ldap;

import org.springframework.data.ldap.repository.LdapRepository;

import java.util.Optional;

public interface LDAPUserRepository extends LdapRepository<LDAPUser> {

    Optional<LDAPUser> findByUsernameAndPassword(String username, String password);
}
