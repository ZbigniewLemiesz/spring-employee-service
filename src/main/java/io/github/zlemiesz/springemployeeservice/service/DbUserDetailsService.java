package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.model.UserAccount;
import io.github.zlemiesz.springemployeeservice.repository.UserAccountRepository;
import io.github.zlemiesz.springemployeeservice.security.UserPrincipal;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public DbUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String normalized = email.trim().toLowerCase();

        UserAccount userAccount = userAccountRepository.findForLoginByEmail(normalized)
                .orElseThrow(()->new UsernameNotFoundException("User not found: " + email));

        if(!userAccount.isEnabled()){
            throw new DisabledException("User disabled: " + email);
        }
        List<SimpleGrantedAuthority> authorities = userAccount.getRoles().stream()
                .map(r->new SimpleGrantedAuthority((r.getName())))
                .toList();



        return new UserPrincipal(
                userAccount.getId(),
                userAccount.getEmployee().getEmail(),
                userAccount.getPasswordHash(),
                userAccount.isEnabled(),
                authorities
        );
    }
}
