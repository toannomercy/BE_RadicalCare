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
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("noreply@demomailtrap.com");

        String htmlContent = """
        <html>
        <head>
            <meta charset='UTF-8'>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f9f9f9;
                    color: #333333;
                    margin: 0;
                    padding: 0;
                }
                .email-container {
                    max-width: 600px;
                    margin: 20px auto;
                    background: #ffffff;
                    border-radius: 8px;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    overflow: hidden;
                }
                .email-header {
                    background-color: #007bff;
                    padding: 20px;
                    text-align: center;
                    color: #ffffff;
                }
                .email-header h1 {
                    font-size: 24px;
                    margin: 0;
                }
                .email-body {
                    padding: 30px;
                    text-align: center;
                }
                .email-body h2 {
                    font-size: 20px;
                    margin: 0 0 10px;
                    color: #333333;
                }
                .email-body p {
                    font-size: 16px;
                    line-height: 1.5;
                    color: #555555;
                }
                .email-button {
                    display: inline-block;
                    margin-top: 20px;
                    padding: 10px 20px;
                    background-color: #007bff;
                    color: #ffffff !important;
                    text-decoration: none;
                    font-size: 16px;
                    border-radius: 5px;
                }
                .email-footer {
                    padding: 20px;
                    text-align: center;
                    font-size: 14px;
                    color: #888888;
                    background: #f9f9f9;
                    border-top: 1px solid #e6e6e6;
                }
                .email-footer a {
                    color: #007bff;
                    text-decoration: none;
                }
            </style>
        </head>
        <body>
            <div class='email-container'>
                <div class='email-header'>
                    <h1>🔒 Đặt lại mật khẩu</h1>
                </div>
                <div class='email-body'>
                    <h2>Xin chào,</h2>
                    <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>
                    <p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>
                    <a href='http://localhost:8080/api/v1/reset-password/shown?token=%s' class='email-button'>Đặt lại mật khẩu</a>
                    <p>Email của bạn: <strong>%s</strong></p>
                </div>
                <div class='email-footer'>
                    <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với <a href='#'>đội hỗ trợ</a>.</p>
                    <p>&copy; 2024 RadicalCare. Mọi quyền được bảo lưu.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(token, to);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
