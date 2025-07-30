package com.example.p24zip.global.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Value("${MAIL_ADDRESS}")
    private String mailusername;

    @Value("${MAIL_PASSWORD}")
    private String mailpassword;

    @Bean
    public Mailer mailer() {
        return MailerBuilder
            .withSMTPServer("smtp.gmail.com", 587, mailusername, mailpassword)
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .withSessionTimeout(5000)
            .async() // 내부 큐 사용 가능하지만, 우리는 외부에서 제어할 예정
            .buildMailer();
    }
}