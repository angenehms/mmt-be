package ssafy.mmt.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // 스웨거 상단 정보 제공 (OAS)
    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("mmt API Documentation")
                        .description("mmt 프로젝트의 API 목록입니다.")
                        .version("1.0")
                )
                .servers(List.of(
                        // 1. 현재 접속한 도메인을 자동으로 따라가도록 설정 (배포 환경 대응)
                        new Server()
                                .url("/")
                                .description("현재 접속한 서버 (자동 인식)"),
                        // 2. 로컬에서 직접 테스트할 때 사용할 주소
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발용 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        ));
    }

    // 엔드포인트 버전별 대그룹화
    @Bean
    public GroupedOpenApi groupedOpenApiV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupedOpenApiV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/api/v2/**")
                .build();
    }
}