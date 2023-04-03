package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendEmailForm;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import com.zerobase.cms.user.service.seller.SignUpSellerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SignUpSellerService signUpSellerService;


    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        } else {
            Customer customer = signUpCustomerService.signUp(form);

            String code = getRandomCode();
            SendEmailForm sendEmailForm = SendEmailForm.builder()
                .from("chk7119@gmail.com")
                .to(form.getEmail())
                .subject("Verification Email!")
                .text(getVeriicationEmailBody(customer.getEmail(), customer.getName(), "customer",
                    code))
                .build();
            mailgunClient.sendEmail(sendEmailForm);
            signUpCustomerService.changeCustomerValidateEmail(customer.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    public void sellerVerify(String email, String code) {
        signUpSellerService.verifyEmail(email, code);
    }

    public String sellerSignUp(SignUpForm form) {
        if (signUpSellerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        } else {
            Seller seller = signUpSellerService.signUp(form);

            String code = getRandomCode();
            SendEmailForm sendEmailForm = SendEmailForm.builder()
                .from("chk7119@gmail.com")
                .to(form.getEmail())
                .subject("Verification Email!")
                .text(getVeriicationEmailBody(seller.getEmail(), seller.getName(), "seller", code))
                .build();
            mailgunClient.sendEmail(sendEmailForm);
            signUpSellerService.changeSellerValidateEmail(seller.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVeriicationEmailBody(String email, String name, String type, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name)
            .append("! Please Click Link for verification\n\n")
            .append("http://localhost:8081/signUp/")
            .append(type)
            .append("/verify?email=")
            .append(email)
            .append("&code=")
            .append(code).toString();

    }
}
