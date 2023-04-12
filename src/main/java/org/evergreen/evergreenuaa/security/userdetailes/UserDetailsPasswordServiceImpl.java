package org.evergreen.evergreenuaa.security.userdetailes;

import lombok.RequiredArgsConstructor;
import org.evergreen.evergreenuaa.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {
    private final UserRepository userRepository;

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        return userRepository.findOptionalByUsername(userDetails.getUsername())
                .map(user -> {
                    // withPassword 返回一个新的userDetails对象
                    return (UserDetails)userRepository.save(user.withPassword(newPassword));
                })
                .orElse(userDetails);
    }
}
