package ssafy.mmt.common.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssafy.mmt.common.auth.jwt.repository.RefreshTokenRepository;

import java.time.LocalDateTime;

// config 긴 한데 component 성격이 강하기 때문에 @Component을 붙임
@Component
public class ScheduleConfig {

    private final RefreshTokenRepository refreshTokenRepository;
    public ScheduleConfig(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Refresh 토큰 저장소 8일 지난 토큰 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8); // 8일이 지난 리프레시 토큰을 삭제
        refreshTokenRepository.deleteByCreatedDateBefore((cutoff));
    } // 지금은 repository 를 참조했지만 service 단을 참조해서 service 단에 지우는 메서드를 등록해도 좋을 거 같다
}
