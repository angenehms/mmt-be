package ssafy.mmt.common.auth.jwt.presentation;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ssafy.mmt.common.auth.jwt.application.JWTService;
import ssafy.mmt.common.auth.jwt.dto.request.RefreshRequestDTO;
import ssafy.mmt.common.auth.jwt.dto.response.JWTResponseDTO;

@RestController
public class JWTController {

    private final JWTService jwtService;

    public JWTController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    // 소셜 로그인 쿠키 방식의 Refresh 토큰 헤더 방식으로 교환

    // Refresh 토큰으로 Access 토큰 재발급 (Rotate 포함)
    @PostMapping(value = "/jwt/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JWTResponseDTO jwtRefreshApi(
            @Validated @RequestBody RefreshRequestDTO dto
    ) {
        return jwtService.refreshRotate(dto);
    }
}
