package com.kira.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

     private final JavaMailSender mailSender;
     private final SpringTemplateEngine templateEngine;
@Async
     public void sendEmail(
             String to,
             String username,
             EmailTemplate emailTemplate,
             String confirmationURL,
             String activationCode,
             String subject) throws MessagingException {

          String templateName;
          if (emailTemplate == null) {
               templateName = "confirm-email";
          } else {
               templateName = emailTemplate.name();
          }

          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name()
          );

          Map<String, Object> properties = new HashMap<>();
          properties.put("username", username);
          properties.put("confirmationUrl", confirmationURL);
          properties.put("activation_code", activationCode);

          Context context = new Context();
          context.setVariables(properties);

          String template = templateEngine.process(templateName, context);


          helper.setFrom("contact@kiracoding.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(template, true);

          mailSender.send(mimeMessage);

     }
}