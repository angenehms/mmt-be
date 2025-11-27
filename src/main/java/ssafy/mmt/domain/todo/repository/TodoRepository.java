package ssafy.mmt.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.mmt.domain.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
