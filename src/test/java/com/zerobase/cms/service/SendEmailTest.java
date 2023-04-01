package com.zerobase.cms.service;

import com.zerobase.cms.client.MailgunClient;
import com.zerobase.cms.client.mailgun.SendEmailForm;
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
        String content = mailgunClient.sendEmail(form).getBody();

        //then
        System.out.println(content);
    }
}