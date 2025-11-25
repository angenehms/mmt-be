package ssafy.mmt.common.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ssafy.mmt.common.auth.jwt.application.JWTService;
import ssafy.mmt.common.auth.util.JWTUtil;

import java.io.IOException;

@Component
@Qualifier("LoginSuccessHandler")
// 자체로그인과 소셜로그인 모두 각각 성공 핸들러에서 JWT 를 발급해줄건데 동일하게 AuthenticationSuccessHandler 를 구현하면 bean 등록시 중복돼서 등록될 수 있음
// 그래서 여기 핸들러에는 @Qualifier 로 이름을 부여해줌
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    private final JWTService jwtService;

    public LoginSuccessHandler(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    // 로그인이 수행된 이후에 아래 onAuthenticationSuccess 메서드가 수행되기 때문에
    // 받는 인자로는 요청에서 넘어온 request, 응답해줄 response, 로그인 성공시 만들어진 객체 authentication(여기선 username 과 role 을 뽑아낼 수 있음 -> 이걸 기반으로 토큰 생성)
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // username, role
        String username =  authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        Long memberId = (Long) authentication.getPrincipal();

        // JWT(Access/Refresh) 발급
        String accessToken = JWTUtil.createJWT(username, role, memberId, true);
        String refreshToken = JWTUtil.createJWT(username, role, memberId, false);

        // 발급한 Refresh DB 테이블 저장 (Refresh whitelist)
        // 따로 백엔드에 모아 관리할 것이기 때문
        jwtService.addRefreshToken(username, refreshToken);

        // 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // json 바디에 넣어 전달할 것임 -> 아래는 json 바디 포맷 -> 첫번째 %s 는 accessToken, 두번째 %s 는 refreshToken
        String json = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(json);
        response.getWriter().flush();

    }
    // 이렇게 위처럼 성공 핸들러를 만들었고, 만들었다고 된 게 아니라 이제 로그인 필터에 등록시켜줘야함
    // -> LoginFilter 내 successfulAuthentication 를 오버라이딩 시켜서 등록시켜줄 것!
    // -> 이 메서드는 로그인이 되면 어떤걸 수행할지 물어보는 메서드(그래서 저 메서드에 System.out.print 하면 로그인 성공시 프린트 찍힘)
    // -> 등록하는 방법 -> onAuthenticationSuccess 가 Bean 으로 등록되어 있기 때문에 로그인 필터의 successfulAuthentication 에 의존성을 주입해주면 됨

}
