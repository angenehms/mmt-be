package ssafy.mmt.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByUsername(String username);

    // 회원 정보 수정시 자체 로그인 여부, 잠김 여부를 확인
    // 왜냐면 잠겨있으면 admin 이 잠궈둔 계정을 강제로 바꾸지 않도록 잠겨있지 않은 경우에만 수정 가능하도록
    // 자체/소셜 검증 안할경우 사용자가 강제로 소셜로그인 데이터를 바꿀 수 있는 우려를 방지
    Optional<Member> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);
}

