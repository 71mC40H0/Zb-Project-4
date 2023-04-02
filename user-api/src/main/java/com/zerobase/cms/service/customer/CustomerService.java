package com.zerobase.cms.service.customer;

import com.zerobase.cms.domain.model.Customer;
import com.zerobase.cms.domain.repository.CustomerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id, String email) {
        return customerRepository.findByIdAndEmail(id, email);
    }

    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);

    }

}
