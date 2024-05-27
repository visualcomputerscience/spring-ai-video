package com.spring.ai.demo;

import java.util.function.Function;

record SendEmailRequest(String receiverEmail, String title, String content) {}

record SendEmailResponse(boolean sendStatus) {}

public class EmailSender implements Function<SendEmailRequest, SendEmailResponse> {

    @Override
    public SendEmailResponse apply(SendEmailRequest sendEmailRequest) {
        // You can have any code here :-D
    }
}
