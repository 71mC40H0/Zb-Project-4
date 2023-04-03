package com.zerobase.cms.user.domain.repository;

import com.zerobase.cms.user.domain.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndEmail(Long id, String email);

    Optional<Customer> findByEmailAndPasswordAndVerifyIsTrue(String email, String password);

    Optional<Customer> findByEmail(String email);

}
