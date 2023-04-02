package com.zerobase.cms.application;

import com.zerobase.cms.domain.SignInForm;
import com.zerobase.cms.domain.model.Customer;
import com.zerobase.cms.exception.CustomException;
import com.zerobase.cms.exception.ErrorCode;
import com.zerobase.cms.service.CustomerService;
import com.zerobase.domain.common.UserType;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm form) {
        Customer c = customerService.findValidCustomerByEmailAndPassword(form.getEmail(), form.getPassword())
            .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));


        return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
    }

}
