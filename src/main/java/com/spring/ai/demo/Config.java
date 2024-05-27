package com.spring.ai.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class Config {

    @Bean
    @Description("Sends an email and returns the sending status")
    public Function<SendEmailRequest, SendEmailResponse> sendEmail() {
        return new EmailSender();
    }
}
