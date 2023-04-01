package com.zerobase.cms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.domain.SignUpForm;
import com.zerobase.cms.domain.model.Customer;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpCustomerServiceTest {
    @Autowired
    private SignUpCustomerService service;

    @Test
    void testSignUp() {
        //given
        LocalDate now = LocalDate.now();
        SignUpForm form = SignUpForm.builder()
            .email("test1@gmail.com")
            .name("testname1")
            .password("test1")
            .birth(now)
            .phone("01000000000")
            .build();

        //when
        Customer customer = service.signUp(form);

        //then
        assertNotNull(customer.getId());
        assertEquals("test1@gmail.com", customer.getEmail());
        assertEquals("testname1", customer.getName());
        assertEquals("test1", customer.getPassword());
        assertEquals(now, customer.getBirth());
        assertEquals("01000000000", customer.getPhone());
        assertFalse(customer.isVerify());
        System.out.println(customer);
    }



}
