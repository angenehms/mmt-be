package ssafy.mmt.common.auth.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.common.auth.jwt.entitiy.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}