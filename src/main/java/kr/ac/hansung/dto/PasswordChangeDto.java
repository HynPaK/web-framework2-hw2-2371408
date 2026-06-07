package kr.ac.hansung.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {

    // 💡 현재 로그인한 사용자의 기존 비밀번호 확인용
    private String currentPassword;

    // 💡 새롭게 변경할 비밀번호 (컨트롤러에서 8자 이상 자바 코드로 검증 예정)
    private String newPassword;

    // 💡 오타 방지용 새 비밀번호 재입력 확인 필드
    private String confirmPassword;
}