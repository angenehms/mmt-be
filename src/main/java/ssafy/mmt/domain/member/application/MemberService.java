package ssafy.mmt.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.domain.member.dto.request.MemberSearchRequest;
import ssafy.mmt.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

//    // @RequiredArgsConstructor 이 붙어서 아래 의존성 주입 코드는 생략해도 됨 -> private final 저렇게만 써도 됨 어노테이션 있어서
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 의존성 주입
    private final MemberRepository memberRepository;

    // 자체 로그인 회원 가입 (존재 여부)
    @Transactional(readOnly = true)
    public Boolean isMemberExist(MemberSearchRequest msr) {
        return memberRepository.isExistByUsername(msr.getUsername());
    }

    // 자체 로그인 회원 가입

    // 자체 로그인

    // 자체 로그인 회원 정보 수정

    // 자체/소셜 로그인 회원 탈퇴

    // 소셜 로그인 (매 로그인시 : 신규 = 가입, 기존 = 업데이트)

    // 자체/소셜 유저 정보 조회

}
