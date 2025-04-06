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

        String htmlContent = "<html>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<style>"
                + "  body { font-family: Arial, sans-serif; color: #333; }"
                + "  .container { background-color: #f7f7f7; padding: 20px; }"
                + "  .content { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; }"
                + "  .button { display: inline-block; background-color: #EF5B25; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; margin-top: 20px; }"
                + "  .footer { font-size: 14px; color: #888888; margin-top: 30px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "  <div class='container'>"
                + "    <div class='content'>"
                + "      <h2 style='color: #EF5B25;'>Xin chào,</h2>"
                + "      <p style='font-size: 16px;'>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào liên kết bên dưới để đặt lại mật khẩu:</p>"
                + "      <a href='http://localhost:8080/api/v1/reset-password/shown?token=" + token + "' class='button'>Đặt lại mật khẩu</a>"
                + "      <p style='font-size: 16px;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "      <p class='footer'>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội hỗ trợ của chúng tôi.</p>"
                + "    </div>"
                + "  </div>"
                + "</body>"
                + "</html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
