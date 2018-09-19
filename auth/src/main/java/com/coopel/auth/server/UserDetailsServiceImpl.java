package com.coopel.auth.server;

import com.coopel.auth.model.User;
import com.coopel.auth.service.UserDtoService;
import com.coopel.auth.service.UserService;
import com.coopel.common.helper.TokenHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Provider<UserService> userService;
    private final Provider<UserDtoService> userDtoService;

    @Inject
    public UserDetailsServiceImpl(Provider<UserService> userService, Provider<UserDtoService> userDtoService) {
        this.userService = userService;
        this.userDtoService = userDtoService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUser(username);
        return new SpecialUserDetails(
                TokenHelper.tokenUsernameFromId(user.getId()),
                user.getPassword(),
                !user.getArchived(),
                true,
                true,
                true,
                buildGrantedAuthorities(user),
                user.getTokensNotBefore()
        );
    }

    private User findUser(String username) throws UsernameNotFoundException {
        Optional<User> result = Optional.empty();

        if (TokenHelper.isId(username)) {
            int id = TokenHelper.extractId(username);
            result = userService.get().findByIdHideArchived(id);
        } else if (TokenHelper.isEmail(username)) {
            String email = TokenHelper.extractEmail(username);
            result = userService.get().findByEmailHideArchived(email);
        } else if (TokenHelper.isPhone(username)) {
            String phone = TokenHelper.extractPhone(username);
            result = userService.get().findByPhoneHideArchived(phone);
        }

        return result.orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private Collection<GrantedAuthority> buildGrantedAuthorities(User user) {
        return userDtoService.get().buildAuthorities(user).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
