package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.exception.UserAccountAlreadyExistsException;
import io.github.zlemiesz.springemployeeservice.model.Employee;
import io.github.zlemiesz.springemployeeservice.model.UserAccount;
import io.github.zlemiesz.springemployeeservice.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Zbigniew Lemiesz
 */

@ExtendWith(MockitoExtension.class)
public class UserAccountProvisioningServiceImpTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountProvisioningServiceImp service;

    @Test
    void shouldProvisionDisabledUserAccountWithPlaceholderPassword() {
        Employee employee = new Employee();
        employee.setId(10L);

        when(userAccountRepository.existsByEmployee_Id(10L)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-placeholder");
        when(userAccountRepository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount result = service.provisionFor(employee);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(captor.capture());

        UserAccount saved = captor.getValue();
        assertThat(saved.getEmployee()).isEqualTo(employee);
        assertThat(saved.isEnabled()).isFalse();
        assertThat(saved.getPasswordHash()).isEqualTo("encoded-placeholder");

        assertThat(result.getEmployee()).isEqualTo(employee);
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getPasswordHash()).isEqualTo("encoded-placeholder");
    }

    @Test
    void shouldThrowWhenEmployeeIsNotPersisted() {
        Employee employee = new Employee();

        assertThatThrownBy(() -> service.provisionFor(employee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("persisted");
    }


    @Test
    void shouldThrowWhenAccountAlreadyExists() {
        Employee employee = new Employee();
        employee.setId(20L);

        when(userAccountRepository.existsByEmployee_Id(20L)).thenReturn(true);

        assertThatThrownBy(() -> service.provisionFor(employee))
                .isInstanceOf(UserAccountAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }
}
