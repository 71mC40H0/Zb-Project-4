package com.zerobase.cms.application;

import com.zerobase.cms.client.MailgunClient;
import com.zerobase.cms.client.mailgun.SendEmailForm;
import com.zerobase.cms.domain.SignUpForm;
import com.zerobase.cms.domain.model.Customer;
import com.zerobase.cms.exception.CustomException;
import com.zerobase.cms.exception.ErrorCode;
import com.zerobase.cms.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        } else {
            Customer customer = signUpCustomerService.signUp(form);
            String from = "chk7119@gmail.com";

            String code = getRandomCode();
            SendEmailForm sendEmailForm = SendEmailForm.builder()
                .from(from)
                .to(form.getEmail())
                .subject("Verification Email!")
                .text(getVeriicationEmailBody(customer.getEmail(), customer.getName(), code))
                .build();
            mailgunClient.sendEmail(sendEmailForm);
            signUpCustomerService.changeCustomerValidateEmail(customer.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVeriicationEmailBody(String email, String name, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name)
            .append("! Please Click Link for verification\n\n")
            .append("https://localhost:8081/signup/customer/verify?email=")
            .append(email)
            .append("&code=")
            .append(code).toString();

    }
}
