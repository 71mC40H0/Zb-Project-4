package com.zerobase.cms.user.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendEmailForm;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SendEmailTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Test
    void testSendEmail() {
        //given
        SendEmailForm form = SendEmailForm.builder()
            .from("chk7119@gmail.com")
            .to("mozamp2020@gmail.com")
            .subject("Test email from zerobase")
            .text("Test text")
            .build();

        //when
        String response = mailgunClient.sendEmail(form).getBody();

        //then
        assertTrue(Objects.requireNonNull(response).contains("Queued. Thank you."));
    }
}