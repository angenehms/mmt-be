package ssafy.mmt.domain.todo.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.domain.todo.entity.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByMemberId(@NotNull Long memberId);
    Optional<Todo> findByMemberIdAndTodoId(Long memberId, Long todoId);
}
