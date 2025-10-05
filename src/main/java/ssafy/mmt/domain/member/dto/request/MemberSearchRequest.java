package ssafy.mmt.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchRequest {
    private String username;
    private String password;
    private String nickname;
    private String email;
}
