package ssafy.mmt.common.auth.jwt.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.jwt.dto.request.RefreshRequestDTO;
import ssafy.mmt.common.auth.jwt.dto.response.JWTResponseDTO;
import ssafy.mmt.common.auth.jwt.entitiy.RefreshToken;
import ssafy.mmt.common.auth.jwt.repository.RefreshTokenRepository;
import ssafy.mmt.common.auth.util.JWTUtil;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 소셜 로그인 성공 후 쿠키(Refresh) -> 헤더 방식으로 응답 <-- 이건 추후에 작성
    // restful 하게 설계하게되면 쿠키형태로 발급받게 되고 헤더 형식으로 통합하기 위해서는 다시 백엔드로 보내 쿠키를 검증하고 헤더로 바꿔주는 과정이 필요함

    // Refresh 토큰으로 Access 토큰 재발급 로직 (Rotate 포함) <-- 이건 추후에 작성
    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDTO dto) {

        String refreshToken = dto.getRefreshToken();

        // Refresh 토큰 검증
        Boolean isValid = JWTUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = JWTUtil.getUsername(refreshToken);
        String role = JWTUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = JWTUtil.createJWT(username, role, true);
        String newRefreshToken = JWTUtil.createJWT(username, role, false);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshToken newRefreshEntity = RefreshToken.builder()
                .username(username)
                .refreshToken(newRefreshToken)
                .build();

        removeRefreshToken(refreshToken);
        refreshTokenRepository.save(newRefreshEntity);

        return new JWTResponseDTO(newAccessToken, newRefreshToken);
    }

    // JWT Refresh 토큰 발급 후 저장 메소드
    @Transactional
    public void addRefreshToken(String username, String refreshToken) {
        // 지금은 인자에서 username 을 받았지만 로그인 되어있는 시큐리티 컨텍스트 홀더로부터 꺼내와도 됨

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    // JWT Refresh 존재 확인 메소드
    @Transactional(readOnly = true) // 오로지 조회만 하기에
    public Boolean isRefreshTokenExist(String refreshToken) {
        return refreshTokenRepository.existsByRefreshToken(refreshToken);
    }

    // JWT Refresh 토큰 삭제 메소드
    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    // 특정 유저 Refresh 토큰 모두 삭제 (탈퇴)
    @Transactional
    public void removeRefreshTokenMember(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
