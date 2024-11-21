package com.radical.be_radicalcare.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("noreply@demomailtrap.com");

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='background-color: #f4f4f4; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px;'>"
                + "<h2 style='color: #4CAF50;'>Hello,</h2>"
                + "<p style='font-size: 16px;'>We received a request to reset your password. Please click the link below to change your password:</p>"
                + "<p style='font-size: 16px;'>If you didn't request a password reset, you can safely ignore this email.</p>"
                + "<a href='http://localhost:8080/api/v1/auth/reset-password?token=" + token + "' "
                + "style='display: inline-block; background-color: #4CAF50; color: white; padding: 12px 25px; "
                + "text-decoration: none; border-radius: 5px; margin-top: 20px;'>Reset Your Password</a>"
                + "<p style='font-size: 14px; color: #888888; margin-top: 30px;'>"
                + "If you have any questions, feel free to contact our support team."
                + "</p>"
                + "</div>"
                + "</div>"
                + "</body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
