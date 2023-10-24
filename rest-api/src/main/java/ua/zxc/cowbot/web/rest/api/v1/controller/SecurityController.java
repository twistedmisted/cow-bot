package ua.zxc.cowbot.web.rest.api.v1.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Hidden
@RestController
@RequestMapping("security")
public class SecurityController {

    @PostMapping("login")
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        return "Successful";
    }

    @GetMapping("check")
    public String check() {
        return "Yep";
    }
}
