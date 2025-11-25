package ssafy.mmt.common.auth.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.jwt.entitiy.RefreshToken;

import java.time.LocalDateTime;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefreshToken(String refreshToken);

    @Transactional // delete 동작의 경우 transactional 을 열어줘야함 -> 수정이나 삭제 쿼리 수행시 필요
    void deleteByRefreshToken(String refreshToken);

    @Transactional
    void deleteByUsername(String username);

    // 특정일 지난 refresh 토큰 삭제
    @Transactional
    void deleteByCreatedDateBefore(LocalDateTime createdDateBefore);

}