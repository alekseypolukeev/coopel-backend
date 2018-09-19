package com.coopel.common.email;

import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

@Lazy
@Component
public class EmailService {

    private final JavaMailSender emailSender;

    @Inject
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendHtml(String subject, String to, String body, InlineObject... inlineObjects) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setSubject(subject);
        messageHelper.setTo(to);
        messageHelper.setText(body, true);
        messageHelper.setFrom("Coopel", "Coopel");
        for (InlineObject o : inlineObjects) {
            messageHelper.addInline(o.getContentId(), o.getInputStreamSource(), o.getContentType());
        }

        emailSender.send(message);
    }
}
