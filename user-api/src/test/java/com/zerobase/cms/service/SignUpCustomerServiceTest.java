package com.zerobase.cms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.cms.domain.SignUpForm;
import com.zerobase.cms.domain.model.Customer;
import com.zerobase.cms.domain.repository.CustomerRepository;
import com.zerobase.cms.exception.CustomException;
import com.zerobase.cms.exception.ErrorCode;
import com.zerobase.cms.service.customer.SignUpCustomerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class SignUpCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SignUpCustomerService service;

    @Test
    @DisplayName("회원 가입")
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
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        //when
        service.signUp(form);

        //then
        verify(customerRepository, times(1)).save(captor.capture());
        assertEquals("test1@gmail.com", captor.getValue().getEmail());
        assertEquals("testname1", captor.getValue().getName());
        assertEquals("test1", captor.getValue().getPassword());
        assertEquals(now, captor.getValue().getBirth());
        assertEquals("01000000000", captor.getValue().getPhone());
        assertFalse(captor.getValue().isVerify());
        System.out.println(captor.getValue());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void testVerifyEmailSuccess() {
        //given
        String code = "abcde12345";
        Customer customer = Customer.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("testname")
            .password("test")
            .birth(LocalDate.of(2023, 1, 1))
            .phone("01012345678")
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode(code)
            .verify(false)
            .build();
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(customer));

        //when
        service.verifyEmail("test@gmail.com", "abcde12345");

        //then
        assertTrue(customer.isVerify());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 일치하는 회원 없음")
    void testVerifyEmail_NotFoundUser() {
        //given
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> service.verifyEmail("test@gmail.com", "abcde12345"));

        //then
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 이미 인증 완료")
    void testVerifyEmail_AlreadyVerified() {
        //given
        String code = "abcde12345";
        Customer customer = Customer.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("testname")
            .password("test")
            .birth(LocalDate.of(2023, 1, 1))
            .phone("01012345678")
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode(code)
            .verify(true)
            .build();
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> service.verifyEmail("test@gmail.com", "abcde12345"));

        //then
        assertEquals(ErrorCode.ALREADY_VERIFIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 잘못된 인증 시도")
    void testVerifyEmail_WrongVerification() {
        //given
        String code = "abcde12345";
        Customer customer = Customer.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("testname")
            .password("test")
            .birth(LocalDate.of(2023, 1, 1))
            .phone("01012345678")
            .verifyExpiredAt(LocalDateTime.now().plusDays(1))
            .verificationCode(code)
            .verify(false)
            .build();
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> service.verifyEmail("test@gmail.com", "12345abcde"));

        //then
        assertEquals(ErrorCode.WRONG_VERIFICATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증 시간 만료")
    void testVerifyEmail_ExpireCode() {
        //given
        String code = "abcde12345";
        Customer customer = Customer.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("testname")
            .password("test")
            .birth(LocalDate.of(2023, 1, 1))
            .phone("01012345678")
            .verifyExpiredAt(LocalDateTime.now().minusDays(2))
            .verificationCode(code)
            .verify(false)
            .build();
        given(customerRepository.findByEmail(anyString()))
            .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> service.verifyEmail("test@gmail.com", "abcde12345"));

        //then
        assertEquals(ErrorCode.EXPIRE_CODE, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 코드, 만료일 설정 성공")
    void testChangeCustomerValidateEmailSuccess() {
        //given
        String code = "abcde12345";
        Customer customer = Customer.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("testname")
            .password("test")
            .birth(LocalDate.of(2023, 1, 1))
            .phone("01012345678")
            .verifyExpiredAt(null)
            .verificationCode(null)
            .verify(false)
            .build();
        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.of(customer));

        //when
        service.changeCustomerValidateEmail(1L, code);

        //then
        assertEquals(code, customer.getVerificationCode());
        assertNotNull(customer.getVerifyExpiredAt());

    }

    @Test
    @DisplayName("이메일 인증 코드, 만료일 설정 실패 - 일치하는 회원 없음")
    void testChangeCustomerValidateEmail_NotFoundUser() {
        //given
        String code = "abcde12345";
        given(customerRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> service.changeCustomerValidateEmail(1L, code));

        //then
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());

    }
}
