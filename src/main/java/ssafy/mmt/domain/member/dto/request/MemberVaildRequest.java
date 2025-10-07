package ssafy.mmt.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberVaildRequest {

    // groups
    public interface existGroup {} // 회원 가입시 username 존재 확인
    public interface addGroup {} // 회원 가입시 // 회원가입의 경우 검증해야하는 로직
    public interface passwordGroup {} // 비밀번호 변경시
    public interface updateGroup {} // 회원 수정시
    public interface deleteGroup {} // 회원 삭제시

    @NotBlank(groups = {existGroup.class, addGroup.class, updateGroup.class, deleteGroup.class}) @Size(min = 4) // 빈값 안되고 최소 사이즈 4
    private String username;
    @NotBlank(groups = {addGroup.class, passwordGroup.class}) @Size(min = 4)
    private String password;
    @NotBlank(groups = {addGroup.class, updateGroup.class})
    private String nickname;
    @Email(groups = {addGroup.class, updateGroup.class}) // 이메일 형태 검증
    private String email;

    // 회원가입의 경우 위 4개의 값을 모두 받아야겠지만
    // 회원탈퇴의 경우 username 만 받아도 될듯? 왜냐면 유니크 하니까! -> 근데 이러면 NotBlank 때문에 오류가 뜨게 됨 -> 그래서 groups 를 설정한거임(NotBlank 어노테이션 참고)
}