package ssafy.mmt.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.domain.member.dto.request.MemberSearchRequest;
import ssafy.mmt.domain.member.entity.Member;
import ssafy.mmt.domain.member.entity.MemberRoleType;
import ssafy.mmt.domain.member.repository.MemberRepository;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

//    // @RequiredArgsConstructor 이 붙어서 아래 의존성 주입 코드는 생략해도 됨 -> private final 저렇게만 써도 됨 어노테이션 있어서
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 의존성 주입
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // [API]  자체 로그인 회원 가입 (존재 여부) =====
    @Transactional(readOnly = true)
    public Boolean isMemberExist(MemberSearchRequest msr) {
        return memberRepository.existsByUsername(msr.getUsername());
    }

    // [API]  자체 로그인 회원 가입 =====
    // 리턴 타입을 Long 으로 받은 이유는 리턴을 해당 member 의 아이디값으로 받기 위해 .. 다른 걸로 하고 싶으면 해도 됨
    @Transactional(readOnly = true)
    public Long createMember(MemberSearchRequest msr) {

        // 회원 존재 여부 검증
        // 프론트에서 검증 한 번 했을텐데 왜 또 할까?
        // -> 프론트를 통해서가 아니라 포스트맨이나 기타 다른 곳에서 백엔드에 직접 쏠 수 있는 경우가 있기에 백엔드에서도 검증로직을 삽입해두는 게 좋음
        if (memberRepository.existsByUsername(msr.getUsername())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        Member member = Member.builder()
                .username(msr.getUsername())
                .password(passwordEncoder.encode(msr.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(MemberRoleType.USER) // 우선 일반 유저로 가입
                .nickname(msr.getNickname())
                .email(msr.getEmail())
                .build();

        return memberRepository.save(member).getMemberId();
    }


    // [API]  자체 로그인 =====
    // return 된 후 자체 로그인 처리로직 수행하기 위해 아래는 사용자의 요청에서 파싱한 username 과 password 를 기반으로 DB 로부터 조회하는 메서드
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder() // User 라는 걸 import 한 거임
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRoleType().name())
                .accountLocked(member.getIsLock())
                .build();
    }

    // [API]  자체 로그인 회원 정보 수정 =====
    @Transactional(readOnly = true)
    public Long updateMember(MemberSearchRequest msr) throws AccessDeniedException {

        // 본인만 수정 가능 검증
        // 현재 스레드에 들고 있는 username 을 들고와 그 값이 msr 의 username 과 동일한지 판단
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(msr.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능합니다!");
        }

        // 조회
        Member member = memberRepository.findByUsernameAndIsLockAndIsSocial(msr.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(msr.getUsername()));

        // 회원 정보 수정
        member.updateMember(msr);

        return memberRepository.save(member).getMemberId();
    }

    // [API]  자체/소셜 로그인 회원 탈퇴 =====

    // [API]  소셜 로그인 (매 로그인시 : 신규 = 가입, 기존 = 업데이트) =====

    // [API]  자체/소셜 유저 정보 조회 =====

}
