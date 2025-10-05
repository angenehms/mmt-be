package ssafy.mmt.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // MmtApplication 메인함수에 어노테이션 달아도 되는데 통합테스트에 문제가 될 수 있어 따로 config 파일 만들어 관리
public class JpaAuditingConfig {
}
