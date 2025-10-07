package ssafy.mmt.common.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import ssafy.mmt.common.auth.filter.LoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler) { // Qualifier 로 확실히 구분해 의존성 주입받기
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    // 커스텀 자체 로그인 필터를 위한 AuthenticationManager Bean 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 단방향(BCrypt) 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        // CSRF 보안 필터 disable (커스텀 세팅) - stateless 한 서버에서 필요 없기에 꺼주고
        http
                .csrf(AbstractHttpConfigurer::disable);

        // CORS 설정 (커스텀 세팅) - 리액트, 스프링과 같이 프론트, 백에 서로 다른 오리진을 가지는 경우에는 CORS 설정이 필수

        // 기본 Form 기반 인증 필터들 disable (커스텀 세팅) - multipart form 데이터 형태로 받지 않고 우리는 json body 기반으로 받을 거기 때문에
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // 기본 Basic 인증 필터 disable (커스텀 세팅) - httpBasic 기반의 로그인도 하지 않을 거기 때문에 꺼주고
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 인가 (커스텀 세팅) - 컨트롤러 api 에 대해 접근을 허용할건지 말건지 결정
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());

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
        // UsernamePasswordAuthenticationFilter.class 가 기준이 되는 필터고 그 앞에 LoginFilter 객체를 찍어서 배치함
        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);

        // 세션 필터 설정 (STATELESS) (커스텀 세팅) - stateless 한 설정 세팅
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}
