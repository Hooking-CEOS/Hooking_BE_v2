package shop.hooking.hooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.response.OAuthUserResDto;
import shop.hooking.hooking.service.OAuthUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final OAuthUserService oAuthUserService;

    @Operation(summary = "유저 정보 조회하기")
    @GetMapping("/information")
    public OAuthUserResDto getUserInfo(HttpServletRequest httpRequest) {
        return oAuthUserService.getUser(httpRequest);
    }

    @Operation(summary = "역할 선택하기")
    @PostMapping("/select-role")
    public ResponseEntity<?> selectRole(HttpServletRequest httpRequest, @RequestParam String role) {
        try {
            System.out.println("New Role: " + role);
            oAuthUserService.selectRole(httpRequest, role);
            // 업데이트 성공 시 응답
            return ResponseEntity.ok(Map.of("success", true, "message", "Role updated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            // 업데이트 실패 시 응답
            return ResponseEntity.ok(Map.of("success", false, "error", e.getMessage()));
        }
    }

}

