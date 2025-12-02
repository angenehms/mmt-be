package ssafy.mmt.domain.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.mmt.common.auth.CustomMemberPrincipal;
import ssafy.mmt.domain.todo.dto.request.TodoCreateRequest;
import ssafy.mmt.domain.todo.dto.request.TodoUpdateRequest;
import ssafy.mmt.domain.todo.dto.response.TodoSearchResponse;
import ssafy.mmt.domain.todo.entity.Todo;
import ssafy.mmt.domain.todo.repository.TodoRepository;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public List<TodoSearchResponse> getTodo(
            CustomMemberPrincipal customMemberPrincipal
    ) {
        Long memberId = customMemberPrincipal.getMemberId();

        List<Todo> rawList = todoRepository.findByMemberId((memberId));
        List<TodoSearchResponse> formattedList = new ArrayList<>();

        for(Todo curr : rawList) {
            formattedList.add(
                    new TodoSearchResponse(
                            curr.getTodoId(),
                            curr.getContent(),
                            curr.getIsDone()
                    )
            );
        }

        return formattedList;
    }

    @Transactional
    public void deleteTodo(
            CustomMemberPrincipal customMemberPrincipal,
            Long todoId
    ) {
        Long memberId = customMemberPrincipal.getMemberId();
        todoRepository.deleteTodoByMemberIdAndTodoId(memberId, todoId);
    }

    @Transactional
    public void updateTodo(
            TodoUpdateRequest tur,
            CustomMemberPrincipal customMemberPrincipal,
            Long todoId
    ) {
        Long memberId = customMemberPrincipal.getMemberId();
        Todo todo = todoRepository.findByMemberIdAndTodoId(memberId, todoId);

        todo.setContent(tur.content());
        todo.setIsDone(tur.isDone());

        todoRepository.save(todo);

    }

}
