package shop.hooking.hooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shop.hooking.hooking.dto.SessionUser;

import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {
    private final HttpSession httpSession;

    @Operation(summary = "테스트용 카카오 로그인")
    @GetMapping("/")
    public String index(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if(user != null){
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("picture", user.getImage());
        }
        return "index";
    }
}
