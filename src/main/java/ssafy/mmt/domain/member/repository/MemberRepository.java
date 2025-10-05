package ssafy.mmt.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
