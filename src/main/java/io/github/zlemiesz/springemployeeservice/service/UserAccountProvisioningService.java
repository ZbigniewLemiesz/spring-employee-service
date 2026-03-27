package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import io.github.zlemiesz.springemployeeservice.model.UserAccount;

/**
 * @author Zbigniew Lemiesz
 */
public interface UserAccountProvisioningService {
    UserAccount provisionFor(Employee employee);
}
