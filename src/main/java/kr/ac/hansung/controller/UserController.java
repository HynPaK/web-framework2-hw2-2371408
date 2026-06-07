package kr.ac.hansung.controller;

import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 💡 4.4절 GET 매핑: 비밀번호 변경 폼 화면 표시
    @GetMapping("/user/password")
    public String changePasswordForm(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "user/password";
    }

    // 💡 4.4절 POST 매핑: 현재 비밀번호 BCrypt 검증 후 새 비밀번호 저장
    @PostMapping("/user/password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("passwordChangeDto") PasswordChangeDto dto,
            BindingResult bindingResult,
            RedirectAttributes ra) {

        // 1. 필수값(빈칸) 자바 순정 코드로 1차 검증 및 처리
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().trim().isEmpty()) {
            bindingResult.rejectValue("currentPassword", "required", "현재 비밀번호를 입력하세요");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
            bindingResult.rejectValue("newPassword", "required", "새 비밀번호를 입력하세요");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().trim().isEmpty()) {
            bindingResult.rejectValue("confirmPassword", "required", "새 비밀번호 확인을 입력하세요");
        }
        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        // 2. 새 비밀번호 8자 이상 자바 코드로 유효성 검증 대체 구현
        if (dto.getNewPassword().length() < 8) {
            bindingResult.rejectValue("newPassword", "size", "새 비밀번호는 8자 이상이어야 합니다");
            return "user/password";
        }

        // 3. [교수님 힌트 ③] 새 비밀번호와 확인 필드 일치 검증
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "새 비밀번호가 일치하지 않습니다");
            return "user/password";
        }

        try {
            // 4. 비즈니스 로직 호출 (현재 로그인한 사용자 ID인 이메일 주소 획득)
            userService.changePassword(userDetails.getUsername(), dto.getCurrentPassword(), dto.getNewPassword());

            // 5. 성공 시 플래시 메시지와 함께 리다이렉트
            ra.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다");
        } catch (IllegalArgumentException e) {
            // 6. 현재 비밀번호 불일치 예외 처리 (화면 렌더링용)
            bindingResult.rejectValue("currentPassword", "wrong", e.getMessage());
            return "user/password";
        }

        return "redirect:/home";
    }
}