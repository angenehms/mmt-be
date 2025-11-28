package ssafy.mmt.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.common.auth.jwt.application.JWTService;
import ssafy.mmt.domain.member.dto.request.MemberVaildRequest;
import ssafy.mmt.domain.member.dto.response.MemberInfoResponse;
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
    private final JWTService jwtService; // 회원탈퇴 시 회원이 가진 토큰들을 모두 제거해야하기 위해 필요

    // [API]  자체 로그인 회원 가입 (존재 여부) =====
    @Transactional(readOnly = true)
    public Boolean isMemberExist(MemberVaildRequest mvr) {
        return memberRepository.existsByUsername(mvr.getUsername());
    }

    // [API]  자체 로그인 회원 가입 =====
    // 리턴 타입을 Long 으로 받은 이유는 리턴을 해당 member 의 아이디값으로 받기 위해 .. 다른 걸로 하고 싶으면 해도 됨
    @Transactional
    public Long createMember(MemberVaildRequest mvr) {

        // 회원 존재 여부 검증
        // 프론트에서 검증 한 번 했을텐데 왜 또 할까?
        // -> 프론트를 통해서가 아니라 포스트맨이나 기타 다른 곳에서 백엔드에 직접 쏠 수 있는 경우가 있기에 백엔드에서도 검증로직을 삽입해두는 게 좋음
        if (memberRepository.existsByUsername(mvr.getUsername())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        Member member = Member.builder()
                .username(mvr.getUsername())
                .password(passwordEncoder.encode(mvr.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(MemberRoleType.USER) // 우선 일반 유저로 가입
                .nickname(mvr.getNickname())
                .email(mvr.getEmail())
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

//        return User.builder() // User 라는 걸 import 한 거임
//                .username(member.getUsername())
//                .password(member.getPassword())
//                .roles(member.getRoleType().name())
//                .accountLocked(member.getIsLock())
//                .build();

        // 🌟🌟🌟 이 부분을 CustomMemberPrincipal 로 변경합니다 🌟🌟🌟
        return new CustomMemberPrincipal(
                member.getMemberId(), // 1. memberId 추가
                member.getUsername(),
                member.getRoleType().name(), // 2. RoleType 문자열로 변경
                member.getPassword(),
                // 3. 권한 목록 생성 (단일 역할이라면 이렇게, 여러 개라면 별도 로직 필요)
                java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + member.getRoleType().name()))
        );
    }

    // [API]  자체 로그인 회원 정보 수정 =====
    @Transactional(readOnly = true)
    public Long updateMember(MemberVaildRequest mvr, Long memberId) throws AccessDeniedException {

        // 본인만 수정 가능 검증
        // 현재 스레드에 들고 있는 username 을 들고와 그 값이 msr 의 username 과 동일한지 판단
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(mvr.getUsername())) {
            throw new AccessDeniedException("계정 정보가 일치하지 않습니다!");
        }

        // DB 조회
        Member member = memberRepository.findByUsernameAndIsLockAndIsSocial(mvr.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(mvr.getUsername()));
        if(memberId != member.getMemberId()) throw new AccessDeniedException("계정 정보가 일치하지 않습니다!");

        // 회원 정보 수정
        member.updateMember(mvr);

        return memberRepository.save(member).getMemberId();
    }

    // [API]  자체/소셜 로그인 회원 탈퇴 =====
    @Transactional
    public void deleteMember(MemberVaildRequest mvr, Long memberId) throws AccessDeniedException {

        // 본인 및 어드민만 삭제 가능 검증
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = sessionUsername.equals(mvr.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_"+MemberRoleType.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        // DB 조회
        Member member = memberRepository.findByUsernameAndIsLockAndIsSocial(mvr.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(mvr.getUsername()));
        if(memberId != member.getMemberId()) throw new AccessDeniedException("계정 정보가 일치하지 않습니다!");

        // 유저 제거
        memberRepository.deleteById((memberId));

        // Refresh 토큰 제거
        jwtService.removeRefreshTokenMember(mvr.getUsername());

    }

    // [API]  소셜 로그인 (매 로그인시 : 신규 = 가입, 기존 = 업데이트) =====

    // [API]  자체/소셜 유저 정보 조회 =====
    @Transactional(readOnly = true)
    public MemberInfoResponse readMember(Long memberId) throws AccessDeniedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));
        if(memberId != member.getMemberId()) throw new AccessDeniedException("계정 정보가 일치하지 않습니다!");

        return new MemberInfoResponse(username, member.getIsSocial(), member.getNickname(), member.getEmail());
    }

}
