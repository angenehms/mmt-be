package ssafy.mmt.domain.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.todo.dto.request.TodoCreateRequest;
import ssafy.mmt.domain.todo.entity.Todo;
import ssafy.mmt.domain.todo.repository.TodoRepository;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public void addTodo (
            TodoCreateRequest tcr,
            CustomMemberPrincipal customMemberPrincipal)
    {
        Long memberId = customMemberPrincipal.getMemberId();
        Todo newTodo = Todo.builder()
                .memberId(memberId)
                .isDone(false)
                .content(tcr.content())
                .build();
        todoRepository.save(newTodo);
    }


}
