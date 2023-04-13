package org.evergreen.evergreenuaa.security.auth.ladp;

import lombok.val;
import org.evergreen.evergreenuaa.security.auth.ldap.LDAPUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@DataLdapTest
public class LDAPUserRepositoryIntegrationTest {
    @Autowired
    LDAPUserRepository ldapUserRepository;

    @Test
    public void LDAPUserRepositoryWithCorrectPasswordSuccessTest() {
        String username = "EricChan";
        String password = "User@123";

        val user = ldapUserRepository.findByUsernameAndPassword(username, password);
        assertTrue(user.isPresent());
    }

    @Test
    public void LDAPUserRepositoryWithWrongPasswordFailureTest() {
        String username = "EricChan";
        String password = "User@123_456";

        val user = ldapUserRepository.findByUsernameAndPassword(username, password);
        assertFalse(user.isPresent());
    }
}
