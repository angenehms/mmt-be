package ssafy.mmt.domain.todo.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.domain.todo.entity.Todo;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByMemberId(@NotNull Long memberId);

    void deleteTodoByMemberIdAndTodoId(@NotNull Long memberId, Long todoId);

    Todo findByMemberIdAndTodoId(@NotNull Long memberId, Long todoId);
}
