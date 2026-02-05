package io.github.zlemiesz.springemployeeservice.repository;

import io.github.zlemiesz.springemployeeservice.model.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Zbigniew Lemiesz
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = {"employee", "roles"})
    @Query("""
             select distinct ua
             from UserAccount ua
             join ua.employee e
             where e.email =:email
            """)
    Optional<UserAccount> findForLoginByEmail(String email);
}
