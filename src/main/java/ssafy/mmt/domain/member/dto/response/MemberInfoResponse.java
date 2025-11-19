package ssafy.mmt.domain.member.dto.response;

public record MemberInfoResponse(
        String username,
        Boolean social,
        String nickname,
        String email
) {

}
