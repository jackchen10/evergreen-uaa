package org.evergreen.evergreenuaa.security.userdetailes;

import lombok.RequiredArgsConstructor;
import org.evergreen.evergreenuaa.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户名" + username + "不存在"));
    }
}
