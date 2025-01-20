package com.lecture.lecture.Controller;

import com.lecture.lecture.Service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                && authentication.getPrincipal().equals("anonymousUser"))) {
            // 사용자가 인증된 경우
            return "redirect:/enrollment";
        }
        // 사용자가 인증되지 않은 경우
        return "redirect:/login";
    }

    // 로그인 페이지 표시
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login"; // Thymeleaf 템플릿 이름
    }


    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}