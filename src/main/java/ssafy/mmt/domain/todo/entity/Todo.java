package ssafy.mmt.domain.todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "todos")
@Getter
@Builder
@NoArgsConstructor // 아무것도 없는 생성자
@AllArgsConstructor // 모든 것이 다 있는 생성자
public class Todo {

    // 주요키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    // 외래키
    @NotNull
    private Long memberId;

    @NotNull
    @Column(length = 30)
    private String content;

    @NotNull
    @Builder.Default
    private Boolean isDone = false;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
