package com.radical.be_radicalcare.Controllers;

import com.radical.be_radicalcare.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/reset-password")
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordController {

    private final UserService userService;

    // Hiển thị trang reset mật khẩu
    @GetMapping("/shown")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        boolean isTokenValid = userService.isTokenValid(token);

        if (!isTokenValid) {
            return "error_page"; // Trường hợp token không hợp lệ, chuyển đến trang lỗi
        }
        model.addAttribute("token", token);  // Truyền token vào model để sử dụng trong template
        return "shown_reset_password";  // Trả về tên của template Thymeleaf
    }
}
