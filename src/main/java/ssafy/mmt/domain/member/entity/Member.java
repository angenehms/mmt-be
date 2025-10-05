package ssafy.mmt.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // 수정일 생성일 자동 반영을 위함
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor // 아무것도 없는 생성자
@AllArgsConstructor // 모든 것이 다 있는 생성자
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    // socialProviderType 은 null 이면 안됨 왜냐면 소셜 로그인 아닐 경우 null 이 되어야하기 때문
    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role_type", nullable = false)
    private MemberRoleType roleType;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
