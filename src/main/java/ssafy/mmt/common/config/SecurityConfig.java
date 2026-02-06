package ssafy.mmt.common.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ssafy.mmt.common.auth.filter.JWTFilter;
import ssafy.mmt.common.auth.filter.LoginFilter;
import ssafy.mmt.common.auth.handler.RefreshTokenLogoutHandler;
import ssafy.mmt.common.auth.jwt.application.JWTService;
import ssafy.mmt.domain.member.entity.MemberRoleType;
import ssafy.mmt.domain.member.repository.MemberRepository;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final JWTService jwtService;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler, JWTService jwtService) { // Qualifier 로 확실히 구분해 의존성 주입받기
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.jwtService = jwtService;
    }

    // 커스텀 자체 로그인 필터를 위한 AuthenticationManager Bean 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 권한 계층 -> admin 계정이 높은 role 인지, user 계정이 높은 role 인지 계층을 구분해주는 Bean
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(MemberRoleType.ADMIN.name()).implies(MemberRoleType.USER.name())
                .build();
    }

    // 비밀번호 단방향(BCrypt) 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 허용 포트
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용 메서드
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie")); // 받는 헤더 값
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder, MemberRepository memberRepository) throws Exception {

        // CSRF 보안 필터 disable (커스텀 세팅) - stateless 한 서버에서 필요 없기에 꺼주고
        http
                .csrf(AbstractHttpConfigurer::disable);

        // CORS 설정 (커스텀 세팅) - 리액트, 스프링과 같이 프론트, 백에 서로 다른 오리진을 가지는 경우에는 CORS 설정이 필수
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 기본 로그아웃 필터 + 커스텀 Refresh 토큰 삭제 핸들러 추가
        http
                .logout(logout -> logout
                        .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService)));

        // 기본 Form 기반 인증 필터들 disable (커스텀 세팅) - multipart form 데이터 형태로 받지 않고 우리는 json body 기반으로 받을 거기 때문에
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // 기본 Basic 인증 필터 disable (커스텀 세팅) - httpBasic 기반의 로그인도 하지 않을 거기 때문에 꺼주고
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 인가 (커스텀 세팅) - 컨트롤러 api 에 대해 접근을 허용할건지 말건지 결정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                        .requestMatchers("/actuator/**").permitAll() // 모니터링용 엔드포인트 허용

                        .requestMatchers(HttpMethod.POST, "/api/*/member/exist", "/api/*/member").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/*/member").hasRole(MemberRoleType.USER.name())
                        .requestMatchers(HttpMethod.PUT, "/api/*/member").hasRole(MemberRoleType.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/*/member").hasRole(MemberRoleType.USER.name())

                        .anyRequest().authenticated()
                );

        // 예외 처리 (커스텀 세팅)
        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답 // 로그인을 하지 않은 상태로 접근한 경우
                        })
                        .accessDeniedHandler((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답 // 권한이 없는 경우
                        })
                );

        // 커스텀 필터 추가 ( addFilterBefore -> 어떤 특정 필터 앞, 즉 before 에 추가 )
        http // UsernamePasswordAuthenticationFilter.class 가 기준이 되는 필터고 그 앞에 LoginFilter 객체를 찍어서 배치함
                .addFilterBefore(
                        new LoginFilter(
                                authenticationManager(authenticationConfiguration),
                                loginSuccessHandler,
                                memberRepository
                        ),
                        UsernamePasswordAuthenticationFilter.class);

        // 커스텀 필터 추가 ( addFilterBefore -> 어떤 특정 필터 앞, 즉 before 에 추가 )
        http // LogoutFilter 보다 앞에 등록
                .addFilterBefore(new JWTFilter(), LogoutFilter.class);

        // 세션 필터 설정 (STATELESS) (커스텀 세팅) - stateless 한 설정 세팅
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
