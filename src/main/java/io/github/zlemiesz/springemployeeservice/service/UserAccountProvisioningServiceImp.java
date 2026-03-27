package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.exception.UserAccountAlreadyExistsException;
import io.github.zlemiesz.springemployeeservice.model.Employee;
import io.github.zlemiesz.springemployeeservice.model.UserAccount;
import io.github.zlemiesz.springemployeeservice.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * @author Zbigniew Lemiesz
 */
public class UserAccountProvisioningServiceImp implements UserAccountProvisioningService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountProvisioningServiceImp(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccount provisionFor(Employee employee) {
        if (employee == null || employee.getId() == null) {
            throw new IllegalArgumentException("Employee must be persisted before provisioning account");
        }

        if (userAccountRepository.existsByEmployee_Id(employee.getId())) {
            throw new UserAccountAlreadyExistsException(employee.getId());
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmployee(employee);
        userAccount.setEnabled(false);
        userAccount.setPasswordHash(
                passwordEncoder.encode((UUID.randomUUID().toString()))
        );

        return userAccountRepository.save(userAccount);
    }
}
