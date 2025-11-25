package ssafy.mmt.common.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.common.auth.util.JWTUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization"); // 헤더에서 JWT 를 가지고 오는 Authorization 헤더를 파싱
        if (authorization == null) { // null 이면
            filterChain.doFilter(request, response); // 그 다음 필터로 넘김, 어차피 로그인 안되어 있으면 접근을 거부할 것
            return;
        }

        if (!authorization.startsWith("Bearer ")) { // Bearer 라는 접두사 키워드가 없다고 하면
            throw new ServletException("Invalid JWT token"); // 에러
        }

        // 토큰 파싱 ( 위 조건들이 다 통과가 된다면 )
        String accessToken = authorization.split(" ")[1]; // Bearer 뒷부분을 accessToken 에 담아서 가져오고

        if (JWTUtil.isValid(accessToken, true)) { // 그 스트링 값을 진짜 JWT 가 맞는지 검증

            Long memberId = JWTUtil.getMemberId(accessToken); // memberId principal로 사용
            String username = JWTUtil.getUsername(accessToken);
            String role = JWTUtil.getRole(accessToken);

            // 하나의 시큐리티 컨텍스트를 만든 이후에
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            CustomMemberPrincipal principal = new CustomMemberPrincipal(
                    memberId,
                    username,
                    role,
                    null, // password 필요 없으면 null
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );

            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 하나의 시큐리티 컨텍스트를 만든 이후에

            // 다음 필터로 넘김
            filterChain.doFilter(request, response);

        } else { // 403 응답으로 json 바디에 토큰이 알맞지 않다고 전달
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
            return;
        }

    }

}
