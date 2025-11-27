package ssafy.mmt.domain.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.mmt.domain.todo.repository.TodoRepository;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;


}
